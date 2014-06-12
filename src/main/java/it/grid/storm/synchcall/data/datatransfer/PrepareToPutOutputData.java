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

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

/**
 * @author Enrico Vianello
 */
public class PrepareToPutOutputData extends FileTransferOutputData {

	public PrepareToPutOutputData(TSURL surl, TTURL turl, TReturnStatus status,
		TRequestToken requestToken) throws IllegalArgumentException {

		super(surl, turl, status, requestToken);
	}

	@Override
	public boolean isSuccess() {

		return this.getStatus().getStatusCode().equals(TStatusCode.SRM_SPACE_AVAILABLE);
	}
}
