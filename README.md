# MacKey — Wireless Bluetooth HID Keyboard for macOS

**MacKey** turns your Android smartphone into an authentic wireless Bluetooth hardware keyboard designed exclusively for your MacBook or Mac desktop.

It communicates with macOS directly over native **Bluetooth HID (Human Interface Device)** protocols — the exact same standard used by Apple's Magic Keyboard.

---

## ⚡ Highlights

- **100% Free & Open Source**: No subscriptions, no ads, no paid APIs, no cloud dependencies.
- **Pure Bluetooth HID**: Connects directly to macOS over Bluetooth. No Wi-Fi, internet connection, or server software on your Mac required.
- **Mac-Specific Layout**: Dedicated keys for **Command (⌘)**, **Option (⌥)**, **Control (⌃)**, **Shift (⇧)**, **Esc**, **Return**, **Tab**, **Delete**, and Arrow keys.
- **Modifier Key Latching & Locking**: Single-tap a modifier (⌘, ⌥, ⌃) to latch it for the next keypress (e.g. ⌘ then C); double-tap to lock it.
- **One-Tap macOS Shortcut Toolbar**: Instant buttons for ⌘C, ⌘V, ⌘X, ⌘Z, ⌘A, ⌘S, ⌘F, ⌘W, ⌘Tab, and ⌘Space (Spotlight). Add and edit custom shortcuts.
- **Text Input Mode**: Type or paste long paragraphs, URLs, or code using your phone's native virtual keyboard, swipe typing, or voice dictation, and stream it sequentially to macOS with zero character collisions.
- **Zero-Storage Privacy**: Typed text is processed entirely in volatile memory and immediately discarded. Never logged or saved to disk.
- **Apple Minimalist UI**: Dark mode by default, light mode option, haptic feedback, and responsive key sizing.

---

## 📁 Project Structure

```
sofware/
├── app/
│   ├── build.gradle.kts                      # Module build configuration (Compose, SDK 35, MinSdk 28)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml               # Bluetooth HID permissions & activity definitions
│       ├── java/com/mackey/app/
│       │   ├── MainActivity.kt               # Permission checks & Compose lifecycle
│       │   ├── bluetooth/
│       │   │   ├── HidConstants.kt           # macOS-compliant HID Report Descriptor & Keycodes
│       │   │   ├── HidDeviceManager.kt       # BluetoothHidDevice proxy, SDP registration & report dispatch
│       │   │   ├── ConnectionState.kt        # StateFlow representation of Bluetooth link
│       │   │   └── MacKeyMapper.kt           # ASCII-to-HID translator & sequential text streamer
│       │   ├── model/
│       │   │   ├── KeyModel.kt               # Key representations & modifier state models
│       │   │   └── ShortcutItem.kt           # Configurable Mac shortcut items
│       │   ├── viewmodel/
│       │   │   ├── KeyboardViewModel.kt      # Keyboard logic, modifier latching, text streaming
│       │   │   └── SettingsViewModel.kt      # App theme, haptics, key scaling, custom shortcuts
│       │   └── ui/
│       │       ├── theme/                    # Apple-inspired Dark & Light palettes & typography
│       │       ├── components/
│       │       │   ├── KeyButton.kt          # Rounded key component with tactile animations
│       │       │   ├── ModifierKeyButton.kt  # ⌘, ⌥, ⌃ indicator (Latched / Locked states)
│       │       │   ├── ShortcutBar.kt        # Quick Mac shortcut strip
│       │       │   └── ConnectionBadge.kt    # Top status banner
│       │       └── screens/
│       │           ├── MainScreen.kt         # Bottom navigation container
│       │           ├── KeyboardScreen.kt     # Full interactive Mac keyboard
│       │           ├── TextInputScreen.kt    # Dedicated batch text entry mode
│       │           ├── ShortcutsScreen.kt    # Shortcut manager & custom shortcut creator
│       │           ├── ConnectionScreen.kt   # Bluetooth status, Mac pairing guide & troubleshoot
│       │           └── SettingsScreen.kt     # Theme, haptic strength, key size sliders
│       └── res/                              # Adaptive icons, colors, themes, and strings
├── build.gradle.kts                          # Top-level Gradle configuration (AGP 8.5.1, Kotlin 2.0.0)
├── settings.gradle.kts                       # Project name & repository settings
├── gradle.properties                         # JVM arguments & AndroidX flags
└── gradlew                                   # Unix Gradle wrapper launcher
```

---

## 🛠️ Step-by-Step Installation Guide

### Step 1: Install Android Studio on your Mac

1. Visit the official Android Studio download page: [developer.android.com/studio](https://developer.android.com/studio).
2. Download the installer for your Mac architecture:
   - **Mac with Apple Silicon (M1/M2/M3/M4)**: Select the **"Mac with Apple chip"** (`.dmg`) option.
   - **Mac with Intel**: Select the **"Mac with Intel chip"** (`.dmg`) option.
3. Open the downloaded `.dmg` file and drag **Android Studio** into your **Applications** folder.
4. Launch Android Studio and complete the initial setup wizard (Standard setup is recommended; it automatically downloads the latest Android SDK and build tools).

### Step 2: Open the Project in Android Studio

1. Open Android Studio.
2. On the welcome screen, click **Open** (or go to **File > Open…**).
3. Navigate to and select this project directory:
   `/Users/apple/Desktop/sofware`
4. Click **Open**. Android Studio will initialize the project and sync Gradle dependencies automatically.

### Step 3: Enable Developer Options & USB Debugging on Your Android Phone

1. On your Android phone, open **Settings**.
2. Go to **About Phone** (or **About Device**).
3. Find **Build Number** and tap it **7 times** until you see the message: *"You are now a developer!"*
4. Go back to **Settings > System > Developer Options** (or **Additional Settings > Developer Options**).
5. Toggle **USB Debugging** to **ON**.

### Step 4: Build and Run the App on Your Phone

1. Connect your Android phone to your MacBook using a USB cable.
2. A prompt will appear on your phone asking *"Allow USB debugging?"* — Check **Always allow** and tap **Allow**.
3. In Android Studio, look at the top toolbar device selector. Your phone model will appear in the dropdown.
4. Click the green **Run (▶)** button (or press `Control + R`).
5. Android Studio will compile the app and install `MacKey` directly onto your smartphone.

*(Alternatively, to generate a standalone APK: In Android Studio, go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**. The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk` which you can send to your phone).*

---

## 🔗 How to Pair with macOS

1. On your MacBook, open **System Settings > Bluetooth** (or click the Bluetooth icon in the Mac menu bar).
2. Open the **MacKey** app on your phone.
3. Grant the requested Bluetooth permissions when prompted.
4. In MacKey, switch to the **Connect** tab (or tap the status banner at the top).
5. Tap the **"Make Discoverable"** button. Your phone will become visible to nearby Bluetooth hosts for 300 seconds.
6. On your Mac, look under **Nearby Devices** in the Bluetooth settings window. You will see **"MacKey Keyboard"** (or your phone's Bluetooth name).
7. Click **Connect** next to the device on your Mac.
8. A 6-digit Bluetooth pairing PIN dialog will appear on both your Mac screen and your phone screen. Confirm that the numbers match and click **Pair / Allow** on both devices.
9. macOS will now recognize your phone as an external Bluetooth keyboard.

> **Note on Keyboard Setup Assistant**:
> The first time you connect, macOS may open the "Keyboard Setup Assistant" asking you to press the key next to the Left Shift key. Tap the key **Z** on the phone screen. When asked for the keyboard type, select **ANSI (Standard US keyboard)** and click **Done**.

---

## ⌨️ How to Use the Keyboard

- **Standard Typing**: Tap any letter, number, or punctuation key to type immediately onto your Mac.
- **Mac Modifier Keys**:
  - Tap **⌘ Command** once to latch it (indicated by a blue outline). Now tap **C** to trigger `⌘ + C`. The modifier automatically unlatches after the keystroke.
  - Double-tap **⌘ Command** to lock it continuously (indicated by solid blue). Tap again to unlock.
  - Same behavior applies to **⌥ Option**, **⌃ Control**, and **⇧ Shift**.
- **Quick Shortcuts**:
  - Use the shortcut strip above the keyboard for instant one-tap actions: `⌘C` (Copy), `⌘V` (Paste), `⌘Z` (Undo), `⌘A` (Select All), `⌘S` (Save), `⌘W` (Close Tab/Window), `⌘Tab` (Switch Apps), and `⌘Space` (Spotlight).
- **Function / Media Keys**:
  - Tap the **fn** key on the keyboard to display Mac hardware media controls: Brightness Down/Up, Play/Pause, Skip, Mute, and Volume Down/Up.
- **Text Input Mode**:
  - Switch to the **Text** tab.
  - Type or paste lengthy paragraphs, links, or code snippets using your phone's virtual keyboard, predictive text, or voice typing.
  - Click **"Send to Mac ⏎"** to stream the text cleanly into whatever application is currently active on your MacBook.

---

## 🔧 Bluetooth Troubleshooting

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| **Mac shows "Connection Failed"** | Old Bluetooth cache or bond mismatch | In macOS System Settings > Bluetooth, click the **(i)** next to the phone and select **Forget This Device**. In Android Bluetooth settings, unpair the Mac. Then re-pair. |
| **Phone shows "HID Unsupported"** | Phone manufacturer disabled peripheral role | Some low-end chipsets or aggressive vendor ROMs omit `BluetoothHidDevice` from their Bluetooth HAL. Ensure your phone runs Android 9+ on official firmware (Google Pixel, Samsung Galaxy, OnePlus, Xiaomi). |
| **Keystrokes are not appearing on Mac** | Modifier stuck or connection dropped | Tap the **"Reset ⌘/⌥"** button on the keyboard screen to send an atomic key-release report to macOS. Check the status badge to ensure it says "Connected". |
| **macOS Keyboard Setup Assistant keeps opening** | macOS identifier pending | Complete the assistant once by pressing **Z** when prompted for the key next to Left Shift, then pick **ANSI**. |

---

## 🛡️ License

Free and open-source software built for macOS users.

