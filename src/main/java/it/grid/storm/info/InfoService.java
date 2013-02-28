package it.grid.storm.info;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InfoService {

    private static final Logger log = LoggerFactory.getLogger(InfoService.class);

    public static String getResourcePackage() {
    	return "it.grid.storm.info.remote.resources";
    }
    
    
	
}
