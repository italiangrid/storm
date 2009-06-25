package it.grid.storm.synchcall.command;

import it.grid.storm.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DiscoveryCommand implements Command {

    protected Logger log = LoggerFactory.getLogger(DiscoveryCommand.class);
    protected static Configuration  config        = Configuration.getInstance();


}
