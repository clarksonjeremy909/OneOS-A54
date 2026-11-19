package com.fahadali.oneos.session

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.fahadali.oneos.core.root.RootScript
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Saves a short logcat/dmesg excerpt to the public Download folder so it can
 * be shared. Root writes only under the stage; the app copies the streamed
 * text through MediaStore.
 */
object BugLogJob : BackgroundJob<BugLogJob.State>(State(), "oneos-bug-log") {
    data class State(
        val busy: Boolean = false,
        val message: String? = null,
        val fileName: String? = null,
        val ok: Boolean = false,
    )

    fun start(ctx: Context) {
        if (state.busy) return
        val app = ctx.applicationContext
        update(State(busy = true, message = null, fileName = null, ok = false))
        executor.execute {
            val r = runCatching { collect(app) }.getOrElse { Outcome(false, it.message ?: it.javaClass.simpleName, null) }
            update(State(busy = false, message = r.message, fileName = r.fileName, ok = r.ok))
        }
    }

    private class Outcome(val ok: Boolean, val message: String, val fileName: String?)

    private fun collect(app: Context): Outcome {
        val reply = RootVerbs.run(app, RootScript.Verb.BUG_LOG, emptyList(), silenceMs = 60_000L, totalMs = 120_000L)
        if (!reply.ok) return Outcome(false, reply.problem ?: "root did not write a log", null)
        val text = reply.reply.results
            .filter { it.startsWith("buglog ") }
            .joinToString("\n") { it.removePrefix("buglog ") }
            .ifBlank { reply.reply.ok ?: "empty log" }
        val name = "OneOS-hata-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date()) + ".txt"
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = app.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return Outcome(false, "could not create a Download file", null)
        app.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray()) }
            ?: return Outcome(false, "could not write the Download file", null)
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        app.contentResolver.update(uri, values, null, null)
        return Outcome(true, name, name)
    }
}
