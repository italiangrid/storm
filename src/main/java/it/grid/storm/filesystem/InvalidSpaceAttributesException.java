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

package it.grid.storm.filesystem;

import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.common.types.SizeUnit;

/**
 * Class that represents an Exception thrown by the Space constructor if any of
 * the supplied parameters are null, or totalSize is Empty, or guaranteedSize is
 * greater than totalSize.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date May 2006
 */
public class InvalidSpaceAttributesException extends Exception {

	private boolean nullGuarSize = false; // boolean true if garanteedSize is null
	private boolean nullTotSize = false; // boolean true if totalSize is null
	private boolean nullSpaFil = false; // boolean true if spaceFile is null
	private boolean nullSS = false; // boolean true if SpaceSystem is null
	private boolean emptyTotSize = false; // boolean true if totalSize is Empty
	private boolean greater = false; // boolean true if guaranteedSize and
																		// totalSize are not null, not empty, and
																		// when interpreted as double of size BYTE
																		// it is _guaranteedSize_ that is GREATER
																		// than _totalSize_
	private double guaranteed = -1.0; // double that gets set only if (greater) is
																		// true, and represents _guaranteedSize_
																		// expressed in bytes.
	private double total = -1.0; // double that gets set only if (greater) is
																// true, and represents _totalSize_ expressed in
																// bytes.

	public InvalidSpaceAttributesException(TSizeInBytes guaranteedSize,
		TSizeInBytes totalSize, LocalFile spaceFile, SpaceSystem ss) {

		nullGuarSize = guaranteedSize == null;
		nullTotSize = totalSize == null;
		nullSpaFil = spaceFile == null;
		nullSS = ss == null;
		emptyTotSize = (!nullTotSize) && totalSize.isEmpty();
		greater = (!nullGuarSize)
			&& (!nullTotSize)
			&& (!guaranteedSize.isEmpty())
			&& (!totalSize.isEmpty())
			&& (guaranteedSize.getSizeIn(SizeUnit.BYTES) > totalSize
				.getSizeIn(SizeUnit.BYTES));
		if (greater) {
			guaranteed = guaranteedSize.getSizeIn(SizeUnit.BYTES);
			total = totalSize.getSizeIn(SizeUnit.BYTES);
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("InvalidSpaceAttributesException: nullGuaranteedSize=");
		sb.append(nullGuarSize);
		sb.append("; nullTotalSize=");
		sb.append(nullTotSize);
		sb.append("; nullSpaceFile=");
		sb.append(nullSpaFil);
		sb.append("; nullSpaceSystem=");
		sb.append(nullSS);
		sb.append("; emptyTotalSize=");
		sb.append(emptyTotSize);
		sb.append("; guaranteedSize greater than totalSize is ");
		sb.append(greater);
		if (greater)
			sb.append(" with guaranteed=");
		sb.append(guaranteed);
		sb.append(" and total=");
		sb.append(total);
		return sb.toString();
	}
}
