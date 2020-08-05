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
 * 
 */
package it.grid.storm.authz.path.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zappi
 */
public enum SRMFileRequest {

	/**
	 * WRITE_FILE 'W' READ_FILE 'R' RENAME 'F' DELETE 'D' TRAVERSE_DIRECTORY 'T'
	 * LIST_DIRECTORY 'L' MAKE_DIRECTORY 'M' CREATE_FILE 'N' UNDEFINED '?'
	 **/

	// Operations to SURL
	PTP_Overwrite("srmPrepareToPut-overwrite", "PTP-Over",
		new ArrayList<PathOperation>() {

			private static final long serialVersionUID = 1L;

			{
				add(PathOperation.WRITE_FILE);
			}
		}),

	PTP("srmPrepareToPut", "PTP", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.CREATE_FILE);
			add(PathOperation.WRITE_FILE);
		}
	}),

	PTG("srmPrepareToGet", "PTG", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.READ_FILE);
		}
	}),

	CPto("srmCopy to", "CPto", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.WRITE_FILE);
			add(PathOperation.CREATE_FILE);
		}
	}),

	CPto_Overwrite("srmCopy to-overwrite", "CPto_Over",
		new ArrayList<PathOperation>() {

			private static final long serialVersionUID = 1L;

			{
				add(PathOperation.WRITE_FILE);
			}
		}),

	CPfrom("srmCopy from", "CPfrom", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.READ_FILE);
		}
	}),

	// OVERLOAD with OP
	RM("srmRemove", "RM", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.DELETE);
		}
	}),

	RMD("srmRemoveDir", "RMD", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.DELETE);
		}
	}),

	MD("srmMakeDir", "MD", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.MAKE_DIRECTORY);
		}
	}),

	LS("srmLS", "LS", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.LIST_DIRECTORY);
		}
	}),

	MV_source("srmMove-source", "MV-source", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.READ_FILE);
			add(PathOperation.DELETE);
		}
	}),

	MV_dest_Overwrite("srmMove-dest-overwrite", "MV-dest-Over",
		new ArrayList<PathOperation>() {

			private static final long serialVersionUID = 1L;

			{
				add(PathOperation.WRITE_FILE);
			}
		}),

	MV_dest("srmMove-dest-overwrite", "MV-dest", new ArrayList<PathOperation>() {

		private static final long serialVersionUID = 1L;

		{
			add(PathOperation.CREATE_FILE);
			add(PathOperation.WRITE_FILE);
		}
	});

	private final String description;
	private final String srmOp;
	private final PathAccessMask requestedPathOps;

	private static HashMap<String, SRMFileRequest> ops = new HashMap<String, SRMFileRequest>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("PTP-Over", PTP_Overwrite);
			put("srmPrepareToPut-overwrite", PTP_Overwrite);
			put("PTP", PTP);
			put("srmPrepareToPut", PTP);
			put("PTG", PTG);
			put("srmPrepareToGet", PTG);
			put("CPto_Over", CPto_Overwrite);
			put("srmCopy to-overwrite", CPto_Overwrite);
			put("CPto", CPto);
			put("srmCopy to", CPto);
			put("CPFrom", CPfrom);
			put("srmCopy from", CPfrom);
			put("RM", RM);
			put("srmRm", RM);
			put("RMD", RMD);
			put("srmRemoveDir", RM);
			put("MD", MD);
			put("srmMakeDir", MD);
			put("LS", LS);
			put("srmLs", LS);
			put("MV-source", MV_source);
			put("srmMove-source", MV_source);
			put("MV-dest-Over", MV_dest_Overwrite);
			put("srmMove-dest-overwrite", MV_dest_Overwrite);
			put("MV-dest", MV_dest);
			put("srmMove-dest", MV_dest);
		}
	};

	/*
	 * Used only for testing
	 */
	public static SRMFileRequest buildFromString(String srmOp) {

		if (ops.containsKey(srmOp)) {
			return ops.get(srmOp);
		} else {
			return null;
		}
	}

	/**
	 * SRMOperation
	 */
	private SRMFileRequest(String description, String srmOp,
		List<PathOperation> pathOps) {

		this.description = description;
		this.srmOp = srmOp;
		requestedPathOps = new PathAccessMask();
		for (PathOperation pathOp : pathOps) {
			requestedPathOps.addPathOperation(pathOp);
		}
	}

	public PathAccessMask getSRMPathAccessMask() {

		return requestedPathOps;
	}

	@Override
	public String toString() {

		return String.format("%s : %s = %s", srmOp, description, requestedPathOps);
	}

	public boolean isReadOnly() {

		for (PathOperation operation : requestedPathOps.getPathOperations()) {
			if (!operation.isReadOnly()) {
				return false;
			}
		}
		return true;
	}

}
