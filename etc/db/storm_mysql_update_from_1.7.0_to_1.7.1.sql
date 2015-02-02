DELETE FROM storm_db.db_version;
INSERT INTO storm_db.db_version (major,minor,revision,description) VALUES (1,7,1,'27 Jan 2015');

INSERT INTO storm_db.config_Protocols VALUES ('xroot');