#!/usr/bin/env bash

set -e

./gradlew clean build jacocoTestReport
./gradlew bintrayUpload -PbintrayUser=$BINTRAY_USERNAME -PbintrayKey=$BINTRAY_KEY -PdryRun=false
bash <(curl -s https://codecov.io/bash)