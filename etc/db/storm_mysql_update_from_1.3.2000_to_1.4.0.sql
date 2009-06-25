--- 
--- Update StoRM database from 1.3.20 to 1.4.00
---

USE storm_db;

REPLACE INTO db_version (major,minor,revision,description) VALUES (1,4,00,'1 Dec 2008');

USE storm_be_ISAM;

UPDATE storage_space SET storage_space.userdn='/DC=it/DC=infngrid/OU=Services/CN=storm'  WHERE storage_space.userdn='StoRM_admin';



