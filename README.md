# Interval Ear Trainer (Android MVP)

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
