/* 
 * GridUserException
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 *
 * $Id: GridUserException.java,v 1.1 2005/07/21 11:31:30 rmurri Exp $
 *
 */
package it.grid.storm.griduser;

/**
 * Root class for errors arising with the GridUser instanciation.
 */
public class GridUserException 
	extends Exception {
	public GridUserException (String message) {
		super(message);
	}
}
