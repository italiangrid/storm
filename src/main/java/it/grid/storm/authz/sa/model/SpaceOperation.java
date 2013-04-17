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

package it.grid.storm.authz.sa.model;

/**
 * RELEASE_SPACE (D) UPDATE_SPACE (U) READ_FROM_SPACE (R) WRITE_TO_SPACE (W)
 * STAGE_TO_SPACE (S) REPLICATE_FROM_SPACE(C) PURGE_FROM_SPACE (P) QUERY_SPACE
 * (Q) MODIFY_SPACE_ACL (M)
 **/

public enum SpaceOperation {
	RELEASE_SPACE('D', "RELEASE_SPACE", "Release space"), UPDATE_SPACE('U',
		"UPDATE_SPACE", "Update space"), READ_FROM_SPACE('R', "READ_FROM_SPACE",
		"Read from space"), WRITE_TO_SPACE('W', "WRITE_TO_SPACE", "Write to space"), STAGE_TO_SPACE(
		'S', "STAGE_TO_SPACE", "Stage to space"), REPLICATE_FROM_SPACE('C',
		"REPLICATE_FROM_SPACE", "Replicate from space"), PURGE_FROM_SPACE('P',
		"PURGE_FROM_SPACE", "Purge from space"), QUERY_SPACE('Q', "QUERY_SPACE",
		"Query space"), MODIFY_SPACE_ACL('M', "MODIFY_SPACE_ACL",
		"Modify space acl"), UNDEFINED('?', "UNDEFINED", "Undefined");

	private final char operation;
	private final String operationName;
	private final String operationDescription;

	private SpaceOperation(char operation, String spaceOpName, String spaceOpDesc) {

		this.operation = operation;
		operationName = spaceOpName;
		operationDescription = spaceOpDesc;
	}

	public static SpaceOperation getSpaceOperation(char op) {

		switch (op) {
		case 'D':
			return RELEASE_SPACE;
		case 'U':
			return UPDATE_SPACE;
		case 'R':
			return READ_FROM_SPACE;
		case 'W':
			return WRITE_TO_SPACE;
		case 'S':
			return STAGE_TO_SPACE;
		case 'C':
			return REPLICATE_FROM_SPACE;
		case 'P':
			return PURGE_FROM_SPACE;
		case 'Q':
			return QUERY_SPACE;
		case 'M':
			return MODIFY_SPACE_ACL;
		default:
			return UNDEFINED;
		}
	}

	@Override
	public String toString() {

		return String.valueOf(operationName);
	}

	public char getSpaceOperationValue() {

		return operation;
	}

	public SpaceOperation getSpaceOp(int ordinal) {

		SpaceOperation[] sp = SpaceOperation.values();
		if ((ordinal >= 0) && (ordinal < sp.length)) {
			return sp[ordinal];
		} else {
			return UNDEFINED;
		}
	}

	public int getNumberOfSpaceOp() {

		return SpaceOperation.values().length - 1;
	}

}
