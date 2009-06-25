package it.grid.storm.synchcall.command;

import it.grid.storm.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DirectoryCommand implements Command {

    protected Logger log = LoggerFactory.getLogger(DirectoryCommand.class);
    protected static Configuration  config        = Configuration.getInstance();


}
