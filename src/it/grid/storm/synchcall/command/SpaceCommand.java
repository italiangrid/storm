package it.grid.storm.synchcall.command;

import org.apache.log4j.Logger;


import it.grid.storm.config.Configuration;

public abstract class SpaceCommand implements Command {

    protected Logger log = Logger.getLogger("synch");
    protected static Configuration  config        = Configuration.getInstance();
    

}
