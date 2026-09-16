#!/bin/bash

# Download gradle-wrapper.jar for Gradle 8.4
mkdir -p gradle/wrapper

echo "Downloading gradle-wrapper.jar..."
curl -L "https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradle/wrapper/gradle-wrapper.jar" \
  -o "gradle/wrapper/gradle-wrapper.jar"

if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "✓ gradle-wrapper.jar downloaded successfully"
  git add gradle/wrapper/gradle-wrapper.jar
  git commit -m "Add gradle-wrapper.jar binary"
  git push
else
  echo "✗ Failed to download gradle-wrapper.jar"
  exit 1
fi
