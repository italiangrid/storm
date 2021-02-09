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

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.converter.OverwriteModeConverter;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;

/**
 * @author Michele Dibenedetto
 * 
 */
public class AnonymousPrepareToPutInputData extends
	AnonymousFileTransferInputData implements PrepareToPutInputData {

	private TOverwriteMode overwriteMode = OverwriteModeConverter.getInstance()
		.toSTORM(Configuration.getInstance().getDefaultOverwriteMode());
	private TSizeInBytes fileSize = TSizeInBytes.makeEmpty();
	private TLifeTimeInSeconds desiredFileLifetime;

	/**
	 * @param user
	 * @param surl
	 * @param transferProtocols
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public AnonymousPrepareToPutInputData(TSURL surl, TURLPrefix transferProtocols)
		throws IllegalArgumentException, IllegalStateException {

		super(surl, transferProtocols);
		this.desiredFileLifetime = TLifeTimeInSeconds.make(Configuration
			.getInstance().getFileLifetimeDefault(), TimeUnit.SECONDS);

	}

	public AnonymousPrepareToPutInputData(TSURL surl,
		TURLPrefix transferProtocols, TLifeTimeInSeconds desiredFileLifetime)
		throws IllegalArgumentException, IllegalStateException {

		this(surl, transferProtocols);
		this.desiredFileLifetime = desiredFileLifetime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData#
	 * getOverwriteMode()
	 */
	@Override
	public TOverwriteMode getOverwriteMode() {

		return overwriteMode;
	}

	@Override
	public void setOverwriteMode(TOverwriteMode overwriteMode) {

		this.overwriteMode = overwriteMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData#getFileSize
	 * ()
	 */
	@Override
	public TSizeInBytes getFileSize() {

		return fileSize;
	}

	@Override
	public void setFileSize(TSizeInBytes fileSize) {

		this.fileSize = fileSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData#
	 * getDesiredFileLifetime()
	 */
	@Override
	public TLifeTimeInSeconds getDesiredFileLifetime() {

		return desiredFileLifetime;
	}

	@Override
	public void setDesiredFileLifetime(TLifeTimeInSeconds desiredFileLifetime) {

		this.desiredFileLifetime = desiredFileLifetime;
	}

}
