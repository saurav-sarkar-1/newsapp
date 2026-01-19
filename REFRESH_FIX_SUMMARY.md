# News Refresh Fix - Summary

## Problem
The "Refresh News" button in the UI was not actually refreshing the news. Clicking it would call the same API endpoint that fetches news on initial load, which could return cached or the same results.

## Root Cause
1. **Frontend Issue**: The `fetchNews()` function was always calling the regular endpoints (`/api/news` or `/api/news/stock-market`) instead of the refresh endpoints (`/api/news/refresh` or `/api/news/stock-market/refresh`)
2. **Backend Issue**: The refresh endpoints existed but didn't have any mechanism to force fresh data - they were just calling the same service methods as the regular endpoints
3. **No Caching Strategy**: There was no caching implemented, so there was no way to distinguish between a cached response and a fresh fetch

## Solution Implemented

### 1. Backend Changes (NewsService.java)
- Added caching mechanism with 15-minute cache duration
- Created cache variables for both AI news and stock market news
- Modified `getAiNews()` and `getIndianStockMarketNews()` to accept a `forceRefresh` parameter
- When `forceRefresh=true`, the cache is bypassed and fresh data is fetched from the API
- When `forceRefresh=false`, cached data is returned if it's less than 15 minutes old
- Added console logging to show when cache is used vs when fresh data is fetched

### 2. Backend Changes (NewsController.java)
- Updated `/api/news/refresh` endpoint to call `newsService.getAiNews(true)` - forcing refresh
- Updated `/api/news/stock-market/refresh` endpoint to call `newsService.getIndianStockMarketNews(true)` - forcing refresh
- Regular endpoints continue to use caching for better performance

### 3. Frontend Changes (app.js)
- Modified `setupEventListeners()` to pass `true` parameter when refresh button is clicked
- Updated `fetchNews()` function to accept a `forceRefresh` parameter (default: false)
- When `forceRefresh=true`, the function calls the `/refresh` endpoints
- When `forceRefresh=false`, it calls the regular endpoints (uses cache if available)
- Added better loading messages to distinguish between initial load and refresh
- Added console logging for successful refresh operations

## How It Works Now

### Initial Load
1. User opens the app
2. `fetchNews(false)` is called
3. Calls `/api/news` (regular endpoint)
4. Backend checks cache - if empty or expired, fetches from API
5. Results are cached for 15 minutes
6. News is displayed

### Clicking Refresh Button
1. User clicks "🔄 Refresh News" button
2. `fetchNews(true)` is called
3. Calls `/api/news/refresh` (refresh endpoint)
4. Backend bypasses cache and forces fresh API call
5. New results are cached
6. Updated news is displayed
7. Console logs: "News refreshed successfully at [time]"

### Switching Between Tabs
1. User switches between "AI News" and "Stock Market" tabs
2. `fetchNews(false)` is called
3. Uses cached data if available (within 15 minutes)
4. Otherwise fetches fresh data and caches it

## How to Run the Application Locally

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Steps

1. **Navigate to project directory:**
   ```bash
   cd /Users/I054564/ai-news-app
   ```

2. **Build the application:**
   ```bash
   mvn clean package
   ```

3. **Run using Maven:**
   ```bash
   mvn spring-boot:run
   ```

   OR

   **Run the JAR file:**
   ```bash
   java -jar target/ai-news-app-1.0.0.jar
   ```

4. **Access the application:**
   - Open your browser
   - Go to: http://localhost:8081
   - The app will load with AI News by default

5. **Test the refresh functionality:**
   - Click the "🔄 Refresh News" button
   - Open browser console (F12) to see the refresh log messages
   - Notice the loading text changes to "Refreshing AI news..."
   - News articles will be fetched fresh from the API

### Verifying the Fix

1. **Check Console Logs (Browser):**
   - Open Developer Tools (F12)
   - Go to Console tab
   - Click "Refresh News"
   - You should see: "News refreshed successfully at [time]"

2. **Check Server Logs:**
   - Watch the terminal where the app is running
   - On first load: "Fetching fresh AI news from API..."
   - Within 15 minutes: "Returning cached AI news (age: X minutes)"
   - After clicking Refresh: "Fetching fresh AI news from API..."
   - After refresh: "Cached X AI news articles"

3. **Network Tab:**
   - Open Developer Tools → Network tab
   - Click "Refresh News"
   - You should see a request to: `/api/news/refresh`

## Benefits

1. **Better Performance**: Caching reduces unnecessary API calls
2. **Faster Load Times**: Cached data loads instantly
3. **True Refresh**: Refresh button actually fetches new data
4. **API Quota Conservation**: Caching helps stay within free API limits
5. **Better UX**: Users can see fresh data when they explicitly request it

## Files Modified

1. `/src/main/java/com/ainews/service/NewsService.java` - Added caching and force refresh logic
2. `/src/main/java/com/ainews/controller/NewsController.java` - Updated refresh endpoints
3. `/src/main/resources/static/js/app.js` - Fixed frontend to call refresh endpoints

## Testing Checklist

- [x] Refresh button calls the correct endpoint
- [x] Fresh data is fetched when refresh is clicked
- [x] Cache works for initial loads within 15 minutes
- [x] Both AI News and Stock Market news refresh correctly
- [x] Console logs show cache hits/misses
- [x] Loading messages update appropriately
- [x] UI updates with new data after refresh

---

**Fix Applied:** January 19, 2026
**Status:** ✅ COMPLETE

