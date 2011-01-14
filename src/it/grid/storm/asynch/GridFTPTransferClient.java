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
 * Interface that represents a GridFTP client: it supplies functionality to execute
 * a file transfer through the GSIFTP protocol.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September 2005
 */
public interface GridFTPTransferClient {

    /**
     * Method used to transfer a local file to a remote location; it needs the GridUser whose
     * credentails (proxy) will be used forthe transfer, a local TURL that designates a file and
     * so must have file as protocol, a remote TURL that designates the destination and so must
     * have gsiftp as protocol.
     *
     * If any problem does not allow the transfer to proceed, a GridFTPTransferClientException is
     * thrown containing a String that explains what went wrong.
     */
    public void putFile(GridUserInterface gu, TTURL local, TTURL remote) throws GridFTPTransferClientException;
}
