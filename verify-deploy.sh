#!/bin/sh
# Verify all deployment files are correct before pushing

echo "🔍 Checking Railway deployment configuration..."
echo ""

# Check railway.json
echo "✅ railway.json:"
if grep -q "DOCKERFILE" railway.json; then
    echo "   ✓ Using Dockerfile builder"
else
    echo "   ✗ NOT using Dockerfile - FIX NEEDED"
    exit 1
fi

# Check Dockerfile
echo ""
echo "✅ Dockerfile:"
if grep -q "COPY --from=build /app/target/\*.jar" Dockerfile; then
    echo "   ✓ Copying JAR files with wildcard (flexible)"
elif grep -q "ai-news-app-1.0.0.jar" Dockerfile; then
    echo "   ✓ Copying explicit JAR file"
else
    echo "   ✗ JAR file copy not found - FIX NEEDED"
    exit 1
fi

if grep -q "mvn clean package" Dockerfile; then
    echo "   ✓ Maven build command present"
else
    echo "   ✗ Maven build missing - FIX NEEDED"
    exit 1
fi

# Check pom.xml
echo ""
echo "✅ pom.xml:"
if grep -q "<finalName>\${project.artifactId}-\${project.version}</finalName>" pom.xml; then
    echo "   ✓ JAR name will be: ai-news-app-1.0.0.jar"
else
    echo "   ⚠ Custom JAR naming - verify Dockerfile matches"
fi

# Check target folder (local)
echo ""
echo "✅ Local build verification:"
if [ -f "target/ai-news-app-1.0.0.jar" ]; then
    echo "   ✓ JAR exists locally: target/ai-news-app-1.0.0.jar"
    ls -lh target/ai-news-app-1.0.0.jar
else
    echo "   ⚠ JAR not found locally (run: mvn clean package)"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ ALL CHECKS PASSED!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🚀 Ready to deploy! Run:"
echo ""
echo "   git add ."
echo "   git commit -m \"Fix Railway deployment with Docker\""
echo "   git push"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

