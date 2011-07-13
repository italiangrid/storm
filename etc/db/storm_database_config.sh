#!/bin/bash
#
# The following environment variables are used by this script and can be set outside:
# STORM_MYSQL_HOSTNAME (hostname.domain which runs mysql)
# STORM_DBSCRIPT_DIR   (directory containing the StoRM scripts for the StoRM DB)
# MYSQL_PASSWORD       (mysql root password)


################################ Set environment variables ####################
STORM_DB_NAME=storm_db
if [ -z "$STORM_MYSQL_HOSTNAME" ]; then
    STORM_MYSQL_HOSTNAME=`hostname`
    # extract the short name (i.e. stop at the first dot)
    STORM_MYSQL_HOSTNAME_SHORT=`expr "$STORM_MYSQL_HOSTNAME" : '\([^.]*\)'`
fi

if [ -z "$STORM_DBSCRIPT_DIR" ]; then
    STORM_DBSCRIPT_DIR=/etc/storm/backend-server/db
fi

if [ -z "$MYSQL_PASSWORD" ]; then
    MYSQL_PASSWORD=storm
fi


############################### Function definition ###########################
function get_stormdb_version () {
    local MYSQL_OPTS="-h $STORM_MYSQL_HOSTNAME -u root ${MYSQL_PWD_OPTION} "
    local STORMDB_VERSION_MAJOR=`mysql $MYSQL_OPTS -s -e"use storm_db;select major from db_version;"`
    local STORMDB_VERSION_MINOR=`mysql $MYSQL_OPTS -s -e"use storm_db;select minor from db_version;"`
    local STORMDB_VERSION_REVISION=`mysql $MYSQL_OPTS -s -e"use storm_db;select revision from db_version;"`
    STORMDB_VERSION="$STORMDB_VERSION_MAJOR.$STORMDB_VERSION_MINOR.$STORMDB_VERSION_REVISION"
}

function get_stormbeISAM_version () {
    local MYSQL_OPTS="-h $STORM_MYSQL_HOSTNAME -u root ${MYSQL_PWD_OPTION} "
    local STORMBEISAM_VERSION_MAJOR=`mysql $MYSQL_OPTS -s -e"use storm_be_ISAM;select major from db_version;"`
    local STORMBEISAM_VERSION_MINOR=`mysql $MYSQL_OPTS -s -e"use storm_be_ISAM;select minor from db_version;"`
    local STORMBEISAM_VERSION_REVISION=`mysql $MYSQL_OPTS -s -e"use storm_be_ISAM;select revision from db_version;"`
    STORMBEISAM_VERSION="$STORMBEISAM_VERSION_MAJOR.$STORMBEISAM_VERSION_MINOR.$STORMBEISAM_VERSION_REVISION"
}

function set_transition_script_filename () {
    if [ -n "$STORMDB_VERSION" ]; then
       
        tmp=`ls $STORM_DBSCRIPT_DIR/storm_mysql_update_from_${STORMDB_VERSION}* 2>&1`  
       
        if [ $? -eq 0 ]; then
            TRANSITION_SCRIPT_FILENAME=$tmp
        else
            TRANSITION_SCRIPT_FILENAME=script_not_found # foo value, just a filename that doesn't exist
        fi
    else
        TRANSITION_SCRIPT_FILENAME=script_not_found # foo value, just a filename that doesn't exist
   fi
}

function set_stormbeISAM_transition_script_filename () {
    if [ -n "$STORMBEISAM_VERSION" ]; then
       
        tmp=`ls $STORM_DBSCRIPT_DIR/storm_be_ISAM_mysql_update_from_${STORMBEISAM_VERSION}* 2>&1`  
       
        if [ $? -eq 0 ]; then
            TRANSITION_SCRIPT_FILENAME=$tmp
        else
            TRANSITION_SCRIPT_FILENAME=script_not_found # foo value, just a filename that doesn't exist
        fi
    else
        TRANSITION_SCRIPT_FILENAME=script_not_found # foo value, just a filename that doesn't exist
   fi
}

function create_new_storm_db () {
    echo "Creating new db..."
    mysql -u root $MYSQL_PWD_OPTION < $STORM_DBSCRIPT_DIR/storm_mysql_tbl.sql
    tmp=`mktemp /tmp/sql.XXXXXX`

    sed s/__HOST__/${STORM_MYSQL_HOSTNAME_SHORT}/g $STORM_DBSCRIPT_DIR/storm_mysql_grant.sql | \
	sed s/__STORMUSER__/${STORM_DB_USER}/g | \
	sed s/__HOSTDOMAIN__/${STORM_MYSQL_HOSTNAME}/g > $tmp

    mysql -u root $MYSQL_PWD_OPTION < $tmp
    rm -f $tmp
    echo "Created new DB"
}

function update_storm_db () {
    get_stormdb_version
    set_transition_script_filename
    while [ "$TRANSITION_SCRIPT_FILENAME" != script_not_found ]
    do
        if [ -e "$TRANSITION_SCRIPT_FILENAME" ]; then
             mysql -u root $MYSQL_PWD_OPTION < $TRANSITION_SCRIPT_FILENAME
        fi
        get_stormdb_version
        set_transition_script_filename
        # After running the script the DB version should be changed, if not then
        # there is nothing else to do and the DB is up to date.
    done
    echo "Update done!"
}

function update_storm_be_ISAM () {
    get_stormbeISAM_version
    set_stormbeISAM_transition_script_filename
    while [ "$TRANSITION_SCRIPT_FILENAME" != script_not_found ]
    do
        if [ -e "$TRANSITION_SCRIPT_FILENAME" ]; then
             mysql -u root $MYSQL_PWD_OPTION < $TRANSITION_SCRIPT_FILENAME
        fi
        get_stormbeISAM_version
        set_stormbeISAM_transition_script_filename
        # After running the script the DB version should be changed, if not then
        # there is nothing else to do and the DB is up to date.
    done
    echo "Update done!"
}

################################## Main #######################################
# check for the existence of mysql
which mysql > /dev/null 2> /dev/null
if [ "$?" -ne 0 ] # check "which" exit status
then
    echo "Error: mysql not found (install mysql or add it to the PATH environment variable)."
    exit 1
fi

#echo "*** WARNING: When you are asked for a password, it's the 'root' MySQL user password. ***"
# check if mysql need a root password
mysql -u root -e ";" 2>/dev/null
if [ "$?" -ne 0 ]; then # the exit status is not zero
    MYSQL_PWD_OPTION="-p$MYSQL_PASSWORD";
else # the exit status is zero, i.e. no passwd
    MYSQL_PWD_OPTION=""
fi

# check that the storm database exists
mysql -h $STORM_MYSQL_HOSTNAME -u root ${MYSQL_PWD_OPTION} -e"use ${STORM_DB_NAME};" > /dev/null 2> /dev/null
if [ "$?" -ne 0 ]; then
    create_new_storm_db
else
    update_storm_db
    update_storm_be_ISAM
fi

exit 0

