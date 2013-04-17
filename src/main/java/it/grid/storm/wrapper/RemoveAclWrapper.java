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
 * Class for Remove ACL native Library. This class provide method to remove ACL
 * using native library provided by StoRM. Return: 1 if success -1 if FIle does
 * not exist. -2 if Error in exec acl command.
 */
public class RemoveAclWrapper {

	/**
	 * Logger. This Logger it's used to log information.
	 */
	private static final Logger log = LoggerFactory
		.getLogger(RemoveAclWrapper.class);

	native int removeAcl(String tempDir, String path, String user);

	static {
		// System.out.println("File: "+pathToFile+", size = "+ size );
		try {
			System.loadLibrary("removeaclnativelib");
		} catch (UnsatisfiedLinkError e) {
			log.error("RemoveACL native library failed to load!", e);
			System.exit(1);
		}
	}

}
