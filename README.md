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

- **Основной источник:** `app/src/main/assets/soundfonts/Chorium.SF2` (взят из [Repetitor](https://github.com/free-creations/Repetitor), **Apache-2.0**; см. [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md)) — рендер через [fluidsynth-kmp](https://github.com/kotlinds/fluidsynth-kmp) (FluidSynth, **LGPL-2.1** для нативных библиотек; см. лицензию пакета на Maven / репозитории). При первом запуске файл копируется во внутреннее хранилище приложения.
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

## Build from scratch (терминал)

Минимальные требования:

- JDK `21`
- Android SDK (platform + build-tools, которые просит Android Studio/AGP)
- `ANDROID_HOME` (или `local.properties` с `sdk.dir=...`)

Проверка окружения:

```bash
java -version
echo $ANDROID_HOME
```

Сборка debug APK:

```bash
./gradlew :app:assembleDebug
```

Запуск unit-тестов:

```bash
./gradlew :app:testDebugUnitTest
```

APK после сборки:

`app/build/outputs/apk/debug/app-debug.apk`

## Сборка APK из терминала

Через Gradle Wrapper (рекомендуется):

```bash
./gradlew :app:assembleDebug
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
./gradlew :app:testDebugUnitTest
```

Instrumentation/UI тесты (нужно подключенное устройство или эмулятор):

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Лицензия

- Исходный код проекта: **MIT** — см. [`LICENSE`](LICENSE).
- Сторонние библиотеки и вложенные ассеты (SoundFont, семплы): см. [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md).

## Проверка стабильности аудио

См. чеклист: `QA_STABILIZATION.md`

## Crash-отчеты (вручную)

Если приложение не стартует, crash-файлы можно забрать вручную.

Где лежат отчеты:

`/data/data/com.muxaeji.intervalo/files/crash-reports/`

Для debug-сборки через ADB:

```bash
adb shell run-as com.muxaeji.intervalo ls files/crash-reports
adb exec-out run-as com.muxaeji.intervalo cat files/crash-reports/<имя_файла> > crash-report.txt
```

Если приложение запускается, можно отправить последний отчет кнопкой
`Отправить crash-отчет` на стартовом экране.
