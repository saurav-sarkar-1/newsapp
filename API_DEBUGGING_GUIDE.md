# 🔧 API Debugging Guide - Jobs Not Loading

## Problem
- Adzuna API configured but returning no jobs
- RemoteOK API returning null

## ✅ Fixed Issues

### 1. **Enhanced Error Logging**
Added comprehensive logging to track:
- API configuration status
- API call attempts
- Response sizes
- Parsing results
- Error details with stack traces

### 2. **Improved RemoteOK API Call**
- Added proper User-Agent header (full browser string)
- Added Accept header
- Added timeout (15 seconds, increased from 10)
- **Increased WebClient buffer to 5MB** (fixes DataBufferLimitException)
- Better error handling for buffer overflow
- RemoteOK returns large datasets - now handled properly

### 3. **Enhanced Adzuna API Call**
- Broadened search terms: "english OR data entry OR administrative OR banking OR teacher OR office"
- Increased results to 50 per page
- Added timeout (15 seconds)
- Better error logging with full exception details

### 4. **Better Response Parsing**
- Added null checks
- Added try-catch for individual jobs
- Skip malformed entries instead of failing entirely
- Log parsing progress

---

## 🧪 How to Test

### Step 1: Configure Adzuna API

Add to `src/main/resources/application.properties`:
```properties
adzuna.api.id=YOUR_APP_ID_HERE
adzuna.api.key=YOUR_APP_KEY_HERE
```

Or set environment variables:
```bash
export ADZUNA_API_ID=your_app_id
export ADZUNA_API_KEY=your_app_key
```

### Step 2: Build & Run with Logging
```bash
cd /Users/I054564/ai-news-app

# Clean and build
mvn clean package -DskipTests

# Run and watch the logs
java -jar target/ai-news-app-1.0.0.jar
```

### Step 3: Trigger Job Fetch
```bash
# In another terminal
curl http://localhost:8080/api/news/jobs/germany
```

---

## 📊 What You'll See in Logs

### Successful Adzuna API Call:
```
========================================
Starting job fetch from multiple sources
========================================
Adzuna API ID configured: 12345abc
Fetching from Adzuna API...
API URL (without credentials): https://api.adzuna.com/v1/api/jobs/de/search/1?results_per_page=50&what=english OR data entry OR administrative OR banking OR teacher OR office
Adzuna API response received, length: 45231
Adzuna returned 50 results
Successfully parsed 50 jobs from Adzuna
✅ Successfully fetched 50 jobs from Adzuna

Fetching from RemoteOK API...
Attempting to fetch from RemoteOK API: https://remoteok.com/api
RemoteOK API response received, length: 123456
RemoteOK returned 1000 total results
Found 8 Germany-related jobs from RemoteOK
✅ Successfully fetched 8 jobs from RemoteOK

========================================
Total jobs fetched from APIs: 58
========================================
```

### Failed API Call (shows errors):
```
========================================
Starting job fetch from multiple sources
========================================
Adzuna API ID configured: wrong_id
Fetching from Adzuna API...
Adzuna API error: WebClientResponseException$Unauthorized - 401 Unauthorized
  at org.springframework.web.reactive...
❌ Adzuna returned no jobs

Fetching from RemoteOK API...
Attempting to fetch from RemoteOK API: https://remoteok.com/api
RemoteOK API error: ReadTimeoutException - Connection timeout
  at reactor.netty.channel...
❌ RemoteOK returned no jobs

========================================
Total jobs fetched from APIs: 0
========================================
⚠️  No jobs from APIs, using sample job data
```

---

## 🐛 Common Issues & Solutions

### Issue 1: Adzuna Returns 401 Unauthorized
**Cause:** Wrong API credentials

**Solution:**
1. Verify your API ID and Key at https://developer.adzuna.com/
2. Check for extra spaces in configuration
3. Ensure environment variables are set correctly

**Test credentials:**
```bash
curl "https://api.adzuna.com/v1/api/jobs/de/search/1?app_id=YOUR_ID&app_key=YOUR_KEY&results_per_page=1"
```

### Issue 2: RemoteOK Returns Timeout or Buffer Limit Error
**Cause:** API might be slow, blocking requests, or returning very large response (>256KB)

**Error Messages:**
- `ReadTimeoutException - Connection timeout`
- `DataBufferLimitException: Exceeded limit on max bytes to buffer : 262144`

**Solutions:**
1. ✅ **FIXED**: Increased WebClient buffer to 5MB (handles large RemoteOK responses)
2. The app will fallback to Adzuna + sample data automatically
3. RemoteOK API is free but returns 1000+ jobs which can be large
4. Check if https://remoteok.com/api works in browser

**Manual test:**
```bash
curl -H "User-Agent: Mozilla/5.0" https://remoteok.com/api | jq '.[0:3]'
```

**Note:** RemoteOK returning large responses is normal - the app now handles it!

### Issue 3: Empty Results from Adzuna
**Cause:** Search terms might not match many jobs

**Solutions:**
1. Check Adzuna website directly: https://www.adzuna.de/
2. Modify search terms in `JobService.java` line ~116
3. Try without location filter:
   ```java
   String url = String.format(
       "https://api.adzuna.com/v1/api/jobs/de/search/1?app_id=%s&app_key=%s&results_per_page=50",
       adzunaApiId, adzunaApiKey
   );
   ```

### Issue 4: Rate Limiting
**Cause:** Too many API calls (Adzuna free tier: 250/month)

**Solution:**
- Add caching to reduce API calls
- Use sample data as fallback (already implemented)
- Consider upgrading Adzuna plan

---

## 🔍 Debugging Commands

### Check Application Properties:
```bash
cat src/main/resources/application.properties | grep adzuna
```

### Check Environment Variables:
```bash
echo "Adzuna ID: $ADZUNA_API_ID"
echo "Adzuna Key: $ADZUNA_API_KEY"
```

### Test Adzuna API Directly:
```bash
# Replace with your credentials
APP_ID="your_id"
APP_KEY="your_key"

curl "https://api.adzuna.com/v1/api/jobs/de/search/1?app_id=$APP_ID&app_key=$APP_KEY&results_per_page=5&what=software" | jq '.results[0]'
```

### Test RemoteOK API Directly:
```bash
curl -H "User-Agent: Mozilla/5.0" https://remoteok.com/api | jq '.[1:3] | .[] | {position, company, location}'
```

### Monitor Application Logs:
```bash
# Run app and watch logs in real-time
java -jar target/ai-news-app-1.0.0.jar 2>&1 | grep -A 5 -B 5 "job\|API\|Error"
```

---

## 📝 Enhanced Features

### What Changed:
1. **Better logging** - See exactly what's happening
2. **Broader search terms** - More job categories
3. **Increased results** - 50 jobs from Adzuna (was 20)
4. **Error resilience** - Parse what we can, skip bad data
5. **Timeout handling** - Don't wait forever for APIs

### Fallback Mechanism:
```
API Call → Error? → Try next API
All APIs fail? → Return 22 sample jobs
```

You'll **always** get jobs, even if APIs fail!

---

## ✅ Verification Steps

1. **Start the app**
   ```bash
   java -jar target/ai-news-app-1.0.0.jar
   ```

2. **Watch the startup logs** - Look for:
   - "Adzuna API ID configured: XXX"
   - API call attempts
   - Success/failure messages

3. **Call the jobs endpoint**
   ```bash
   curl http://localhost:8080/api/news/jobs/germany | jq '. | length'
   ```

4. **Check the response**
   - Should see job count (58+ if APIs work, 22 if using samples)
   - Check `source` field: "Adzuna", "RemoteOK", or company names (samples)

---

## 🎯 Expected Behavior

### With Working APIs:
- Adzuna: 30-50 jobs (varies by day)
- RemoteOK: 5-15 Germany-related remote jobs
- **Total: 35-65 jobs**

### With Failed APIs:
- **22 curated sample jobs** (always available)
- Categories: Software, Data Entry, Admin, Banking, Education

---

## 📧 Still Not Working?

If after these fixes you still see issues:

1. **Check the logs** when calling `/api/news/jobs/germany`
2. **Copy the error messages** from console
3. **Test Adzuna API directly** with curl command above
4. **Verify your API credentials** at developer.adzuna.com

The enhanced logging will show exactly where it's failing!

---

## 🚀 Quick Test Script

```bash
#!/bin/bash
# Test job APIs

echo "Testing Adzuna API..."
if [ -n "$ADZUNA_API_ID" ] && [ -n "$ADZUNA_API_KEY" ]; then
    curl -s "https://api.adzuna.com/v1/api/jobs/de/search/1?app_id=$ADZUNA_API_ID&app_key=$ADZUNA_API_KEY&results_per_page=1" | jq '.count'
else
    echo "⚠️  Adzuna credentials not set"
fi

echo ""
echo "Testing RemoteOK API..."
curl -s -H "User-Agent: Mozilla/5.0" https://remoteok.com/api | jq 'length'

echo ""
echo "Testing App API..."
curl -s http://localhost:8080/api/news/jobs/germany | jq '. | length'
```

Save as `test-apis.sh`, make executable, and run!

---

**The fixes are in place! Build, run, and check the logs to see what's happening.** 🔍

