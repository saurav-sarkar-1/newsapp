# Railway Deployment Fix

## Problem
Error: `Unable to access jarfile target/ai-news-app-1.0.0.jar`

## Solution Applied

1. **Updated `railway.json`** - Uses dynamic JAR finding:
   ```json
   "startCommand": "java -jar $(find target -name 'ai-news-app-*.jar' -type f | head -1)"
   ```

2. **Updated `application.properties`** - Added server address:
   ```properties
   server.address=0.0.0.0
   server.port=${PORT:8081}
   ```

3. **Created `start.sh`** - Backup startup script (if needed)

## Next Steps

1. **Push to GitHub:**
   ```bash
   git push
   ```

2. **In Railway Dashboard:**
   - The deployment should automatically retry
   - Or trigger a new deployment
   - Check the logs to see if the JAR is found

## If Still Not Working

### Option 1: Use Dockerfile (More Reliable)
Rename `railway-dockerfile.json` to `railway.json`:
```bash
mv railway-dockerfile.json railway.json
git add railway.json
git commit -m "Switch to Dockerfile deployment"
git push
```

### Option 2: Check Railway Build Logs
- Go to Railway dashboard → Your service → Deployments
- Check the build logs to see:
  - If Maven build succeeded
  - What files are in the target directory
  - The exact error message

### Option 3: Manual JAR Path
If you see the exact JAR name in logs, update `railway.json`:
```json
"startCommand": "java -jar target/EXACT-JAR-NAME.jar"
```

## Current Configuration
- Builder: RAILPACK (auto-detects Maven)
- Start Command: Dynamically finds JAR file
- Server: Listens on 0.0.0.0:PORT
