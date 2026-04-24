# Product backlog

## Idea

1. Нужно добавить еще один режим, назовем его игровой: на экране два столбца. В левом несколько кнопок "прослушать интервал", лучше всего если на них будет графическое изображение записи. В правом - соответствующее им количество кнопок с названием интервалов. Порядок перемешанный случайно.
Задача игрока по очереди нажимая кнопки справа и слева выбирать соответствующие прослушанному интервалу
При ошибке оповещать об этом, и давать пользователю снова выбрать кнопку в правом столбце (левый считаем уже выбранным)

## Priority 2

### Audio

1. **Sample-based playback (done — multisample pack)**  
   - Teddy_Frost Freesound pack (CC0) in `app/src/main/assets/audio/piano_teddy/` + `LICENSE_README.txt`.  
   - `MultiSampleIntervalAudioPlayer`: nearest sample + small pitch correction; fallback chain in `IntervalAudioPlayerProvider` (Teddy → `piano_ref` → sine).  
   - **Next (P2):** extend coverage (more octaves / chromatic set) for fewer pitch-shifts at range extremes; optional SoundFont.

2. **Playback timing (done — config + overlap)**  
   - `IntervalPlaybackTiming`: `arpeggioGapMs` — positive silence, **negative overlap** between arpeggio notes (see `ArpeggioMixer`).  
   - **Next:** tune defaults per device; optional in-app advanced settings later.

## Priority 3
1. **поправить термины**
   - слово "сессия" звучит слишком официально, лучше заменить его
   - в сообщении об ошибке нужно писать и полное название интервала, не только сокращенное
