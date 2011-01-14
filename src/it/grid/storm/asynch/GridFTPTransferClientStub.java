/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.srm.types.TTURL;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents a Stub for the GridFTPTransferClient. The methods implemeted
 * do not carry out any action.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    September, 2005
 */
public class GridFTPTransferClientStub implements GridFTPTransferClient {

    /**
     * This is a stub implementation that does nothing.
     */
    public void putFile(GridUserInterface gu, TTURL local, TTURL remote) throws GridFTPTransferClientException {
        if (false) throw new GridFTPTransferClientException("Exception in stub! Should not occur!");
    }
}
