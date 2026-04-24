# Intervalo (Android MVP)

Android-приложение для тренировки распознавания музыкальных интервалов на слух.

## Что уже реализовано

- Выбор произвольного набора интервалов перед стартом тренировки.
- Генерация случайного вопроса:
  - случайная базовая нота;
  - случайный интервал из выбранного набора.
- Воспроизведение интервала:
  - сначала две ноты вместе;
  - затем по очереди.
- Экран ответа: выбор интервала сразу даёт обратную связь «верно/неверно».
- Кнопка повторного прослушивания.
- Экран итогов тренировки: `верно/всего`, точность в процентах.
- Базовые unit и UI тесты.

## Аудио (SoundFont и семплы)

- **Основной источник:** `app/src/main/assets/soundfonts/Chorium.SF2` — рендер через [fluidsynth-kmp](https://github.com/kotlinds/fluidsynth-kmp) (FluidSynth, **LGPL-2.1** для нативных библиотек; см. лицензию пакета на Maven / репозитории). При первом запуске файл копируется во внутреннее хранилище приложения.
- **Запасной путь:** `app/src/main/assets/audio/piano_teddy/` (WAV, Teddy_Frost, CC0) — `MultiSampleIntervalAudioPlayer`.  
- **Ещё запасной:** `app/src/main/assets/audio/piano_ref.wav` — один WAV + ресемплинг.  
- Если ничего из этого нет — синтезатор (синус).

## Тайминг воспроизведения

- Всё в `IntervalPlaybackTiming`.  
- Поле **`arpeggioGapMs`**: пауза между двумя нотами арпеджио; **отрицательное значение** — перекрытие (вторая нота начинается до окончания первой).

## Технологии

- Kotlin
- Jetpack Compose
- Navigation Compose
- MVVM

## Быстрый запуск (Android Studio)

1. Открыть проект в Android Studio.
2. Дождаться Gradle Sync.
3. Подключить устройство Android (USB debugging) или запустить эмулятор.
4. Нажать `Run`.

## Сборка APK из терминала

Если на машине установлен Gradle:

```bash
gradle assembleDebug
```

После сборки APK обычно находится в:

`app/build/outputs/apk/debug/app-debug.apk`

## Установка APK на телефон (ADB)

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Тесты

Unit-тесты:

```bash
gradle testDebugUnitTest
```

Instrumentation/UI тесты (нужно подключенное устройство или эмулятор):

```bash
gradle connectedDebugAndroidTest
```

## Проверка стабильности аудио

См. чеклист: `QA_STABILIZATION.md`
