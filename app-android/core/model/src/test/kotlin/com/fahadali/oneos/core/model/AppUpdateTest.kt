package com.fahadali.oneos.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdateTest {
    private val sha = "a".repeat(64)
    private fun json(
        code: String = "58",
        name: String = "\"0.4.42\"",
        apk: String = "\"https://github.com/fahadalijaved/OneOS/releases/download/v0.4.42/OneOS-0.4.42.apk\"",
        bytes: String = "52428800",
        sha256: String = "\"$sha\"",
    ) = """{"versionCode": $code, "versionName": $name, "apk": $apk, "bytes": $bytes, "sha256": $sha256}"""

    @Test
    fun readsTheReleasesUpdate() {
        val u = AppUpdate.parse(json()).getOrThrow()
        assertEquals(AppUpdate(58, "0.4.42", "https://github.com/fahadalijaved/OneOS/releases/download/v0.4.42/OneOS-0.4.42.apk", 52428800L, sha), u)
        assertTrue(u.newerThan(57))
        assertTrue(!u.newerThan(58))
    }

    @Test
    fun notesAndSystemsAreOptional() {
        val plain = AppUpdate.parse(json()).getOrThrow()
        assertEquals(null, plain.notes)
        assertTrue(plain.systems.isEmpty() && !plain.changes("coloros17-findn6"))
        val extra = json().removeSuffix("}") + ", \"notes\": \"Theme Store on the Fold8 ColorOS\", \"systems\": \"coloros17-findn6,hyperos-lhasa\"}"
        val u = AppUpdate.parse(extra).getOrThrow()
        assertEquals("Theme Store on the Fold8 ColorOS", u.notes)
        assertTrue(u.changes("coloros17-findn6") && u.changes("hyperos-lhasa") && !u.changes("pixel11fold-yogi"))
        assertTrue(AppUpdate.parse(json().removeSuffix("}") + ", \"systems\": \"*\"}").getOrThrow().changes("pixel11fold-yogi"))
        assertTrue(AppUpdate.parse(json().removeSuffix("}") + ", \"systems\": \"../x\"}").isFailure)
        val longNotes = AppUpdate.parse(json().removeSuffix("}") + ", \"notes\": \"" + "x".repeat(141) + "\"}").getOrThrow().notes!!
        assertEquals(AppUpdate.NOTES_SHOWN, longNotes.length)
        assertTrue(longNotes.endsWith("\u2026"))
        assertEquals("x".repeat(140), AppUpdate.parse(json().removeSuffix("}") + ", \"notes\": \"" + "x".repeat(140) + "\"}").getOrThrow().notes)
    }

    @Test
    fun driveApksOnlyOnTheTestChannel() {
        val drive = "\"https://drive.usercontent.google.com/download?id=1AbCdEfGhIjKlMnOpQrStUvWxYz012345&export=download&confirm=t\""
        assertTrue(AppUpdate.parse(json(apk = drive)).isFailure)
        assertTrue(AppUpdate.parse(json(apk = drive), test = true).isSuccess)
        assertTrue(AppUpdate.parse(json(apk = "\"https://drive.usercontent.google.com/download?id=x&export=download&confirm=t\""), test = true).isFailure)
        assertTrue(AppUpdate.parse(json(apk = "\"https://evil.example/download?id=1AbCdEfGhIjKlMnOpQrStUvWxYz012345&export=download&confirm=t\""), test = true).isFailure)
        assertTrue(AppUpdate.parse(json(), test = true).isSuccess)
    }

    @Test
    fun refusesAnythingElse() {
        val bad = listOf(
            json(apk = "\"https://example.com/OneOS-0.4.42.apk\""),
            json(apk = "\"https://github.com/fahadalijaved/OneOS/releases/download/v0.4.42/../../x.apk\""),
            json(apk = "\"https://github.com/someone/OneOS/releases/download/v0.4.42/OneOS-0.4.42.apk\""),
            json(code = "0"),
            json(code = "\"58\""),
            json(name = "\"0.4.42; rm\""),
            json(bytes = "0"),
            json(bytes = "999999999999"),
            json(sha256 = "\"ABC\""),
            json() + json(),
            """{"versionCode": 58}""",
        )
        for (b in bad) assertTrue(b, AppUpdate.parse(b).isFailure)
    }

    @Test
    fun readsTheA54VersionJsonPerChannel() {
        val trApk = "https://github.com/clarksonjeremy909/OneOS-A54/releases/download/v0.5.23-a54.1/OneOS-A54-0.5.23-tr.apk"
        val enApk = "https://github.com/clarksonjeremy909/OneOS-A54/releases/download/v0.5.23-a54.1/OneOS-A54-0.5.23-en.apk"
        val text = """{"versionCode": 167, "versionName": "0.5.23.1", "notes": "A54 updater", "apkTr": "$trApk", "bytesTr": 81562844, "sha256Tr": "$sha", "apkEn": "$enApk", "bytesEn": 81562845, "sha256En": "$sha"}"""
        val tr = AppUpdate.parse(text, channel = "tr").getOrThrow()
        assertEquals(167, tr.versionCode)
        assertEquals("0.5.23.1", tr.versionName)
        assertEquals(trApk, tr.apk)
        assertEquals(81562844L, tr.bytes)
        assertEquals("A54 updater", tr.notes)
        val en = AppUpdate.parse(text, channel = "en").getOrThrow()
        assertEquals(enApk, en.apk)
        assertEquals(81562845L, en.bytes)
        assertTrue(tr.newerThan(166))
        assertTrue(!tr.newerThan(167))
    }

    @Test
    fun refusesA54ApksFromAnotherRepo() {
        val apk = "\"https://github.com/someone/OneOS-A54/releases/download/v0.5.23-a54.1/OneOS-A54-0.5.23-tr.apk\""
        assertTrue(AppUpdate.parse(json(apk = apk)).isFailure)
    }
}
