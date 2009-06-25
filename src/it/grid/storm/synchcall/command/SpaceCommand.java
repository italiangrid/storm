package it.grid.storm.synchcall.command;

import it.grid.storm.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpaceCommand implements Command {

    protected Logger log = LoggerFactory.getLogger(SpaceCommand.class);
    protected static Configuration  config        = Configuration.getInstance();


}
