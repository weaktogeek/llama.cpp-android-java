# llama.cpp-android-java

> **Offline AI Chat for Android — Run Llama.cpp locally in Java with no internet, no cloud, full privacy.**

![Android](https://img.shields.io/badge/Platform-Android-brightgreen?logo=android)
![Language](https://img.shields.io/badge/Language-Java-blue?logo=openjdk)
![LLM](https://img.shields.io/badge/Powered_by-llama.cpp-orange?logo=meta)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 📖 Overview

**ThinkOffline** is an Android app that runs **Llama.cpp models fully on-device**, written in **Java** and integrated through **JNI (Java Native Interface)**.  
It provides an **offline AI chat experience** — no internet required, no cloud inference, and complete data privacy.

This project showcases how to embed **Llama.cpp** inside an Android app, load **GGUF models**, and perform **text generation locally**.

---

## ✨ Features

✅ 100% **offline LLM inference** — no network calls  
✅ **Java + JNI** bridge to native Llama.cpp  
✅ **Local model loader** for GGUF models  
✅ **Streaming chat interface** built with RecyclerView  
✅ **Works on Android 8.0+ (API 26+)**  
✅ Optional **Vulkan / NNAPI acceleration**  
✅ **Privacy-first design** — your data never leaves your phone

---

## 🧩 Architecture

ThinkOffline/
├── app/ # Android Java app (UI, ViewModel, JNI bridge)
├── llama.cpp/ # Native C++ inference engine
├── models/ # GGUF model files (e.g., llama-2-7b.Q4_K_M.gguf)
└── README.md

## 🧠 Future Roadmap

 Add multi-threaded inference settings
 Add token streaming UI
 Implement voice input + TTS reply
 Support model quantization selector
 Optional Vulkan acceleration toggle
