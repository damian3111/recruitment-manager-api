

# 🔗 Live Demo: [damiankwasny.pl](https://damiankwasny.pl)

### **Test User**
**Email Address**: root2@gmail.com  

**Password**: tempPass123

---
🚀 Recruitment Matching Platform

A full-stack recruitment platform connecting recruiters and candidates.
Recruiters can create job offers, manage applications, and search for candidates, while candidates can build detailed profiles, showcase skills, and apply for jobs.

The platform features secure authentication, skill-based matching, job & candidate filtering, invitation management, and much more.


## **Core Features:**
```
👤 User Roles – Candidate & Recruiter with dedicated dashboards

🔐 Authentication – Spring Security with JWT & OAuth2 (Google login)

📧 Email Verification – Activation codes for secure onboarding

💼 Job Management – Recruiters create job offers with description, payment, requirements & skills

📝 Candidate Profiles – Candidates describe skills, experience, and personal details

🎯 Skill-Based Matching – Match candidates and jobs based on skills

🔎 Filtering System – Both recruiters & candidates can filter jobs and candidate profiles

🤝 Invitations – Send, accept, reject, and cancel invitations

⚙️ Settings Page – Manage account preferences

📊 Future Modules (UI-ready) – Analytics, Chats, Career Compass, Skill Matcher, Growth Tracker
```



## **🛠️ Tech Stack:**
```
Backend (API)

Java, Spring Boot

Spring Security (JWT, OAuth2 Google Login)

PostgreSQL (Dockerized)

Ehcache (caching)

OpenAPI (API documentation)

JUnit, Testcontainers (integration testing)

Frontend (UI)

Next.js (React, TypeScript)

Tailwind CSS (modern UI styling)

React Hook Form & validations

DevOps

Docker & Docker Compose

CI/CD with GitHub Actions

Email service for activation codes
```
## **📐 Architecture:**
```
Frontend (Next.js + Tailwind)
        ⬇️
REST API (Spring Boot, OpenAPI)
        ⬇️
PostgreSQL (Docker)


Fully containerized with Docker

CI/CD pipeline handles build, test, and deployment

OpenAPI for API contracts and docs
```

## **🚀 Prerequisites:**
```
Java 17+

Node.js 18+

Docker
```
---
```
API available at: http://localhost:8080

Frontend available at: http://localhost:3000

(Auto-generated API docs available via OpenAPI/Swagger UI)
```
## **🧪 Testing:**
```
Run backend tests:

./mvnw test

Uses JUnit + Testcontainers (isolated PostgreSQL DB for tests).
```
