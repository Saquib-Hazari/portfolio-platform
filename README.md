# Portfolio Platform — Full‑Stack Showcase

A modern portfolio platform with authentication, blogs, comments, reactions, admin workflows, and a clean UX. Built end‑to‑end with a production‑ready setup.

**Live Demo:** [](https://react-frontend-zlql.vercel.app/) 

---

## Highlights

1. JWT auth (signup/login/logout)
2. Blog CRUD with tags and images
3. Comments + reactions with permissions
4. Admin dashboards and moderation
5. Dockerized backend with Flyway migrations

---

## Tech Stack

**Frontend**

- React, Vite, TypeScript
- Tailwind CSS

**Backend**

- Java, Spring Boot, Spring Security
- Spring Data JPA, Flyway

**Database**

- PostgreSQL (Supabase)

**Deploy**

- Vercel (frontend), Render (backend)

---

## Screenshots

Add your screenshots to a folder like `docs/images/` and update the paths below.

![Home](./uploads/Screenshot%202026-03-28%20at%201.10.32 PM.png)
![Blog List](./uploads/Screenshot%202026-03-28%20at%203.05.45 PM.png)
![Blog Detail](./uploads/Screenshot%202026-03-28%20at%203.06.10 PM.png)
![Admin Dashboard](./uploads/Screenshot%202026-03-28%20at%203.55.22 PM.png)

---

## Architecture (High‑Level)

1. React SPA consumes Spring Boot REST APIs
2. JWT‑based auth for protected routes
3. PostgreSQL + Flyway for schema migrations
4. Separate deploy pipelines for frontend and backend

---

## Repo Structure

```
.
├── backend/   # Spring Boot API
├── frontend/  # React app
└── README.md
```

---

## Run Locally

**Backend**

```
cd backend
./mvnw spring-boot:run
```

**Frontend**

```
cd frontend
npm install
npm run dev
```

---

## Environment Variables

**Backend**

```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db>
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
JWT_SECRET=...
ACCESS_EXPIRATION=15m
REFRESH_EXPIRATION=14d
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://react-frontend-9tix.vercel.app
```

**Frontend**

```
VITE_API_URL=https://portfolio-backend-ecaf.onrender.com
```

---

## Links

- Live App: https://react-frontend-zlql.vercel.app/

---

## Credits

Built and maintained by Saquib Hazari.
