#!/bin/bash
set -e

export PING_SLEEP=30s
export BUILD_OUTPUT=travis/travis-build.out

dump_output() {
   echo Tailing the last 1000 lines of output:
   tail -1000 $BUILD_OUTPUT
}

error_handler() {
  echo ERROR: An error was encountered with the build.
  dump_output
  exit 1
}

trap 'error_handler' ERR

bash -c "while true; do echo \$(date) - building ...; sleep $PING_SLEEP; done" &
PING_LOOP_PID=$!

mvn clean package >> $BUILD_OUTPUT 2>&1
echo "StoRM build & test completed succesfully"
tail -100 $BUILD_OUTPUT
kill ${PING_LOOP_PID}
