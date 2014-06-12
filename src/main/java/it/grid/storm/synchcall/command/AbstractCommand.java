package it.grid.storm.synchcall.command;

import it.grid.storm.config.Configuration;


public abstract class AbstractCommand implements Command {
  
	protected static Configuration config = Configuration.getInstance();

}
