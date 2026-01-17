# Deployment Guide for AI News App

This guide will help you deploy the AI News App to a free cloud hosting service.

## Option 1: Railway (Recommended - Easiest)

Railway offers a free tier with $5 credit per month.

### Steps:

1. **Create a Railway Account**
   - Go to https://railway.app/
   - Sign up with GitHub (recommended) or email

2. **Create a New Project**
   - Click "New Project"
   - Select "Deploy from GitHub repo" (if you've pushed to GitHub)
   - OR select "Empty Project" and we'll connect it later

3. **Deploy the App**
   
   **If using GitHub:**
   - Push your code to GitHub
   - In Railway, click "New Project" → "Deploy from GitHub repo"
   - Select your repository
   - Railway will auto-detect it's a Java/Maven project
   - It will build and deploy automatically

   **If not using GitHub:**
   - In Railway, click "New Project" → "Empty Project"
   - Click "New" → "GitHub Repo" and connect your repo
   - OR use Railway CLI (see below)

4. **Set Environment Variables (Optional)**
   - In Railway dashboard, go to your project
   - Click on your service → "Variables"
   - Add: `NEWS_API_KEY` = your NewsAPI key (if you have one)
   - Add: `PORT` = 8080 (Railway sets this automatically, but you can override)

5. **Get Your App URL**
   - Railway will provide a URL like: `https://your-app-name.up.railway.app`
   - Your app will be accessible at this URL

### Using Railway CLI (Alternative):

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Initialize project
railway init

# Deploy
railway up
```

---

## Option 2: Render (Also Free)

Render offers a free tier with some limitations.

### Steps:

1. **Create a Render Account**
   - Go to https://render.com/
   - Sign up with GitHub

2. **Create a New Web Service**
   - Click "New" → "Web Service"
   - Connect your GitHub repository
   - OR use "Public Git repository" and paste your repo URL

3. **Configure the Service**
   - **Name**: ai-news-app (or any name)
   - **Environment**: Java
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/ai-news-app-1.0.0.jar`
   - **Plan**: Free

4. **Set Environment Variables**
   - Scroll down to "Environment Variables"
   - Add: `NEWS_API_KEY` = your NewsAPI key (optional)
   - Add: `PORT` = 8080 (Render sets this automatically)

5. **Deploy**
   - Click "Create Web Service"
   - Render will build and deploy your app
   - You'll get a URL like: `https://ai-news-app.onrender.com`

---

## Option 3: Fly.io (Free Tier Available)

### Steps:

1. **Install Fly CLI**
   ```bash
   curl -L https://fly.io/install.sh | sh
   ```

2. **Login to Fly.io**
   ```bash
   fly auth login
   ```

3. **Create a Fly.io App**
   ```bash
   cd /path/to/ai-news-app
   fly launch
   ```
   - Follow the prompts
   - Select a region
   - Don't deploy yet (we'll configure first)

4. **Configure fly.toml** (if not auto-generated)
   - Create `fly.toml` in project root
   - See example below

5. **Set Secrets (Environment Variables)**
   ```bash
   fly secrets set NEWS_API_KEY=your-api-key-here
   ```

6. **Deploy**
   ```bash
   fly deploy
   ```

---

## Quick Deploy Script

I've prepared the necessary files:
- `Dockerfile` - For containerized deployment
- `railway.json` - Railway configuration
- Updated `application.properties` - Supports environment variables

---

## Important Notes:

1. **Free Tier Limitations:**
   - Railway: $5 credit/month (usually enough for small apps)
   - Render: Free tier apps sleep after 15 minutes of inactivity
   - Fly.io: Limited resources on free tier

2. **NewsAPI Key:**
   - The app works without an API key (uses sample data)
   - To get real news, sign up at https://newsapi.org/ and add your key

3. **Port Configuration:**
   - Cloud providers set the PORT environment variable
   - The app is configured to use `${PORT:8081}` (defaults to 8081 if not set)

4. **Build Time:**
   - First deployment may take 5-10 minutes
   - Subsequent deployments are faster

---

## Troubleshooting:

- **Build fails**: Check that Java 17 is specified in pom.xml
- **App won't start**: Check logs in the cloud provider dashboard
- **Port errors**: Ensure PORT environment variable is set correctly
- **No news showing**: Check if NEWS_API_KEY is set (or app will use sample data)

---

## Recommended: Railway

Railway is the easiest option with:
- ✅ Automatic detection of Java/Maven projects
- ✅ Free $5 credit per month
- ✅ Easy GitHub integration
- ✅ Simple environment variable management
- ✅ Good free tier limits

Just push to GitHub and connect to Railway!
