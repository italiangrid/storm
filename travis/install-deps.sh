#!/bin/bash
set -ex

STORM_NATIVE_INTERFACE_REPO=${STORM_NATIVE_INTERFACE_REPO:-https://github.com/italiangrid/storm-native-libs.git}
STORM_NATIVE_INTERFACE_BRANCH=${STORM_NATIVE_INTERFACE_BRANCH:-develop}
NATIVE_INTERFACE_BUILD_DIR=/tmp/native-interface
NATIVE_INTERFACE_BUILD_LOG=/tmp/travis-native-interface-build.out

error_handler() {
  echo ERROR: An error was encountered installing dependencies 
  tail -1000 ${MITREID_BUILD_LOG}
  exit 1
}

trap 'error_handler' ERR

sudo apt-get -qq update
sudo apt-get install -y epel-release
sudo apt-get install -y jpackage-utils


git clone ${STORM_NATIVE_INTERFACE_REPO} ${NATIVE_INTERFACE_BUILD_DIR} 
pushd ${NATIVE_INTERFACE_BUILD_DIR}
git checkout ${STORM_NATIVE_INTERFACE_BRANCH}
mvn install > ${NATIVE_INTERFACE_BUILD_LOG} 2>&1
echo "StoRM native interface build completed succesfully"
tail -100 ${NATIVE_INTERFACE_BUILD_LOG}
popd