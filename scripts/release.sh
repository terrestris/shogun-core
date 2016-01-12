#!/bin/bash

# stop at first command failure
set -e

read -p "Do you really want to deploy to maven central repository (yes/no)? "

# check if prompted to continue
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# check if the input parameter RELEASE_VERSION is valid
RELEASE_VERSION="$1"
if [[ ! $RELEASE_VERSION =~ ^([0-9]+\.[0-9]+\.[0-9])(\-SNAPSHOT){0,1}$ ]]; then
    echo "Error: RELEASE_VERSION must be in X.Y.Z(-SNAPSHOT) format, but was $RELEASE_VERSION"
    exit 1
fi

SCRIPTDIR=`dirname "$0"`

pushd $SCRIPTDIR/../src/

mvn release:clean
mvn release:prepare --batch-mode -DreleaseVersion=$RELEASE_VERSION
mvn release:perform --batch-mode

popd

