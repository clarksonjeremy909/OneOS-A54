package com.fahadali.oneos.session

import android.content.Context
import com.fahadali.oneos.R

/**
 * Bring apps data transfer (OneOS A54): the owner's ticks for media folders
 * and APKs. Folder categories are applied as the official share bind
 * (guest_start.sh /data/oneos_share.dirs); APKs are a one-way copy of `*.apk`
 * from the host Download and Documents folders into the guest Download folder.
 * Host files are never moved or deleted.
 */
object BringMedia {
    private const val PREFS = "oneos_bring_media"
    private const val KEY_SELECTED = "selected"
    private const val KEY_APPLIED = "applied"

    data class Category(
        val id: String,
        val title: Int,
        val summary: Int,
    )

    val CATEGORIES: List<Category> = listOf(
        Category("photos", R.string.bring_media_photos, R.string.bring_media_photos_summary),
        Category("videos", R.string.bring_media_videos, R.string.bring_media_videos_summary),
        Category("downloads", R.string.bring_media_downloads, R.string.bring_media_downloads_summary),
        Category("documents", R.string.bring_media_documents, R.string.bring_media_documents_summary),
        Category("music", R.string.bring_media_music, R.string.bring_media_music_summary),
        Category("apks", R.string.bring_media_apks, R.string.bring_media_apks_summary),
    )

    val IDS: Set<String> = CATEGORIES.map { it.id }.toSet()

    private fun p(ctx: Context) = ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun selected(ctx: Context): Set<String> =
        p(ctx).getStringSet(KEY_SELECTED, emptySet())?.filter { it in IDS }?.toSet() ?: emptySet()

    fun setSelected(ctx: Context, ids: Set<String>) {
        p(ctx).edit().putStringSet(KEY_SELECTED, ids.filter { it in IDS }.toSet()).apply()
    }

    fun toggle(ctx: Context, id: String, on: Boolean) {
        if (id !in IDS) return
        val next = selected(ctx).toMutableSet()
        if (on) next += id else next -= id
        setSelected(ctx, next)
    }

    fun setAll(ctx: Context, on: Boolean) {
        setSelected(ctx, if (on) IDS else emptySet())
    }

    fun applied(ctx: Context): Set<String> =
        p(ctx).getStringSet(KEY_APPLIED, emptySet())?.filter { it in IDS }?.toSet() ?: emptySet()

    fun setApplied(ctx: Context, ids: Set<String>) {
        p(ctx).edit().putStringSet(KEY_APPLIED, ids.filter { it in IDS }.toSet()).apply()
    }
}
