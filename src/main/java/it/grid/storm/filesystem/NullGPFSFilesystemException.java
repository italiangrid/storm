/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

/**
 * Class that represents an Exception thrown by the GPFSSpaceSystem if it is
 * instantited with a null GPFS filesystem
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date May 2006
 */
public class NullGPFSFilesystemException extends Exception {

	public String toString() {

		return "Supplied GPFS filesystem was null!";
	}
}
