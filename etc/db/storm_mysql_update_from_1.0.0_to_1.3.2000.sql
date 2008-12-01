--- 
--- Update StoRM database from 1.3.19 to 1.3.20.
--- Please not that the version of database in the 1.3.19 relase  is 1.0.0
---

USE storm_db;

REPLACE INTO db_version (major,minor,revision,description) VALUES (1,3,2000,'05 March 2008');

REPLACE INTO config_Protocols (ID) VALUES ('root');


