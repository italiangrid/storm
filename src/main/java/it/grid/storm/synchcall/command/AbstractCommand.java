/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command;

import it.grid.storm.config.StormConfiguration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;


public abstract class AbstractCommand implements Command {
  
	protected static StormConfiguration config = StormConfiguration.getInstance();

	public static GridUserInterface getUserFromInputData(InputData id){
	  
	  if (id instanceof IdentityInputData){
	    return ((IdentityInputData)id).getUser();
	  } 
	  return null;
	}
}
