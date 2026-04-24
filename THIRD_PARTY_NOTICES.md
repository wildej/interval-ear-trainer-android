# Third-party materials

This app links to or bundles third-party components and assets. Their licenses are **not** replaced by the project’s own `LICENSE` (MIT).

| Component | License | Notes |
|-----------|---------|--------|
| [fluidsynth-kmp](https://github.com/kotlinds/fluidsynth-kmp) (wrapper) | Apache-2.0 | Kotlin wrapper code. |
| FluidSynth (native, via fluidsynth-kmp) | [LGPL-2.1](https://github.com/Fluidsynth/fluidsynth/blob/master/LICENSE) | Dynamically linked on Android. |
| **Chorium** (GM SoundFont), `assets/soundfonts/Chorium.SF2` | [Apache-2.0](https://github.com/free-creations/Repetitor/blob/master/LICENSE-2.0.txt) | Vendored from the [Repetitor](https://github.com/free-creations/Repetitor) project; see [Chorium.SF2](#choriumsf2) below. |
| Freesound pack *piano notes* (Teddy_Frost) in `assets/audio/piano_teddy/` | CC0 (per pack) | See `LICENSE_README.txt` in that folder. |

### Chorium.SF2

**Source in this app:** the binary matches the copy shipped as a test resource in [free-creations/Repetitor](https://github.com/free-creations/Repetitor):  
[github.com/…/resources/Chorium.SF2](https://github.com/free-creations/Repetitor/blob/master/MidiSong/test/unit/src/de/free_creations/midisong/resources/Chorium.SF2) (path on `master` as of this documentation).

**License:** that repository is released under the **Apache License, Version 2.0** ([`LICENSE-2.0.txt`](https://github.com/free-creations/Repetitor/blob/master/LICENSE-2.0.txt) in the same repo). Follow Apache-2.0 terms when redistributing (including the SoundFont) — for example, retain any required **NOTICE**/attributions the upstream project or dependencies expect if you add them.

**Sound bank name** embedded in the file (asserted in upstream): `Chorium by openwrld` (see e.g. `SongTest` in the same [MidiSong](https://github.com/free-creations/Repetitor/tree/master/MidiSong) module).

*Other* `Chorium` builds on the public web may still be under different terms. If you replace this file, document the new source and license here.

If you add a new SoundFont or sample pack, document its license here.
