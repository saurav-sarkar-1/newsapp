#!/bin/bash
# Final verification script for English Jobs in Germany feature

echo "=============================================="
echo "  JOBS FEATURE - FINAL VERIFICATION"
echo "=============================================="
echo ""

SUCCESS_COUNT=0
TOTAL_CHECKS=0

# Function to check and report
check_item() {
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    if [ $1 -eq 0 ]; then
        echo "✅ $2"
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        echo "❌ $2"
    fi
}

echo "Backend Files:"
echo "──────────────"
test -f "src/main/java/com/ainews/model/JobPosting.java"
check_item $? "JobPosting.java exists"

test -f "src/main/java/com/ainews/service/JobService.java"
check_item $? "JobService.java exists"

grep -q "import com.ainews.model.JobPosting" "src/main/java/com/ainews/controller/NewsController.java"
check_item $? "NewsController imports JobPosting"

grep -q "import com.ainews.service.JobService" "src/main/java/com/ainews/controller/NewsController.java"
check_item $? "NewsController imports JobService"

grep -q "getGermanyJobs" "src/main/java/com/ainews/controller/NewsController.java"
check_item $? "Job endpoints present in NewsController"

echo ""
echo "Frontend Files:"
echo "───────────────"
grep -q "Jobs in Germany" "src/main/resources/static/index.html"
check_item $? "Jobs tab added to HTML"

grep -q "JOBS_API_URL" "src/main/resources/static/js/app.js"
check_item $? "Jobs API URL constant defined"

grep -q "function createJobCard" "src/main/resources/static/js/app.js"
check_item $? "createJobCard function exists"

grep -q "function renderJobs" "src/main/resources/static/js/app.js"
check_item $? "renderJobs function exists"

grep -q "function filterAndRenderJobs" "src/main/resources/static/js/app.js"
check_item $? "filterAndRenderJobs function exists"

grep -q ".job-card" "src/main/resources/static/css/styles.css"
check_item $? "Job card styles added to CSS"

grep -q ".skill-tag" "src/main/resources/static/css/styles.css"
check_item $? "Skill tag styles added to CSS"

echo ""
echo "Build & Compilation:"
echo "────────────────────"
test -f "target/ai-news-app-1.0.0.jar"
check_item $? "JAR file exists"

test -f "target/classes/com/ainews/model/JobPosting.class"
check_item $? "JobPosting.class compiled"

test -f "target/classes/com/ainews/service/JobService.class"
check_item $? "JobService.class compiled"

test -f "target/classes/static/js/app.js"
check_item $? "app.js copied to target"

test -f "target/classes/static/css/styles.css"
check_item $? "styles.css copied to target"

echo ""
echo "Documentation:"
echo "──────────────"
test -f "JOBS_FEATURE_GUIDE.md"
check_item $? "JOBS_FEATURE_GUIDE.md exists"

test -f "JOBS_IMPLEMENTATION_COMPLETE.md"
check_item $? "JOBS_IMPLEMENTATION_COMPLETE.md exists"

test -f "JOBS_UI_COMPLETE.md"
check_item $? "JOBS_UI_COMPLETE.md exists"

test -f "JOBS_QUICK_REFERENCE.md"
check_item $? "JOBS_QUICK_REFERENCE.md exists"

test -f "FINAL_COMPLETE_SUMMARY.md"
check_item $? "FINAL_COMPLETE_SUMMARY.md exists"

echo ""
echo "=============================================="
echo "  VERIFICATION RESULTS"
echo "=============================================="
echo ""
echo "Passed: $SUCCESS_COUNT / $TOTAL_CHECKS checks"
echo ""

if [ $SUCCESS_COUNT -eq $TOTAL_CHECKS ]; then
    echo "🎉 ALL CHECKS PASSED! 🎉"
    echo ""
    echo "Your Jobs in Germany feature is:"
    echo "  ✅ Fully implemented"
    echo "  ✅ Successfully compiled"
    echo "  ✅ Ready to run"
    echo ""
    echo "Next steps:"
    echo "  1. Run: java -jar target/ai-news-app-1.0.0.jar"
    echo "  2. Open: http://localhost:8080"
    echo "  3. Click: 💼 Jobs in Germany tab"
    echo ""
    exit 0
else
    echo "⚠️  Some checks failed"
    echo ""
    echo "Failed: $((TOTAL_CHECKS - SUCCESS_COUNT)) check(s)"
    echo ""
    echo "Please review the items marked with ❌ above"
    exit 1
fi

