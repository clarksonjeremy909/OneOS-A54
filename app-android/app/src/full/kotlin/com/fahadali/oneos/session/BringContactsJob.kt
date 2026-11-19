package com.fahadali.oneos.session

import android.content.Context
import com.fahadali.oneos.core.root.RootScript

/**
 * One-way copy of One UI contacts into the prepared guest (vCard in the stage).
 * Never deletes contacts on either side. Import happens at the next switch.
 */
object BringContactsJob : BackgroundJob<BringContactsJob.State>(State(), "oneos-bring-contacts") {
    data class State(
        val busy: Boolean = false,
        val message: String? = null,
        val ok: Boolean = false,
    )

    fun start(ctx: Context) {
        if (state.busy) return
        val app = ctx.applicationContext
        update(State(busy = true, message = null, ok = false))
        executor.execute {
            val r = runCatching { copy(app) }.getOrElse { Outcome(false, it.message ?: it.javaClass.simpleName) }
            update(State(busy = false, message = r.message, ok = r.ok))
        }
    }

    private class Outcome(val ok: Boolean, val message: String)

    private fun copy(app: Context): Outcome {
        val reply = RootVerbs.run(app, RootScript.Verb.BRING_CONTACTS, emptyList(), silenceMs = 5 * 60_000L)
        val raw = reply.reply.ok ?: reply.problem ?: "bring-contacts failed"
        return Outcome(reply.ok, raw)
    }
}
