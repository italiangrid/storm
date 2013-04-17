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

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

/**
 * Class that represents a generic chunk. It provides only one method which is
 * the primary key associated ot the chunk in persistence.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2006
 */
public interface RequestData extends InputData {

	public TRequestToken getRequestToken();

	public abstract TReturnStatus getStatus();

	public abstract TSURL getSURL();

	public abstract void changeStatusSRM_ABORTED(String explanation);

	public abstract void changeStatusSRM_FILE_BUSY(String explanation);

	public abstract void changeStatusSRM_INVALID_PATH(String explanation);

	public abstract void changeStatusSRM_AUTHORIZATION_FAILURE(String explanation);

	public abstract void changeStatusSRM_INVALID_REQUEST(String explanation);

	public abstract void changeStatusSRM_INTERNAL_ERROR(String explanation);

	public abstract void changeStatusSRM_NOT_SUPPORTED(String explanation);

	public abstract void changeStatusSRM_FAILURE(String explanation);

	public abstract void changeStatusSRM_SUCCESS(String explanation);

	public abstract void changeStatusSRM_SPACE_LIFETIME_EXPIRED(String explanation);

	public abstract void changeStatusSRM_REQUEST_INPROGRESS(String explanation);

	public abstract void changeStatusSRM_REQUEST_QUEUED(String explanation);
}
