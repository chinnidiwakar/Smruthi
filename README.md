# 🧠 Smriti

**Smriti** is a privacy-first, offline cognitive training app focused on improving working memory using scientifically grounded N-Back training.

No ads.  
No analytics.  
No tracking.  
No cloud dependency.  

Just structured cognitive training.

---

## 🎯 Purpose

Smriti is designed as a *mental gym*, not a gamified distraction tool.

It focuses on:

- Working memory training  
- Measurable cognitive performance  
- Clean, minimal interface  
- Respect for user privacy  

---

## 🔐 Privacy Philosophy

Smriti is built with strict privacy principles:

- ❌ No internet permission  
- ❌ No analytics SDKs  
- ❌ No ad networks  
- ❌ No third-party trackers  
- ✅ 100% offline  
- ✅ All data stored locally on device  

User performance data never leaves the device.

---

## 🧠 Training Model

### N-Back (Letter-Based)

Users are shown a sequence of letters.

They must identify when the current letter matches the one shown **N steps earlier**.

**Example (N = 2):**

Sequence:  
A → F → A → Q → A  

Matches occur at positions 2 and 4.

---

### Core Metrics Tracked

- Hits  
- Misses  
- False Alarms  
- Correct Rejections  
- Accuracy  
- Reaction Time  

Future versions may include:

- Dual N-Back  
- Signal Detection (d-prime)  
- Adaptive difficulty  
- Performance trends  

---

## 🏗 Architecture

- Kotlin  
- Jetpack Compose  
- Clean separation between:
  - UI Layer  
  - Engine Layer  
  - Persistence Layer (Room planned)  

The cognitive engine is designed to be:

- Deterministic  
- Testable  
- Extensible (for Dual N-Back support)  

---

## 📦 Current Status (MVP Phase)

- [x] Project scaffold  
- [x] Setup screen  
- [ ] Letter N-Back engine  
- [ ] Game screen  
- [ ] Session tracking  
- [ ] Results screen  

---

## 🚀 Roadmap

### Phase 1
- Single Letter N-Back  
- Session metrics  
- Local storage  

### Phase 2
- Dual N-Back  
- Trend visualization  
- Adaptive N suggestion  

### Phase 3
- Encrypted local backup  
- Minimal performance analytics (fully offline)  

---

## ⚠ Scientific Note

Working memory training can improve task switching, attention control, and short-term retention.  
Effects vary between individuals.

Smriti makes no exaggerated cognitive enhancement claims.

---

## 🧘 Philosophy

Smriti is intentionally:

- Minimal  
- Calm  
- Non-addictive  
- Transparent  

It respects the user's attention.

---

## 📜 License

To be decided. Recommended: **MIT** or **Apache 2.0**.
