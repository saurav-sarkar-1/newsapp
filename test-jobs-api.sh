#!/bin/bash
# Test script for Jobs API

echo "========================================="
echo "Testing Jobs API Endpoints"
echo "========================================="
echo ""

# Check if app is running
echo "Checking if application is running..."
if curl -s http://localhost:8080/api/news > /dev/null 2>&1; then
    echo "✅ Application is running"
else
    echo "❌ Application is not running"
    echo ""
    echo "Please start the application first:"
    echo "  mvn spring-boot:run"
    echo ""
    exit 1
fi

echo ""
echo "========================================="
echo "Test 1: Fetching English Jobs in Germany"
echo "========================================="
echo ""

curl -s http://localhost:8080/api/news/jobs/germany | jq '.[0:2] | .[] | {title, company, location, salary, jobType, category, skills}'

echo ""
echo ""
echo "========================================="
echo "Test 2: Count Total Jobs"
echo "========================================="
echo ""

JOB_COUNT=$(curl -s http://localhost:8080/api/news/jobs/germany | jq '. | length')
echo "Total jobs returned: $JOB_COUNT"

echo ""
echo ""
echo "========================================="
echo "Test 3: Jobs by Category"
echo "========================================="
echo ""

curl -s http://localhost:8080/api/news/jobs/germany | jq 'group_by(.category) | .[] | {category: .[0].category, count: length}'

echo ""
echo ""
echo "========================================="
echo "Test 4: Refresh Jobs Endpoint"
echo "========================================="
echo ""

curl -s http://localhost:8080/api/news/jobs/germany/refresh | jq '.[0] | {title, company, location}'

echo ""
echo ""
echo "========================================="
echo "✅ All Tests Complete!"
echo "========================================="
echo ""
echo "API Endpoints available:"
echo "  GET /api/news/jobs/germany"
echo "  GET /api/news/jobs/germany/refresh"
echo ""

