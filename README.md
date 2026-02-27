# ResumeMatch (Android)

**ResumeMatch** is a premium-looking Android app that:
- uploads a **resume PDF**
- analyzes **ATS Readiness** (heuristic score)
- computes **Job Match** against a pasted Job Description
- shows **matched/missing skills** + improvement suggestions

This app consumes a live FastAPI backend deployed on Render.

---

## Download (APK)
✅ Get the latest build from **Releases**:  
https://github.com/rehanu04/resumematch-android/releases

**Install steps**
1. Download the release `.zip` asset
2. Extract it to get the `.apk`
3. Install the APK on your Android device  
   - If blocked: Settings → Security → **Install unknown apps** → allow Files/Chrome

---

## Backend API
- Repo: https://github.com/rehanu04/resume-match-api
- Live API: https://resume-match-api-gace.onrender.com
- Swagger UI: https://resume-match-api-gace.onrender.com/docs
- Health: https://resume-match-api-gace.onrender.com/health

---

## Features
- 📄 Pick a **PDF resume** from device storage (Document Picker)
- 🧠 ATS Readiness Score (0–100)
  - contact detection (email/phone/links)
  - section detection (education/skills/experience/projects)
  - bullets + keyword density + metric signals
- 🎯 Job Match Score (0–100)
  - matched vs missing skills
  - suggestions to improve alignment
- 🖤 Dark premium UI (glass cards + chips + score bars)

---
Example in repo
- `home.jpg`
- `results.jpg`


---

# 2) Backend repo README  
Repo: `https://github.com/rehanu04/resume-match-api`

:::

```md
# ResumeMatch API (FastAPI)

FastAPI backend that powers the **ResumeMatch Android app**.

It supports:
- **Resume PDF upload** → text extraction + normalization + skill extraction
- **ATS Readiness Score** (0–100, heuristic)
- **Job Match Score** against a Job Description (0–100)
- Matched/missing skills + suggestions

---

## Android App
- Repo: https://github.com/rehanu04/resumematch-android
- Download (APK): https://github.com/rehanu04/resumematch-android/releases

---

## Live Deployment
- Live API: https://resume-match-api-gace.onrender.com
- Swagger UI: https://resume-match-api-gace.onrender.com/docs
- Health: https://resume-match-api-gace.onrender.com/health

---

## Endpoints
### Health
`GET /health`

### Upload Resume (PDF)
`POST /resume/upload`  
**Body:** `multipart/form-data` with `file` (PDF)

**Returns:**
- `resume_id`
- `skills` (curated skill set)
- `extra_skills` (generic tech keywords not in curated list)
- `text_chars`

### ATS Readiness
`POST /ats`  
**Body:**
```json
{ "resume_id": "..." }
