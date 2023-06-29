/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.space;

/**
 * @author Michele Dibenedetto
 * 
 */
public class StorageSpaceNotInitializedException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 6322292500826011705L;

	public StorageSpaceNotInitializedException(String string) {

		super(string);
	}
}
