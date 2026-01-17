# Railway Deployment - Quick Start Guide

## Prerequisites
- GitHub account
- Railway account (sign up at https://railway.app/)

## Step-by-Step Deployment

### 1. Initialize Git (if not already done)
```bash
cd /Users/I054564/ai-news-app
git init
git add .
git commit -m "Initial commit - AI News App ready for Railway"
```

### 2. Create GitHub Repository
1. Go to https://github.com/new
2. Repository name: `ai-news-app` (or any name you prefer)
3. Make it **Public** (required for Railway free tier) or Private (if you have Railway Pro)
4. **Don't** initialize with README, .gitignore, or license
5. Click "Create repository"

### 3. Push to GitHub
```bash
# Replace YOUR_USERNAME with your GitHub username
git remote add origin https://github.com/YOUR_USERNAME/ai-news-app.git
git branch -M main
git push -u origin main
```

### 4. Deploy on Railway

1. **Sign up/Login to Railway**
   - Go to https://railway.app/
   - Click "Start a New Project"
   - Sign up with GitHub (recommended)

2. **Create New Project**
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Authorize Railway to access your GitHub if prompted
   - Select your `ai-news-app` repository

3. **Railway Auto-Detection**
   - Railway will automatically detect it's a Java/Maven project
   - It will use the `railway.json` configuration
   - Build will start automatically

4. **Wait for Deployment**
   - First build takes 5-10 minutes
   - You'll see build logs in Railway dashboard
   - Once complete, Railway will provide a URL

5. **Get Your App URL**
   - In Railway dashboard, click on your service
   - Go to "Settings" tab
   - Under "Domains", you'll see your app URL
   - It will be like: `https://ai-news-app-production.up.railway.app`

6. **Set Environment Variables (Optional)**
   - In Railway dashboard → Your service → "Variables" tab
   - Add: `NEWS_API_KEY` = your NewsAPI key (if you have one)
   - The app works without it (uses sample data)

### 5. Access Your App
- Your app will be live at the Railway-provided URL
- Share this URL with others!

## Troubleshooting

### Build Fails
- Check build logs in Railway dashboard
- Ensure Java 17 is specified in pom.xml (it is)
- Check that all files are committed to git

### App Won't Start
- Check logs in Railway dashboard
- Ensure PORT environment variable is set (Railway sets this automatically)
- Verify application.properties uses `${PORT:8081}`

### No News Showing
- App uses sample data if NEWS_API_KEY is not set
- This is normal and expected behavior
- Add your NewsAPI key in Railway Variables if you want real news

## Railway Free Tier
- $5 credit per month
- Usually enough for small apps
- Apps don't sleep (unlike Render)
- Automatic HTTPS

## Next Steps After Deployment
1. Test your app at the Railway URL
2. (Optional) Add custom domain in Railway Settings
3. (Optional) Set up NEWS_API_KEY for real news

Your app is now live! 🚀
