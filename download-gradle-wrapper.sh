#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="${GRADLE_VERSION:-8.4}"

if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is not installed or not on PATH. Install Gradle ${GRADLE_VERSION} first, then rerun this script."
  exit 1
fi

echo "Generating an official Gradle wrapper with Gradle ${GRADLE_VERSION}..."
gradle wrapper --gradle-version "${GRADLE_VERSION}" --distribution-type bin

if [ -f "gradlew" ]; then
  chmod +x gradlew
  ./gradlew --version
  echo "Gradle wrapper successfully regenerated."
else
  echo "Wrapper generation failed: gradlew was not created."
  exit 1
fi
