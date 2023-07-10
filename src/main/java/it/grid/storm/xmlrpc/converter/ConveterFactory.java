/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter;

import it.grid.storm.common.OperationType;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;
import it.grid.storm.xmlrpc.converter.datatransfer.AbortFilesConverter;
import it.grid.storm.xmlrpc.converter.datatransfer.AbortRequestConverter;
import it.grid.storm.xmlrpc.converter.datatransfer.ExtendFileLifeTimeConverter;
import it.grid.storm.xmlrpc.converter.datatransfer.ManageFileTransferRequestConverter;
import it.grid.storm.xmlrpc.converter.datatransfer.PrepareToGetRequestConverter;
import it.grid.storm.xmlrpc.converter.datatransfer.PrepareToPutRequestConverter;
import it.grid.storm.xmlrpc.converter.datatransfer.PutDoneConverter;
import it.grid.storm.xmlrpc.converter.directory.LsConverter;
import it.grid.storm.xmlrpc.converter.directory.MkdirConverter;
import it.grid.storm.xmlrpc.converter.directory.MvConverter;
import it.grid.storm.xmlrpc.converter.directory.RmConverter;
import it.grid.storm.xmlrpc.converter.directory.RmdirConverter;
import it.grid.storm.xmlrpc.converter.discovery.PingConverter;
import it.grid.storm.xmlrpc.converter.space.GetSpaceMetaDataConverter;
import it.grid.storm.xmlrpc.converter.space.GetSpaceTokensConverter;
import it.grid.storm.xmlrpc.converter.space.ReleaseSpaceConverter;
import it.grid.storm.xmlrpc.converter.space.ReserveSpaceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ************************************************************************ This file is part of the
 * StoRM project. Copyright (c) 2008 INFN-CNAF.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * ************************************************************************ This class is part of
 * the StoRM project. Copyright (c) 2008 INFN-CNAF.
 *
 * <p>Authors:
 *
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * @date = Oct 9, 2008
 */
public class ConveterFactory {

  private static final Logger log = LoggerFactory.getLogger(ConveterFactory.class);

  /**
   * @param type
   * @return null if no converter is available for the provided OperationType
   */
  public static Converter getConverter(OperationType type) throws StoRMXmlRpcException {

    switch (type) {
      case RM:
        return new RmConverter();
      case RMD:
        return new RmdirConverter();
      case MKD:
        return new MkdirConverter();
      case MV:
        return new MvConverter();
      case LS:
        return new LsConverter();

      case PNG:
        return new PingConverter();

      case GSM:
        return new GetSpaceMetaDataConverter();
      case GST:
        return new GetSpaceTokensConverter();
      case RESSP:
        return new ReserveSpaceConverter();
      case RELSP:
        return new ReleaseSpaceConverter();

      case PD:
        return new PutDoneConverter();
      case RF:
        return new ManageFileTransferRequestConverter();
      case EFL:
        return new ExtendFileLifeTimeConverter();
      case AF:
        return new AbortFilesConverter();
      case AR:
        return new AbortRequestConverter();

      case PTP:
        return new PrepareToPutRequestConverter();
      case SPTP:
        return new ManageFileTransferRequestConverter();
      case PTG:
        return new PrepareToGetRequestConverter();
      case SPTG:
        return new ManageFileTransferRequestConverter();
      default:
        log.error("No Converter available for OperationType {}", type);
        throw new StoRMXmlRpcException("No Converter available for OperationType " + type);
    }
  }
}
