#!/bin/bash
# Strict mode for bash.
set -eou pipefail

APP="org.asheesh.beeware.pythontestsuite"
MAIN_ACTIVITY="org.asheesh.beeware.pythontestsuite.MainActivity"

# Tell bash to kill the logcat when this script exits.
trap "trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT

# Build and install the app on the most easily accessible Android device.
./gradlew installDebug

# Clear and then watch the log.
adb shell logcat -c
adb shell logcat &

# Stop the app, then launch it.
adb shell am force-stop "$APP"
USER_ID="$(adb shell dumpsys package org.asheesh.beeware.pythontestsuite | grep userId | sed s,userId=,,)"
adb shell am start "org.asheesh.beeware.pythontestsuite/org.asheesh.beeware.pythontestsuite.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

# Wait infinitely for logcat.
sleep infinity
