#!/bin/bash
# Strict mode for bash.
set -eou pipefail

APP="org.asheesh.beeware.pythontestsuite"
MAIN_ACTIVITY="org.asheesh.beeware.pythontestsuite.MainActivity"

# Tell bash to kill the logcat when this script exits.
trap "trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT

# Build and install the app on the most easily accessible Android device,
# with the cmake directory specifically on the PATH.
PATH="$PATH:${ANDROID_SDK_ROOT}/cmake/3.10.2.4988404/bin" ./gradlew installDebug

# Clear and then watch the log.
adb shell logcat -c
adb shell logcat &

# Stop the app, then launch it.
adb shell am force-stop "$APP" || true
adb shell am start "org.asheesh.beeware.pythontestsuite/org.asheesh.beeware.pythontestsuite.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER || true

# Wait infinitely for logcat.
tail -f /dev/null
