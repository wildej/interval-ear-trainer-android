# Intervalo (Android MVP)

Android-приложение для тренировки распознавания музыкальных интервалов на слух.

## Что уже реализовано

- Выбор произвольного набора интервалов перед стартом сессии.
- Генерация случайного вопроса:
  - случайная базовая нота;
  - случайный интервал из выбранного набора.
- Воспроизведение интервала:
  - сначала две ноты вместе;
  - затем по очереди.
- Экран ответа с проверкой "верно/неверно".
- Кнопка повторного прослушивания.
- Экран итогов сессии: `верно/всего`, точность в процентах.
- Базовые unit и UI тесты.

## Аудио (семплы)

- **Основной набор:** `app/src/main/assets/audio/piano_teddy/` — ноты из пака Freesound *piano notes* (Teddy_Frost, CC0), см. `LICENSE_README.txt` в этой папке.  
- Плеер: `MultiSampleIntervalAudioPlayer` — для каждой MIDI-ноты берётся **ближайший** доступный семпл из пака и при необходимости чуть подстраивается по частоте.  
- **Запасной вариант:** `app/src/main/assets/audio/piano_ref.wav` (одна опорная нота + ресемплинг), если папка с Teddy-семплами недоступна.  
- Если нет и этого файла — синтезатор (синус).

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
