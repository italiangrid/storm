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

import it.grid.storm.config.Configuration;

/**
 * Class that represents a factory of GridFTPTransferClients. It allows for
 * the creation of clients from classes specified in the configuration file.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September 2005
 */
public class GridFTPTransferClientFactory {
    final private static GridFTPTransferClientFactory factory = new GridFTPTransferClientFactory(); //only instance of GridFTPTraqnsferClientFactory!

    private GridFTPTransferClientFactory() {}

    /**
     * Methos that returns the only instance of the factory.
     */
    public static GridFTPTransferClientFactory getInstance() {
        return factory;
    }

    /**
     * Method that returns a new instance of a GridFTPTransferClient. If the configured
     * class cannot be supplied, a NoGridFTPTransferClientFoundException is thrown.
     */
    synchronized public GridFTPTransferClient client() throws NoGridFTPTransferClientFoundException {
        try {
            String client = Configuration.getInstance().getGridFTPTransferClient();
            return (GridFTPTransferClient) Class.forName(client).newInstance();
        } catch (ClassNotFoundException e) {
            throw new NoGridFTPTransferClientFoundException(e.toString());
        } catch (InstantiationException e) {
            throw new NoGridFTPTransferClientFoundException(e.toString());
        } catch (IllegalAccessException e) {
            throw new NoGridFTPTransferClientFoundException(e.toString());
        }
    }
}
