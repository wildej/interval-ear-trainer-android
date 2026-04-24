# Stabilization checklist (MVP)

Manual checks to run on at least 2 Android devices (or emulator + device):

1. Launch app, select 4+ intervals, start session.
2. Tap "Слушать / Еще раз" repeatedly; app must not crash or overlap unstable playback.
3. Run 20+ questions and verify:
   - feedback appears right after tapping an interval answer;
   - "Дальше" opens a new random question;
   - stats increments correctly.
4. Finish training and verify summary numbers and accuracy percentage.
5. Return to setup and verify interval selection persists as expected.

Known technical guardrails added in code:
- Playback re-entry is blocked while audio is currently playing.
- Question generation keeps top note within configured MIDI range.
