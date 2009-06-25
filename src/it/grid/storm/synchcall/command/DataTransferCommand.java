package it.grid.storm.synchcall.command;

import it.grid.storm.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataTransferCommand implements Command {

    protected Logger log = LoggerFactory.getLogger(DataTransferCommand.class);
    protected static Configuration  config        = Configuration.getInstance();


}
