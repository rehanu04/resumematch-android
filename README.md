# ResumeMatch (Android)

A premium-looking Android app that compares a **resume PDF** against a **job description**, returns a **match score**, highlights **matched/missing skills**, and generates an **ATS readiness score**.

## Demo
- **Backend (Live):** https://resume-match-api-gace.onrender.com  
- **Swagger (API Docs):** https://resume-match-api-gace.onrender.com/docs

## Features
- Pick a resume **PDF** from phone storage
- Paste Job Description and get:
  - Match score (0–100)
  - Matched skills + Missing skills
  - Suggestions to improve alignment
- ATS Readiness Score (0–100) with signals:
  - contact detection, section detection, bullet/keyword density, metrics/impact

## Download (APK)
Go to **Releases** and download the latest APK ZIP:
1. Download the `.zip` from Releases
2. Extract it to get the `.apk`
3. Install the APK (you may need to allow “Install unknown apps”)

## How it works (High-level)
Android App (Jetpack Compose + Retrofit)
→ uploads PDF to FastAPI `/resume/upload`
→ compares with JD using `/match`
→ computes ATS readiness using `/ats`

## API Endpoints used
- `POST /resume/upload` (multipart PDF)
- `POST /match` (resume_id + job_description)
- `POST /ats` (resume_id)
- `GET /health`

## Tech Stack
**Android:** Kotlin, Jetpack Compose, Retrofit, OkHttp  
**Backend:** FastAPI, Uvicorn, PyPDF, Render

## Local backend (optional)
If you want to run backend locally:
```bash
uvicorn app.main:app --reload
