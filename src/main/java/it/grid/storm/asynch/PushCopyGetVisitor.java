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

package it.grid.storm.asynch;

import java.util.ArrayList;
import it.grid.storm.asynch.Copy.ResultType;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

/**
 * @author Michele Dibenedetto
 */
public class PushCopyGetVisitor implements CopyVisitor {

	@Override
	public Copy.Result visit(VisitableCopy copy) {

		try {
			RequestSummaryData ptgrsd = new RequestSummaryData(
				TRequestType.PREPARE_TO_GET, copy.getLocalrt(), copy.getGu());
			TURLPrefix turlPrefix = new TURLPrefix();
			turlPrefix.addProtocol(Protocol.FILE);
			PtGPersistentChunkData ptgChunkData = new PtGPersistentChunkData(
				copy.getGu(),
				copy.getLocalrt(),
				copy.getRequestData().getSURL(),
				copy.getRequestData().getLifetime(),
				new TDirOption(false, false, 0),
				turlPrefix,
				TSizeInBytes.makeEmpty(),
				new TReturnStatus(
					TStatusCode.SRM_REQUEST_QUEUED,
					"PushCopyChunk has queued this local srmPrepareToGet operation; srmCopy request on SURL"
						+ copy.getRequestData().getSURL()), TTURL.makeEmpty());
			copy.getLog().debug(
				"executeGetOperation: adding new chunkData to PtGCatalog!");
			PtGChunkCatalog.getInstance().add(ptgChunkData, copy.getGu());
			copy.getLog()
				.debug("executeGetOperation: finished adding to PtGCatalog!");

			GlobalStatusManager gsm = new GlobalStatusManager(copy.getLocalrt());
			gsm.addChunk(ptgChunkData);
			gsm.finishedAdding();
			PtGPersistentChunk ptgChunk = new PtGPersistentChunk(ptgrsd,
				ptgChunkData, gsm);
			copy.getLog().debug("executeGetOperation: starting ptgChunk.doIt()!");
			ptgChunk.doIt();
			copy.getLog().debug("executeGetOperation: finished ptgChunk.doIt()!");
			ArrayList<Object> parameters = new ArrayList<Object>(4);
			parameters.add(1, ptgChunkData.getStatus());
			parameters.add(2, ptgChunkData.getTransferURL());
			parameters.add(3, ptgChunkData.getFileSize());
			parameters.add(4, copy.getLocalrt());
			return copy.buildOperationResult(parameters, ResultType.GET);
		} catch (Exception e) {
			copy.getLog().error("ERROR IN PushCopyChunk! Cannot initiate local PtG! "
				+ "Requested SURL: {}", copy.getRequestData().getSURL());
			copy.getLog().error(e.getMessage(), e);
			return copy.buildOperationResult("Cannot initiate local PtG! " + e,
				ResultType.GET);
		}
	}

}
