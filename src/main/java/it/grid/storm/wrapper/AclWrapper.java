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

package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Native ACL support. This class provide add acl functionality using
 * native library provided by StoRM.
 * 
 * path : Path of destination File. user : local user or group acl : acl in unix
 * like form (user:username:r--).
 * 
 * return: 1 if success -1 Path specified does not exist. -2 Error in exec
 * addacl command. -3 Error in ACL Format Error.
 */

public class AclWrapper {

	/**
	 * Logger. This Logger it's used to log information.
	 */
	private static final Logger log = LoggerFactory.getLogger(AclWrapper.class);

	native int addAcl(String tempDir, String path, String user, String acl);

	static {
		// System.out.println("File: "+pathToFile+", size = "+ size );
		try {
			System.loadLibrary("aclnativelib");
		} catch (UnsatisfiedLinkError e) {
			log.error("ACL native library failed to load!\n", e);
			System.exit(1);
		}
	}

}
