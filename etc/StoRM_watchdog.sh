#!/bin/sh

# WatchDog for the StoRM Backend process.
# In case of crashes (damnd ldap...) the StoRM Backend will be restarted and the log will be saved with .crash estension.

STORM_DIR=/opt/storm
DATE=`date +%Y-%m-%d-%H:%M`

#
# Verify if the process is still alive.
#
$(ps aux | grep java > /tmp/WD.tmp)
number_of_process=$( cat /tmp/WD.tmp | grep " su storm -m -s /bin/sh -c umask 077 ; ?        java                  -cp '/opt/storm/lib/storm-backend/storm-backend.jar" | wc -l)

#echo $number_of_process;

#Check if the backend has been stopped correctly.

$(tail -n1 /opt/storm/var/log/storm-backend.log | grep "StoRM: Backend shutdown complete.")

if [ $? -ne 0 ] && [ $number_of_process -lt 1 ];  then
    echo "WatchDog: StoRM processes found: " $number_of_process >> $STORM_DIR/var/log/WD.log
    $(ps aux | grep java > /opt/storm/var/log/ciccio.log)
    #Restart will not work if the BE is already stopped, as in case of crashes.

    # Copy the Log
    cp $STORM_DIR/var/log/storm-backend.log  $STORM_DIR/var/log/WD.storm-backend.log.$DATE
    cp $STORM_DIR/var/log/storm-backend.stderr  $STORM_DIR/var/log/WD.storm-backend.stderr.$DATE
    cp $STORM_DIR/var/log/storm-backend.stdout  $STORM_DIR/var/log/WD.storm-backend.stdout.$DATE

    #Stop StoRM BE
    $STORM_DIR/etc/init.d/storm-backend stop


    #Start StoRM BE
    $STORM_DIR/etc/init.d/storm-backend start

  if [ $? -ne 0 ]; then
      echo "Bad thing. StoRM Backend is already down after the watchdog work..."
  fi

fi
