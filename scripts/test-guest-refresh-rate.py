#!/usr/bin/env python3
"""The switch pins the guest only to a refresh rate its host panel supports."""
from pathlib import Path
import os
import shutil
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[1]
SOURCE = (ROOT / "switcher/assets/oneos_switch.sh").read_text()
BLOCK = SOURCE.split("GUEST_REFRESH=120.0\n", 1)[1].split(
    '# ONEOS_GUEST_POSTURE=live:', 1
)[0]
SH = shutil.which("sh")
if not SH and os.name == "nt":
    candidate = Path(os.environ.get("ProgramFiles", r"C:\Program Files")) / "Git/usr/bin/sh.exe"
    if candidate.is_file():
        SH = str(candidate)


class GuestRefreshRate(unittest.TestCase):
    def rate(self, text, want=None):
        if not SH:
            self.skipTest("a POSIX shell is not installed")
        with tempfile.TemporaryDirectory() as td:
            stage = Path(td)
            (stage / "display_host.txt").write_text(text)
            script = "GUEST_REFRESH=120.0\n" + BLOCK + "printf '%s' \"$GUEST_REFRESH\"\n"
            env = {"STAGE": str(stage)}
            if want is not None:
                env["ONEOS_GUEST_REFRESH"] = want
            run = subprocess.run(
                [SH, "-c", script], env=env,
                capture_output=True, text=True, check=True,
            )
            return run.stdout.strip().splitlines()[-1]

    def test_panel_maximum_is_used(self):
        for rates, expected in [
            ([60], "60.0"),
            ([90, 60], "90.0"),
            ([120, 60, 90], "120.0"),
            ([144, 120, 60], "120.0"),
        ]:
            modes = ", ".join(f"{{id={i}, fps={rate}.0, vsync={rate}.0}}" for i, rate in enumerate(rates))
            with self.subTest(rates=rates):
                self.assertEqual(expected, self.rate("supportedModes [" + modes + "]\n"))

    def test_bad_or_missing_dump_keeps_safe_default(self):
        for dump in ["", "fps=0.0 fps=29.0 fps=240.0\n", "not a display dump\n"]:
            with self.subTest(dump=dump):
                self.assertEqual("120.0", self.rate(dump))

    def test_activator_uses_detected_rate_for_both_bounds(self):
        self.assertNotIn("put system peak_refresh_rate 120.0", SOURCE)
        self.assertRegex(SOURCE, r"put system peak_refresh_rate \$GUEST_REFRESH")
        self.assertRegex(SOURCE, r"put system min_refresh_rate \$GUEST_REFRESH")

    def test_user_pick_uses_a_physical_mode_only(self):
        dump = "supportedModes [{id=0, fps=60.0, vsync=60.0}, {id=1, fps=120.0, vsync=120.0}]\n"
        self.assertEqual("60.0", self.rate(dump, want="60"))
        self.assertEqual("120.0", self.rate(dump, want="120"))
        self.assertEqual("120.0", self.rate(dump, want="auto"))
        # 90 Hz is not in this dump: keep the panel maximum, do not invent it.
        self.assertEqual("120.0", self.rate(dump, want="90"))



if __name__ == "__main__":
    unittest.main()
