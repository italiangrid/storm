/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import java.io.*;

/**
 * This class represents an Exception throws if getChildren is request on PATH
 * that does not exist. *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidDescendantsPathRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private boolean pathNotExists = false;

	public InvalidDescendantsPathRequestException(File fh) {

		this.pathNotExists = !fh.isFile();
	}

	public String toString() {

		return ("PATH specified NOT EXISTS = " + pathNotExists);
	}
}
