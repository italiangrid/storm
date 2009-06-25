#!/bin/bash

###

##  Example of usage:
## #> ./bunch_test : Insert a $DEFAULT_NUMBER_OF_REQUEST of $DEFAULT_REQUEST_TYPE in the $DBHOST database for the surl: $SURL_PREFIX/$DEFAULT_FILE_DIR/$FILENAME.
## #> ./bunch_test PtP 100: Insert 100 PtP request in the $DBHOST database for the surl: $SURL_PREFIX/$DEFAULT_FILE_DIR/$FILENAME
## #> ./bunch_test PtP 100 /dteam/luca_test :  Insert 100 PtP request in the $DBHOST database for the surl: $SURL_PREFIX/dteam/luca_test/$FILENAME

###

### Configuration parameter

DBHOST=omii005.cnaf.infn.it
DBUSER=storm
DBPWD=boicottaritz
SURL_PREFIX="srm://omii003.cnaf.infn.it:8444"

## For PutDone operation

SRM_ENDPOINT=omii003.cnaf.infn.it:8444
CLIENT_SRM_PATH=/home/lucam/SRMv2Client


## Default values

DEFAULT_NUMBER_OF_REQUEST=100
DEFAULT_FILE_DIR="/dteam/bunch_test"
DEFAULT_REQUEST_TYPE="PtP"
DEFAULT_RTOKEN=`date +%s`
DEFAULT_FILENAME_BASE=testfile

###

### Dummy help
if [ $1 = "-h" ]; then
  echo ""
  echo "Usage: bunch_test <request_type:[PtP|PtG]> <number_of_request> <file_directory:[/dteam/dir]> <filename:[ciccio]> <token_base:[abc123>"
  echo ""
  echo "Example of usage:"
  echo "#> ./bunch_test : Insert a DEFAULT_NUMBER_OF_REQUEST of DEFAULT_REQUEST_TYPE in the DBHOST database for the surl: SURL_PREFIX/DEFAULT_FILE_DIR/FILENAME-[0-DEFAULT_NUMBER_OF_REQUESTS]"
  echo "#> ./bunch_test PtP 100: Insert 100 PtP request in the DBHOST database for the surl: SURL_PREFIX/DEFAULT_FILE_DIR/FILENAME_[0-100]"
  echo "#> ./bunch_test PtP 100 /dteam/luca_test :  Insert 100 PtP request in the DBHOST database for the surl: SURL_PREFIX/dteam/luca_test/FILENAME_[0-100]"
  echo "#>/bunch_test PtP 100 /dteam/luca_test ciccio my-token-00 :  Insert 100 PtP request in the DBHOST database for the surl: SURL_PREFIX/dteam/luca_test/ciccio_[0-100] with token starting from my-token-00-[0-100]"
  exit 0
fi
###

### Check request type
if [ -z "$1" ]; then
  REQ_TYPE=$DEFAULT_REQUEST_TYPE
else
  if [ $1 != "PtP" ] && [ $1 != "PtG" ] && [ $1 != "PD" ] ; then
    echo " ERROR: Request type specifed not supported! Aborting..."
    exit 0;
  else
    REQ_TYPE=$1
  fi
fi
###

### Check number of request
if [ -z "$2" ]; then
  NUMBER_OF_REQUEST=$DEFAULT_NUMBER_OF_REQUESTS
else
  NUMBER_OF_REQUEST=$2
fi
###

### Check directory prefix
if [ -z "$3" ]; then
  FILE_DIR=$DEFAULT_FILE_DIR
else
  FILE_DIR=$3
fi
###

### Check directory prefix
if [ -z "$4" ]; then
  FILE_NAME_BASE=$DEFAULT_FILENAME_BASE
else
  FILE_NAME_BASE=$4
fi
###


### Check TOKEN prefix
if [ -z "$5" ]; then
  RTOKEN_BASE=$DEFAULT_RTOKEN
else
  RTOKEN_BASE=$5
fi
###


echo "Adding $NUMBER_OF_REQUEST $REQ_TYPE SRM request with $SURL_PREFIX/$FILE_DIR/$FILE_NAME_BASE to MySQL host:$DBHOST with token base: $RTOKEN_BASE "


for i  in `seq $NUMBER_OF_REQUEST`

do

echo "iterate $i"

FILENAME=${FILE_NAME_BASE}_$i
RTOKEN=${RTOKEN_BASE}-$i


tmp=`mktemp /tmp/test.XXXXXX`

if [ $REQ_TYPE = "PtP" ]; then


cat >> $tmp <<EOF

use storm_db;

INSERT INTO request_queue (  config_FileStorageTypeID, config_OverwriteID, config_RequestTypeID, client_dn, u_token, pinLifetime, fileLifetime, r_token, s_token, status, nbreqfiles, numOfCompleted, numOfWaiting, numOfFailed, proxy, timeStamp) values (NULL, NULL, 'PTP', '/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni', NULL, NULL, NULL, '$RTOKEN', NULL, 17, 1, 0, 1,0 , '/dteam/Role=NULL/Capability=NULL#/dteam/italy/Role=NULL/Capability=NULL#/dteam/italy/INFN-CNAF/Role=NULL/Capability=NULL',  current_timestamp());

INSERT INTO request_Put (targetSURL, expectedFileSize, request_queueID) VALUES ('$SURL_PREFIX/$FILE_DIR/$FILENAME', NULL, LAST_INSERT_ID());

INSERT INTO status_Put (request_PutID, statusCode) values (LAST_INSERT_ID(), 17);

EOF

mysql -u $DBUSER -h $DBHOST -p$DBPWD < $tmp
rm -f $tmp

else 
  
if [ $REQ_TYPE = "PtG" ]; then

cat >> $tmp <<EOF

use storm_db;

INSERT INTO request_queue (  config_FileStorageTypeID, config_RequestTypeID, client_dn, u_token, pinLifetime, r_token, s_token, status, nbreqfiles, numOfCompleted, numOfWaiting, numOfFailed, proxy, timeStamp) values (NULL, 'PTG', '/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni', NULL, NULL, '$RTOKEN', NULL, 17, 1, 0, 1,0 , '/dteam/Role=NULL/Capability=NULL#/dteam/italy/Role=NULL/Capability=NULL#/dteam/italy/INFN-CNAF/Role=NULL/Capability=NULL', current_timestamp());

INSERT INTO request_Get (sourceSURL, request_DirOptionID, request_queueID) VALUES ('$SURL_PREFIX/$FILE_DIR/$FILENAME', NULL, LAST_INSERT_ID());

INSERT INTO status_Get (request_GetID, statusCode) values (LAST_INSERT_ID(), 17);

EOF

mysql -u $DBUSER -h $DBHOST -p$DBPWD < $tmp
rm -f $tmp


else

if [ $REQ_TYPE = "PD" ]; then
  #echo " $CLIENT_SRM_PATH/clientSRM pd -e $SRM_ENDPOINT -s $SURL_PREFIX/$FILE_DIR/$FILENAME -t $RTOKEN &"
  $CLIENT_SRM_PATH/clientSRM pd -e $SRM_ENDPOINT -s $SURL_PREFIX/$FILE_DIR/$FILENAME -t $RTOKEN &

fi 

fi

fi 




done

echo "Done! All requests insterted in DB."



