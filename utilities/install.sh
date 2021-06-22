#!/usr/bin/env bash

# install extract-data
pushd extract-data || exit 1
./gradlew clean installDist
popd || exit 1

cp -r extract-data/build/install/extract-data/* .