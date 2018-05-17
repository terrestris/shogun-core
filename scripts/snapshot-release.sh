#!/bin/bash

# stop at first command failure
set -e

if [ "$TRAVIS" != "true" ]; then
    echo "This script is supposed to be run inside the travis environment."
    exit 1
fi

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    # only proceed if we were called by a merge (and not by a PR request)
    exit 1
fi

if [ "$TRAVIS_BRANCH" != "master" ]; then
    # only proceed if the target branch is master
    exit 1
fi

if [ "$TRAVIS_JDK_VERSION" != "oraclejdk8" ]; then
    # only proceed if the target JDK is oraclejdk8
    exit 1
fi

SCRIPTDIR=`dirname "$0"`

SETTINGSFILE=$SCRIPTDIR/settings.xml

mvn clean deploy --settings $SETTINGSFILE
