#!/bin/bash
# Startup script for Railway deployment

# Find the JAR file in target directory
JAR_FILE=$(find target -name "ai-news-app-*.jar" -type f | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "Error: JAR file not found in target directory"
    echo "Contents of target directory:"
    ls -la target/ 2>/dev/null || echo "target directory does not exist"
    exit 1
fi

echo "Starting application with JAR: $JAR_FILE"
java -jar "$JAR_FILE"
