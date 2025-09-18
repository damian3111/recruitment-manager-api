

# 🔗 Live Demo:

### **Test User**

**Role**: Recruiter  
**Email Address**: root2@gmail.com  
**Password**: tempPass123

---
🚀 Recruitment Matching Platform

Recruitment Matching Platform — Full-stack web application that streamlines hiring by connecting recruiters and candidates. Recruiters create and manage job postings, track applications, and search talent; candidates build rich profiles, showcase skills, and apply with tailored submissions. Key capabilities include secure authentication, role-based dashboards, skill-based matching with relevance scoring, advanced filtering for jobs and candidates, invitation management, and activity tracking across the hiring funnel.

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

<img width="1851" height="958" alt="unknown_041" src="https://github.com/user-attachments/assets/e891d7a4-49d3-4445-a0ea-e4b7dae24fc3" />  

<img width="1832" height="964" alt="unknown_038" src="https://github.com/user-attachments/assets/be9a10a3-03e9-4768-9863-0d7b12402307" />  

 
<img width="1829" height="966" alt="unknown_039" src="https://github.com/user-attachments/assets/71f05394-3ae0-47bc-beef-0d07110983ae" />  


<img width="1849" height="965" alt="unknown_040" src="https://github.com/user-attachments/assets/3aae4488-3f0d-4f6c-8b54-1b13f49f6db6" />  


<img width="1848" height="963" alt="unknown_042" src="https://github.com/user-attachments/assets/9923c8c6-023f-4597-ab2c-2f347cba9aee" />  


<img width="1834" height="964" alt="unknown_043" src="https://github.com/user-attachments/assets/215ff99b-6b3e-4a61-824f-68998c8d9dc6" />



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
https://github.com/damian3111/recruitment-manager-api

Frontend available at: http://localhost:3000
https://github.com/damian3111/recruitment-manager-ui  

(Auto-generated API docs available via OpenAPI/Swagger UI)
```
## **🧪 Testing:**
```
Run backend tests:

./mvnw test

Uses JUnit + Testcontainers (isolated PostgreSQL DB for tests).
```
