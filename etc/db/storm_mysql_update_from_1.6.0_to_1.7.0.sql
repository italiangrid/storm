--
-- Update StoRM database from 1.6.0 to 1.7.0
--

DELETE FROM storm_db.db_version;
INSERT INTO storm_db.db_version (major,minor,revision,description) VALUES (1,7,0,'27 May 2011');

INSERT INTO storm_db.config_Protocols (ID) VALUES ('http');
INSERT INTO storm_db.config_Protocols (ID) VALUES ('https');