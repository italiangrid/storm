/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * @file CannotGiveAway.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * 
 *         Source file for class CannotGiveAway
 * 
 */
/*
 * Copyright (c) 2006, Riccardo Murri <riccardo.murri@ictp.it> for the
 * EGRID/INFN joint project StoRM.
 * 
 * You may copy, distribute and modify this file under the terms of the
 * LICENSE.txt file at the root of the StoRM backend source tree.
 * 
 * $Id: CannotGiveAway.java,v 1.1 2006/03/31 13:35:01 rmurri Exp $
 */

package it.grid.storm.filesystem;

/**
 * Thrown when the StoRM process has insufficient privileges to change ownership
 * of a file.
 * 
 * Ownership change is a privileged operation on most POSIX systems, which
 * usually requires "root" privileges.
 * 
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class CannotGiveAway extends FilesystemError {

	public CannotGiveAway(final String msg) {

		super(msg);
	}
}
