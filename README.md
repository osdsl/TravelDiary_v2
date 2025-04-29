# Инcтрукция по сборке APK-файла через командную строку

Эта инструкция описывает процесс сборки **debug APK** (для тестирования)  для Android-приложения с использованием интерфейса командной строки (CLI) через Gradle.
## Предварительные требования

1. **Необходимые инструменты**:
   - Android Studio (включает Gradle и Android SDK).
   - JDK (Java Development Kit), совместимый с проектом (обычно Java 11 или 17).
   - Настроенные переменные окружения:
     - `JAVA_HOME` — путь к папке JDK.
     - `ANDROID_HOME` или `ANDROID_SDK_ROOT` — путь к Android SDK (например, `~/Library/Android/sdk` на macOS или `C:\Users\<Ваше_Имя>\AppData\Local\Android\Sdk` на Windows).
     - `<Android_SDK>/platform-tools` добавлен в `PATH` для доступа к `adb`.

2. **Проект**:
   - Убедитесь, что проект Android содержит корректный файл `build.gradle` (в папке модуля, обычно `app`).
   - Проверьте, что проект компилируется без ошибок в Android Studio (`Build > Rebuild Project`).


## 1. Сборка Debug APK

### Шаги

1. **Откройте терминал**:
   - Windows: Command Prompt, PowerShell или терминал Android Studio (`View > Tool Windows > Terminal`).

2. **Перейдите в корневую папку проекта**:
   ```bash
   cd ~/AndroidStudioProjects/TravelDairy
   ```

3. **Выполните команду сборки**:
   ```bash
   ./gradlew assembleDebug
   ```
   - На Windows:
     ```bash
     gradlew assembleDebug
     ```
4. **Найдите APK**:
   - APK находится в:
     ```
     app/build/outputs/apk/debug/app-debug.apk
     ```
   - Перейдите в папку:
     ```bash
     cd app/build/outputs/apk/debug
     ls  # или dir на Windows
     ```

5. **Тестирование**:
   - Установите APK на устройство или эмулятор:
     ```bash
     adb install app/build/outputs/apk/debug/app-debug.apk
     ```
     - Убедитесь, что устройство подключено (`adb devices`).
     - Включите установку из неизвестных источников на устройстве.
   - Или перенесите APK на устройство через USB/облако и установите вручную.
