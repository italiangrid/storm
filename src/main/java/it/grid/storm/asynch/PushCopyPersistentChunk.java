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

import it.grid.storm.catalogs.CopyData;
import it.grid.storm.catalogs.CopyPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSizeInBytes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SubClass of CopyChunk that handles Push mode, that is, the SRM server that
 * receives the srmCopy request transfers a file from itself (local) to another
 * SRM server (remote).
 * 
 * The executeGetOpeartion method is overwritten with logic to handle a local
 * srmPreparToGet; the executePutOperation method is overwritten with logic to
 * handle a remote srmPrepareToPut through the use of an internal SRMClient; the
 * executeTransfer method is overwritten to carry out a GridFTP put of the local
 * file to the remote locattion.
 * 
 * @author EGRID - ICTP Trieste
 * @date September, 2005
 * @version 2.0
 */
public class PushCopyPersistentChunk extends CopyPersistentChunk implements
	VisitableCopy {

	public PushCopyPersistentChunk(GridUserInterface gu, RequestSummaryData rsd,
		CopyPersistentChunkData requestData, int n, GlobalStatusManager gsm)
		throws InvalidCopyAttributesException,
		InvalidCopyPersistentChunkAttributesException {

		super(gu, rsd, requestData, n, gsm);
	}

	private static Logger log = LoggerFactory
		.getLogger(PushCopyPersistentChunk.class);

	@Override
	protected GetOperationResult executeGetOperation() {

		PushCopyGetVisitor visitor = new PushCopyGetVisitor();
		return (GetOperationResult) visitor.visit(this);
	}

	@Override
	protected PutOperationResult executePutOperation(TSizeInBytes getFileSize) {

		PushCopyPutVisitor visitor = new PushCopyPutVisitor(getFileSize);
		return (PutOperationResult) visitor.visit(this);
	}

	@Override
	protected TransferResult executeTransfer(GetOperationResult get,
		PutOperationResult put) {

		PushCopyTransferVisitor visitor = new PushCopyTransferVisitor(get, put);
		return (TransferResult) visitor.visit(this);
	}

	@Override
	public TRequestToken getLocalrt() {

		return this.localrt;
	}

	@Override
	public GridUserInterface getGu() {

		return this.gu;
	}

	@Override
	public CopyData getRequestData() {

		return this.requestData;
	}

	@Override
	public Logger getLog() {

		return PushCopyPersistentChunk.log;
	}

	@Override
	public Result buildOperationResult(String string, Copy.ResultType type)
		throws IllegalArgumentException {

		return OperationResultBuilder.build(this, string, type);
	}

	@Override
	public Result buildOperationResult(List<Object> arguments, ResultType type)
		throws IllegalArgumentException {

		return OperationResultBuilder.build(this, arguments, type);
	}

}
