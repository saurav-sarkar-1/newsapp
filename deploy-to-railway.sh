#!/bin/bash

# Railway Deployment Helper Script
echo "🚀 Railway Deployment Helper"
echo "=============================="
echo ""

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "📦 Initializing git repository..."
    git init
fi

# Check if files are committed
if [ -z "$(git status --porcelain)" ]; then
    echo "✅ All files are already committed"
else
    echo "📝 Staging all files..."
    git add .
    echo "💾 Committing files..."
    git commit -m "Initial commit - AI News App ready for Railway deployment"
    echo "✅ Files committed"
fi

echo ""
echo "📋 Next Steps:"
echo "=============="
echo ""
echo "1. Create a GitHub repository:"
echo "   - Go to https://github.com/new"
echo "   - Name it: ai-news-app (or any name)"
echo "   - Make it Public (for Railway free tier)"
echo "   - Don't initialize with README"
echo ""
echo "2. Push to GitHub (replace YOUR_USERNAME):"
echo "   git remote add origin https://github.com/YOUR_USERNAME/ai-news-app.git"
echo "   git branch -M main"
echo "   git push -u origin main"
echo ""
echo "3. Deploy on Railway:"
echo "   - Go to https://railway.app/"
echo "   - Sign up with GitHub"
echo "   - Click 'New Project' → 'Deploy from GitHub repo'"
echo "   - Select your repository"
echo "   - Railway will auto-deploy!"
echo ""
echo "4. Get your app URL from Railway dashboard"
echo ""
echo "✨ Your app will be live in 5-10 minutes!"
echo ""
