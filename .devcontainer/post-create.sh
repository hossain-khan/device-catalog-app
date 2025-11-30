#!/bin/bash

set -e

echo "🚀 Setting up Android development environment..."

# Configuration - Update these versions as needed
CMDLINE_TOOLS_VERSION="11076708"
ANDROID_PLATFORM_VERSION="36"
BUILD_TOOLS_VERSION="36.0.0"

# Define Android SDK paths
export ANDROID_HOME="/usr/local/lib/android/sdk"
export ANDROID_SDK_ROOT="${ANDROID_HOME}"
export PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"

# Install Android SDK Command Line Tools if not present
if [ ! -d "${ANDROID_HOME}/cmdline-tools" ]; then
    echo "📥 Downloading Android SDK Command Line Tools (version ${CMDLINE_TOOLS_VERSION})..."
    CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
    sudo mkdir -p "${ANDROID_HOME}/cmdline-tools"
    cd /tmp
    wget -q "${CMDLINE_TOOLS_URL}" -O commandlinetools.zip
    unzip -q commandlinetools.zip
    sudo mv cmdline-tools "${ANDROID_HOME}/cmdline-tools/latest"
    rm commandlinetools.zip
    cd -
    
    # Set proper ownership and permissions
    sudo chown -R vscode:vscode "${ANDROID_HOME}"
    sudo chmod -R 755 "${ANDROID_HOME}"
fi

# Accept Android SDK licenses
# Note: License acceptance may output warnings that are safe to ignore
echo "📝 Accepting Android SDK licenses..."
if ! yes | sdkmanager --licenses > /dev/null 2>&1; then
    echo "⚠️ Some licenses may not have been accepted. Run 'yes | sdkmanager --licenses' manually if needed."
fi

# Install required Android SDK components
echo "📦 Installing Android SDK components (Platform ${ANDROID_PLATFORM_VERSION}, Build Tools ${BUILD_TOOLS_VERSION})..."
if ! sdkmanager "platform-tools" "platforms;android-${ANDROID_PLATFORM_VERSION}" "build-tools;${BUILD_TOOLS_VERSION}"; then
    echo "⚠️ SDK component installation had issues. Some components may need manual installation."
fi

# Update SDK components
echo "🔄 Updating SDK components..."
if ! sdkmanager --update; then
    echo "⚠️ SDK update had issues. Run 'sdkmanager --update' manually if needed."
fi

# Set proper permissions for Gradle wrapper
echo "🔧 Setting Gradle wrapper permissions..."
if [ -f ./gradlew ]; then
    chmod +x ./gradlew
else
    echo "⚠️ gradlew not found. Make sure you're in the project root directory."
fi

# Install Gradle dependencies (helps with IDE indexing)
echo "📚 Downloading Gradle dependencies..."
if [ -f ./gradlew ]; then
    if ! ./gradlew --version; then
        echo "⚠️ Gradle version check failed. You may need to run './gradlew --version' manually."
    fi
else
    echo "⚠️ Skipping Gradle setup - gradlew not found."
fi

echo "✅ Android development environment setup complete!"
echo "📱 You can now build the project with: ./gradlew build"
echo "🧪 Run tests with: ./gradlew test"
echo "🎨 Format code with: ./gradlew formatKotlin"
echo "🔍 Lint code with: ./gradlew lintKotlin"
