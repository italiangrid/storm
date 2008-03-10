/* 
 * CannotMapUserException
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 *
 * $Id: CannotMapUserException.java,v 1.2 2005/07/21 11:30:41 rmurri Exp $
 *
 */
package it.grid.storm.griduser;



/**
 * Signal that something went wrong during the LCMAPS call.
 */
public class CannotMapUserException 
	extends GridUserException {
	public CannotMapUserException (String message) {
		super (message);
	}
}
