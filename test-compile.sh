#!/bin/bash
# Quick compile test for job feature

echo "========================================="
echo "Testing Compilation of Job Feature"
echo "========================================="
echo ""

echo "Step 1: Checking if files exist..."
echo ""

if [ -f "src/main/java/com/ainews/model/JobPosting.java" ]; then
    echo "✅ JobPosting.java exists"
else
    echo "❌ JobPosting.java NOT FOUND"
    exit 1
fi

if [ -f "src/main/java/com/ainews/service/JobService.java" ]; then
    echo "✅ JobService.java exists"
else
    echo "❌ JobService.java NOT FOUND"
    exit 1
fi

if [ -f "src/main/java/com/ainews/controller/NewsController.java" ]; then
    echo "✅ NewsController.java exists"
else
    echo "❌ NewsController.java NOT FOUND"
    exit 1
fi

echo ""
echo "Step 2: Checking imports in NewsController..."
echo ""

if grep -q "import com.ainews.model.JobPosting" src/main/java/com/ainews/controller/NewsController.java; then
    echo "✅ JobPosting import found"
else
    echo "❌ JobPosting import MISSING"
fi

if grep -q "import com.ainews.service.JobService" src/main/java/com/ainews/controller/NewsController.java; then
    echo "✅ JobService import found"
else
    echo "❌ JobService import MISSING"
fi

if grep -q "private final JobService jobService" src/main/java/com/ainews/controller/NewsController.java; then
    echo "✅ jobService field found"
else
    echo "❌ jobService field MISSING"
fi

echo ""
echo "Step 3: Attempting to compile..."
echo ""

# Clean first
mvn clean -q

# Try to compile
if mvn compile -DskipTests 2>&1 | tee /tmp/compile.log | grep -q "BUILD SUCCESS"; then
    echo "✅ BUILD SUCCESS!"
    echo ""
    echo "Job feature compiled successfully!"
else
    echo "❌ BUILD FAILED"
    echo ""
    echo "Last 30 lines of compile output:"
    tail -30 /tmp/compile.log
fi

echo ""
echo "========================================="
echo "Compile Test Complete"
echo "========================================="

