--###################################################
--# Copyright (c) 2008 on behalf of the INFN CNAF 
--# The Italian National Institute for Nuclear Physics (INFN), 
--# All rights reserved.
--#
--# createrole sql script for a database
--#
--# author: luca.magnoni@cnaf.infn.it
--# contributes: flavia.donno@cern.ch
--# changelog: Added grant permission on storm_be_ISAM database.
--#
--#
--###################################################
USE mysql;
GRANT ALL PRIVILEGES ON storm_db.* TO __STORMUSER__ IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_db.* TO __STORMUSER__@'localhost' IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_db.* TO __STORMUSER__@'__HOST__' IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_db.* TO __STORMUSER__@'__HOSTDOMAIN__' IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;

GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO __STORMUSER__ IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO __STORMUSER__@'localhost' IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO __STORMUSER__@'__HOST__' IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON storm_be_ISAM.* TO __STORMUSER__@'__HOSTDOMAIN__' IDENTIFIED BY '__STORMUSER__' WITH GRANT OPTION;


FLUSH PRIVILEGES;


