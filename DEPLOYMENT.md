# Five2Nine Deployment Guide

This guide walks you through deploying the Five2Nine application to **Render** (backend) and **Vercel** (frontend).

---

## Prerequisites

- [x] Code pushed to GitHub: `https://github.com/mauryaanant005/Five2Nine.git`
- [ ] GitHub account logged in
- [ ] Render account (free tier available at [render.com](https://render.com))
- [ ] Vercel account (free tier available at [vercel.com](https://vercel.com))
- [ ] Gmail App Password for email notifications (see below)

### Generate a Gmail App Password

1. Go to your Google Account → **Security** → **2-Step Verification** (must be enabled)
2. Go to **App Passwords** → select "Mail" and your device → Generate
3. Copy the 16-character password (no spaces)
4. You'll need this for the `MAIL_PASSWORD` environment variable

### Generate a JWT Secret

Run this command or use any random string generator:

```
openssl rand -base64 32
```

Or use this pre-generated one (replace in production): `aGVsbG8td29ybGQtZml2ZTJuaW5lLXNlY3JldC1rZXktMjAyNg==`

---

## Step 1: Set Up the Database

Render does **not** offer managed MySQL. You have two options:

### Option A: Render PostgreSQL (Recommended)

1. Go to [render.com](https://render.com) → Dashboard → **New +** → **PostgreSQL**
2. Configure:
   - **Name**: `five2nine-db`
   - **Region**: Choose closest to your users
   - **Database Plan**: Free (or Starter for production)
3. Click **Create PostgreSQL**
4. Once created, copy the **Internal Database URL** (format: `postgresql://user:password@host:port/dbname`)
5. For the backend, you'll need the **external** connection string. Note all connection details shown.

### Option B: External MySQL (PlanetScale, Aiven, AWS RDS)

If you prefer MySQL, create a database at [PlanetScale](https://planetscale.com) or [Aiven](https://aiven.io) and note the JDBC connection string.

---

## Step 2: Deploy Backend to Render

### 2.1 Create a Web Service

1. Go to [render.com](https://render.com) → Dashboard → **New +** → **Web Service**
2. Connect your GitHub repository:
   - Search for `Five2Nine` under `mauryaanant005`
   - Click **Connect**
3. Configure the service:

| Setting | Value |
|---|---|
| **Name** | `five2nine-backend` |
| **Region** | Same region as your database |
| **Root Directory** | `backend` |
| **Environment** | `Java` |
| **Build Command** | `mvnw clean package -DskipTests` |
| **Start Command** | `java -jar target/five2Nine-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod` |
| **Instance Type** | Free (or Starter for production) |

4. Click **Advanced** → **Add Environment**

### 2.2 Add Environment Variables

Add these environment variables in the Render dashboard:

| Key | Value | Example |
|---|---|---|
| `DATABASE_URL` | Your database JDBC URL | `jdbc:postgresql://host:5432/dbname` (for PostgreSQL) or `jdbc:mysql://host:3306/dbname` (for MySQL) |
| `DB_USERNAME` | Database username | From your database connection details |
| `DB_PASSWORD` | Database password | From your database connection details |
| `JWT_SECRET` | Your JWT secret key | The base64 string you generated above |
| `FRONTEND_URL` | Your Vercel frontend URL (set after Step 3) | `https://five2nine.vercel.app` (update after deploying frontend) |
| `MAIL_HOST` | Gmail SMTP server | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME` | Your Gmail address | `mauryaanant005@gmail.com` |
| `MAIL_PASSWORD` | Gmail App Password | The 16-character app password |

> **Note:** For `DATABASE_URL`, if using Render PostgreSQL, the format is `jdbc:postgresql://<host>:<port>/<dbname>`. Render provides a `DATABASE_URL` variable by default if you link the database — you may need to prepend `jdbc:` to it.

### 2.3 Link Database (Optional, for Render PostgreSQL)

If using Render's managed PostgreSQL:
1. In the Web Service settings → **Events** tab → **Connect Database**
2. Select your `five2nine-db` PostgreSQL instance
3. Render will auto-inject `DATABASE_URL` (you may need to modify it to add `jdbc:` prefix)

### 2.4 Deploy

1. Click **Create Web Service**
2. Wait for the build to complete (~2-5 minutes)
3. Once deployed, note your backend URL (e.g., `https://five2nine-backend.onrender.com`)

### 2.5 Verify Backend

1. Visit `https://five2nine-backend.onrender.com/api/auth/test` (or any public endpoint)
2. Check the **Logs** tab in Render if you see errors

---

## Step 3: Deploy Frontend to Vercel

### 3.1 Create a Project

1. Go to [vercel.com](https://vercel.com) → Dashboard → **Add New...** → **Project**
2. Import your GitHub repository:
   - Find `mauryaanant005/Five2Nine` → Click **Import**
3. Configure the project:

| Setting | Value |
|---|---|
| **Project Name** | `five2nine` (or your preference) |
| **Framework Preset** | `Vite` |
| **Root Directory** | `frontend` |
| **Build Command** | `npm run build` |
| **Output Directory** | `dist` |

### 3.2 Add Environment Variables

1. Scroll to **Environment Variables** → Click **Add**
2. Add:

| Key | Value |
|---|---|
| `VITE_API_URL` | Your Render backend URL + `/api` |

Example: `https://five2nine-backend.onrender.com/api`

> **Important:** Replace the example URL with your actual Render backend URL from Step 2.5.

### 3.3 Deploy

1. Click **Deploy**
2. Wait for the build to complete (~1-2 minutes)
3. Once deployed, note your frontend URL (e.g., `https://five2nine.vercel.app`)

---

## Step 4: Update Backend CORS

After deploying the frontend, update the `FRONTEND_URL` environment variable in Render:

1. Go to Render Dashboard → `five2nine-backend` → **Environment**
2. Find `FRONTEND_URL` → Edit → Set to your Vercel URL (e.g., `https://five2nine.vercel.app`)
3. Click **Save Changes** → Render will auto-redeploy

---

## Step 5: Post-Deployment Checklist

- [ ] **CORS**: Backend allows requests from your Vercel domain
- [ ] **Environment Variables**: All secrets set in Render dashboard (not hardcoded)
- [ ] **Database**: Tables created automatically (`spring.jpa.hibernate.ddl-auto=update`)
- [ ] **Email**: Test by creating a test account and verifying email delivery
- [ ] **JWT Secret**: Using a strong, unique value in production
- [ ] **Health Check**: Visit your Vercel URL → try login → verify API calls reach Render backend
- [ ] **Custom Domain** (optional): Add a custom domain in both Vercel and Render dashboards

---

## Troubleshooting

### Backend fails to start on Render
- Check the **Logs** tab in Render for error details
- Verify `DATABASE_URL` starts with `jdbc:` (e.g., `jdbc:postgresql://...`)
- Ensure all environment variables are set correctly

### Frontend can't connect to backend
- Verify `VITE_API_URL` in Vercel environment variables ends with `/api`
- Check browser console for CORS errors
- Ensure `FRONTEND_URL` in Render matches your Vercel URL exactly

### First request is slow on Render Free tier
- Render's free tier spins down after 15 minutes of inactivity
- First request after spin-down takes ~30 seconds to wake up
- Upgrade to Starter plan for always-on instances

### Database connection errors
- For Render PostgreSQL: ensure the Web Service and Database are in the same region
- Check that the database is accessible from the web service (not just internal network)

---

## Architecture

```
User Browser
    │
    ▼ (HTTPS)
┌─────────────┐
│   Vercel    │  ← Frontend (React + Vite)
│  (Static)   │
└──────┬──────┘
       │ API Calls
       ▼
┌─────────────┐
│   Render    │  ← Backend (Spring Boot)
│  (WebSvc)   │
└──────┬──────┘
       │
       ├─→ PostgreSQL / MySQL (Database)
       └─→ Gmail SMTP (Email)
```
