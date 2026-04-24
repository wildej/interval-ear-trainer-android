# Third-party materials

This app links to or bundles third-party components and assets. Their licenses are **not** replaced by the project’s own `LICENSE` (MIT).

| Component | License | Notes |
|-----------|---------|--------|
| [fluidsynth-kmp](https://github.com/kotlinds/fluidsynth-kmp) (wrapper) | Apache-2.0 | Kotlin wrapper code. |
| FluidSynth (native, via fluidsynth-kmp) | [LGPL-2.1](https://github.com/Fluidsynth/fluidsynth/blob/master/LICENSE) | Dynamically linked on Android. |
| **Chorium** (GM SoundFont), `assets/soundfonts/Chorium.SF2` | *Depends on the exact build* (see below) | Community GM bank; not a single global license. |
| Freesound pack *piano notes* (Teddy_Frost) in `assets/audio/piano_teddy/` | CC0 (per pack) | See `LICENSE_README.txt` in that folder. |

### Chorium.SF2

**Chorium** is a family of General MIDI SoundFonts; file names and sizes differ, and **uploader / revision defines the license**.

- Builds in the **Chorium RevA** lineage (including some community “modded RevA” uploads) are often redistributed under **[CC BY 3.0](https://creativecommons.org/licenses/by/3.0/)** — attribution to the uploader/author and a link to the license are required when you ship the file. Example listing: [ChoriumRevA modded… (Musical Artifacts)](https://musical-artifacts.com/artifacts/2069) (stated as CC BY 3.0 on that page).
- A fork named **Chorium RevB** is listed on [Musical Artifacts](https://musical-artifacts.com/artifacts/1474) as **non-free** (“All rights reserved” / not an open license on that page), with a note that third-party material may be subject to rights of Roland, Yamaha, and others. **Do not assume** a generic `Chorium.SF2` from the internet is CC BY — check the same archive/readme you downloaded.

If your bundled `Chorium.SF2` matches a **CC BY 3.0** build, a minimal attribution in app credits or docs is: name of the soundfont, author/uploader as given at the source, and that it is under CC BY 3.0 with a link to the deed above.

If you add a new SoundFont or sample pack, document its license here.
