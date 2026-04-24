# Product backlog

## Priority 2

### Audio

1. **Sample-based playback (done — MVP)**  
   - Bundled reference WAV: `app/src/main/assets/audio/piano_ref.wav`  
   - Runtime pitch via resampling (`AssetSampleIntervalAudioPlayer`); fallback to sine if asset missing.  
   - **Next (still P2):** proper piano multisamples (e.g. one sample per octave or per note) and/or SoundFont to reduce resampling artifacts far from the reference pitch.

2. **Playback timing (done — config)**  
   - Centralized in `IntervalPlaybackTiming` (tone length, pause chord→arpeggio, pause between arpeggio notes).  
   - **Next:** tune values after testing on more devices; optional in-app advanced settings later.
