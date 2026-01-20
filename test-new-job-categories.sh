#!/bin/bash
# Quick test for new job categories

echo "=========================================="
echo "Testing New Job Categories"
echo "=========================================="
echo ""

echo "Starting app in background..."
cd /Users/I054564/ai-news-app
java -jar target/ai-news-app-1.0.0.jar > /tmp/app.log 2>&1 &
APP_PID=$!

echo "Waiting for app to start (10 seconds)..."
sleep 10

echo ""
echo "Testing API endpoints..."
echo ""

# Test if app is running
if curl -s http://localhost:8080/api/news/jobs/germany > /tmp/jobs.json 2>&1; then
    echo "✅ API is responding"

    # Count total jobs
    TOTAL=$(cat /tmp/jobs.json | jq '. | length' 2>/dev/null)
    echo "✅ Total jobs returned: $TOTAL"

    if [ "$TOTAL" -eq 22 ]; then
        echo "✅ Correct number of jobs (22)!"
    else
        echo "⚠️  Expected 22 jobs, got $TOTAL"
    fi

    echo ""
    echo "Job categories found:"
    cat /tmp/jobs.json | jq -r '[.[].category] | unique | .[]' 2>/dev/null | while read cat; do
        COUNT=$(cat /tmp/jobs.json | jq "[.[] | select(.category==\"$cat\")] | length" 2>/dev/null)
        echo "  - $cat: $COUNT job(s)"
    done

    echo ""
    echo "Sample jobs by new categories:"
    echo ""

    echo "Data Entry Jobs:"
    cat /tmp/jobs.json | jq -r '.[] | select(.category=="Data Entry") | "  • \(.title) at \(.company)"' 2>/dev/null

    echo ""
    echo "Administrative Jobs:"
    cat /tmp/jobs.json | jq -r '.[] | select(.category=="Administrative") | "  • \(.title) at \(.company)"' 2>/dev/null

    echo ""
    echo "Banking Jobs:"
    cat /tmp/jobs.json | jq -r '.[] | select(.category=="Banking") | "  • \(.title) at \(.company)"' 2>/dev/null

    echo ""
    echo "Education Jobs:"
    cat /tmp/jobs.json | jq -r '.[] | select(.category=="Education") | "  • \(.title) at \(.company)"' 2>/dev/null

else
    echo "❌ Failed to connect to API"
    echo "Check logs at /tmp/app.log"
fi

echo ""
echo "=========================================="
echo "Stopping test app..."
kill $APP_PID 2>/dev/null
echo "Done!"
echo "=========================================="

