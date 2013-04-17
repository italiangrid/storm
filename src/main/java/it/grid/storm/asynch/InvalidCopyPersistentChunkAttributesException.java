/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.catalogs.CopyPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidCopyPersistentChunkAttributesException extends
	InvalidCopyAttributesException {

	private static final long serialVersionUID = -474846490237202316L;
	protected boolean nullrsd = true;
	protected boolean nullgsm = true;

	/**
	 * Constructor that requires the GridUser, RequestSummaryData and
	 * CopyChunkData, as well as the int counter, that caused the exception to be
	 * thrown.
	 */
	public InvalidCopyPersistentChunkAttributesException(GridUserInterface gu,
		RequestSummaryData rsd, CopyPersistentChunkData requestData, int n,
		GlobalStatusManager gsm) {

		super(gu, requestData, n);
		nullrsd = (rsd == null);
		nullgsm = (gsm == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("InvalidCopyPersistentChunkAttributesException [nullrsd=");
		builder.append(nullrsd);
		builder.append(", nullgsm=");
		builder.append(nullgsm);
		builder.append(", negativeN=");
		builder.append(negativeN);
		builder.append(", nullGu=");
		builder.append(nullGu);
		builder.append(", nullChunkData=");
		builder.append(nullChunkData);
		builder.append("]");
		return builder.toString();
	}
}
