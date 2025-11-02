# 🧠 Offline.AI
> **Offline AI Chat for Android — Run llama.cpp locally in Java with no internet, no cloud, full privacy.**

![Android](https://img.shields.io/badge/Platform-Android-brightgreen?logo=android)
![Language](https://img.shields.io/badge/Language-Java-blue?logo=openjdk)
![LLM](https://img.shields.io/badge/Powered_by-llama.cpp-orange)
![Min%20SDK](https://img.shields.io/badge/Android-12%2B%20(API%2031%2B)-informational)
![Status](https://img.shields.io/badge/Status-POC-orange)

---

## 📖 Overview

**Offline.AI** is an Android app that runs **llama.cpp** models fully on-device, written in **Java** and integrated through **JNI (Java Native Interface)**.  
It provides an **offline AI chat experience** — no internet required, no cloud inference, and complete data privacy.

This project showcases how to embed **llama.cpp** inside an Android app, load **GGUF** models, and perform **text generation locally**.

> ⚠️ **Note:** This is a **proof-of-concept (POC)** intended for learning and experimentation, not a production-ready app.

---

## ✨ Features

- ✅ 100% **offline LLM inference** — no network calls
- ✅ **Java + JNI** bridge to native **llama.cpp**
- ✅ **Local model loader** for **GGUF** models
- ✅ **Streaming chat interface** built with **RecyclerView**
- ✅ Works on **Android 12+ (API 31+)**
- ✅ **Privacy-first design** — your data never leaves your phone

---

## 🚀 Quick Start (Alternative: Install APK)

If you don’t want to build, you can simply **download the APK** from the repository’s **Releases** section, install it on your device, and run it directly.  
> Settings → Security → allow installing apps from unknown sources (if prompted).

---

## 📦 Prerequisites

1. **Clone llama.cpp** (native engine used by the app):
   ```bash
   git clone https://github.com/ggml-org/llama.cpp
   ```
2. **Android Studio** (Electric Eel or newer) with **Android NDK** and **CMake** components installed.
3. **Android device** running **Android 12+ (API 31+)** with at least **4–6 GB RAM** recommended.
4. A **GGUF** model (e.g., Llama 3.2 1B).

---

## 🛠️ Build & Run (Step-by-step)

### 1) Clone this repository
```bash
git clone https://github.com/weaktogeek/llama.cpp-android-java.git
cd llama.cpp-android-java
```

### 2) Ensure you’re on the `main` branch
```bash
git checkout main
```

### 3) Open the project in Android Studio
- **File → Open…** → select the project folder.
- Let Gradle sync and the NDK/CMake components finish downloading if prompted.

### 4) Configure the native build path (CMakeLists.txt)
- Open the **`llama` module** → `src/main/cpp/` → **`CMakeLists.txt`**.
- **At line 36**, update the path that points to your **local `llama.cpp` build directory** (the repo you cloned in *Prerequisites*).  
  - Example (adjust to your machine):
    ```cmake
    # Example: point this to your local llama.cpp build dir
    set(LLAMA_BUILD_DIR "/absolute/path/to/llama.cpp/build-llama")
    ```
- If `build-llama` does not exist yet, create it or adjust the path to the correct native source/build location within your cloned `llama.cpp` repo.

### 5) Sync & Build
- Click **Sync Project with Gradle Files**.
- Select a **physical device** (recommended) or compatible emulator (x86_64, plenty of RAM).
- Click **Run ▶** to build and install the app.

### 6) First Launch
When the app launches:
1. Grant **storage permission** (used only to let you pick model files from device storage).
2. Prepare a **GGUF** model (example: `llama-3.2-1b-instruct.Q4_K_M.gguf`).  
   - Place it anywhere accessible on your device (e.g., `Downloads/`).

---

## ▶️ How to Use

1. **Open** the app and grant storage permission.
2. **Download** a small GGUF model (e.g., **Llama 3.2 1B**) to your device.
3. Tap **Load Model** and **select** the downloaded `.gguf` file.
4. **Wait** for initialization; once the model is loaded, you’re good to go.
5. Enter your prompt and **chat locally** — all inference stays on-device.

> ⏳ Initial load may take some time depending on device performance and model size.

---

## ❓ Troubleshooting

- **Build fails: NDK/CMake not found**  
  Open Android Studio → **SDK Manager** → **SDK Tools** → install **NDK**, **CMake**, and **LLDB**.

- **`CMakeLists.txt` path error at line 36**  
  Make sure `LLAMA_BUILD_DIR` (or equivalent variable) points to your **actual local path** of the `llama.cpp` repo (e.g., `/Users/you/dev/llama.cpp/build-llama`).

- **App crashes on model load**  
  Use a **smaller model** (e.g., 1B or 3B quantized GGUF), close background apps, and ensure **4–6 GB free RAM**.

- **Very slow inference**  
  Smaller/quantized models run faster. Multi-threading and acceleration toggles may be limited in this POC.

---

## 🧭 Roadmap

- [ ] Add **multi-threaded inference** settings  
- [ ] Add **token streaming** with partial text updates  
- [ ] **UI:** voice input + TTS reply  
- [ ] Support **model quantization** selector  
- [ ] Optional **Vulkan** acceleration toggle

---

## 🔒 Privacy

- No network calls  
- No analytics or telemetry  
- All prompts and generations stay **on-device**


## 🙏 Credits

- [ggml-org/llama.cpp](https://github.com/ggml-org/llama.cpp)  
- Android NDK, CMake, JNI
