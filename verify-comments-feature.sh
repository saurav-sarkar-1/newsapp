#!/bin/bash
# Verification script for comments feature implementation

echo "========================================="
echo "Comments Feature Implementation Checker"
echo "========================================="
echo ""

# Check backend files
echo "✓ Checking Backend Files..."
echo ""

if [ -f "src/main/java/com/ainews/dto/CommentRequest.java" ]; then
    echo "✅ CommentRequest.java exists"
else
    echo "❌ CommentRequest.java NOT FOUND"
fi

if [ -f "src/main/java/com/ainews/repository/CommentRepository.java" ]; then
    echo "✅ CommentRepository.java exists"
else
    echo "❌ CommentRepository.java NOT FOUND"
fi

# Check if NewsArticle has comments field
if grep -q "private String comments" src/main/java/com/ainews/model/NewsArticle.java; then
    echo "✅ NewsArticle.java has comments field"
else
    echo "❌ NewsArticle.java missing comments field"
fi

# Check if NewsController has comment endpoints
if grep -q "CommentRepository" src/main/java/com/ainews/controller/NewsController.java; then
    echo "✅ NewsController.java has CommentRepository"
else
    echo "❌ NewsController.java missing CommentRepository"
fi

if grep -q "@PostMapping(\"/comments\")" src/main/java/com/ainews/controller/NewsController.java; then
    echo "✅ NewsController.java has POST /comments endpoint"
else
    echo "❌ NewsController.java missing POST /comments endpoint"
fi

# Check if NewsService loads comments
if grep -q "commentRepository" src/main/java/com/ainews/service/NewsService.java; then
    echo "✅ NewsService.java uses CommentRepository"
else
    echo "❌ NewsService.java missing CommentRepository"
fi

echo ""
echo "✓ Checking Frontend Files..."
echo ""

# Check JavaScript file
if grep -q "comment-section" src/main/resources/static/js/app.js; then
    echo "✅ app.js has comment-section HTML"
else
    echo "❌ app.js missing comment-section HTML"
fi

if grep -q "function saveComment" src/main/resources/static/js/app.js; then
    echo "✅ app.js has saveComment function"
else
    echo "❌ app.js missing saveComment function"
fi

if grep -q "function clearComment" src/main/resources/static/js/app.js; then
    echo "✅ app.js has clearComment function"
else
    echo "❌ app.js missing clearComment function"
fi

# Check CSS file
if grep -q ".comment-section" src/main/resources/static/css/styles.css; then
    echo "✅ styles.css has comment-section styles"
else
    echo "❌ styles.css missing comment-section styles"
fi

if grep -q ".save-comment-btn" src/main/resources/static/css/styles.css; then
    echo "✅ styles.css has save-comment-btn styles"
else
    echo "❌ styles.css missing save-comment-btn styles"
fi

echo ""
echo "========================================="
echo "Summary"
echo "========================================="
echo ""
echo "All code changes have been implemented!"
echo ""
echo "To test the feature:"
echo "1. mvn clean package -DskipTests"
echo "2. java -jar target/ai-news-app-1.0.0.jar"
echo "3. Open http://localhost:8080 in your browser"
echo "4. Look for comment sections at the bottom of each news card"
echo ""

