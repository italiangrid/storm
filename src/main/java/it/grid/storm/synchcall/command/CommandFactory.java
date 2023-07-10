/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command;

import it.grid.storm.common.OperationType;
import it.grid.storm.synchcall.command.datatransfer.AbortFilesCommand;
import it.grid.storm.synchcall.command.datatransfer.AbortRequestCommand;
import it.grid.storm.synchcall.command.datatransfer.ExtendFileLifeTimeCommand;
import it.grid.storm.synchcall.command.datatransfer.PrepareToGetRequestCommand;
import it.grid.storm.synchcall.command.datatransfer.PrepareToGetRequestStatusCommand;
import it.grid.storm.synchcall.command.datatransfer.PrepareToPutRequestCommand;
import it.grid.storm.synchcall.command.datatransfer.PrepareToPutRequestStatusCommand;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;
import it.grid.storm.synchcall.command.datatransfer.ReleaseFilesCommand;
import it.grid.storm.synchcall.command.directory.LsCommand;
import it.grid.storm.synchcall.command.directory.MkdirCommand;
import it.grid.storm.synchcall.command.directory.MvCommand;
import it.grid.storm.synchcall.command.directory.RmCommand;
import it.grid.storm.synchcall.command.directory.RmdirCommand;
import it.grid.storm.synchcall.command.discovery.PingCommand;
import it.grid.storm.synchcall.command.space.GetSpaceMetaDataCommand;
import it.grid.storm.synchcall.command.space.GetSpaceTokensCommand;
import it.grid.storm.synchcall.command.space.ReleaseSpaceCommand;
import it.grid.storm.synchcall.command.space.ReserveSpaceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project.
 *
 * @author lucamag
 * @date May 27, 2008
 */
public class CommandFactory {

  private static final Logger log = LoggerFactory.getLogger(CommandFactory.class);

  public static Command getCommand(OperationType type) throws IllegalArgumentException {

    switch (type) {
      case RM:
        return new RmCommand();
      case RMD:
        return new RmdirCommand();
      case MKD:
        return new MkdirCommand();
      case MV:
        return new MvCommand();
      case LS:
        return new LsCommand();

      case PNG:
        return new PingCommand();

      case GST:
        return new GetSpaceTokensCommand();
      case GSM:
        return new GetSpaceMetaDataCommand();
      case RESSP:
        return new ReserveSpaceCommand();
      case RELSP:
        return new ReleaseSpaceCommand();

      case PD:
        return new PutDoneCommand();
      case RF:
        return new ReleaseFilesCommand();
      case EFL:
        return new ExtendFileLifeTimeCommand();
      case AF:
        return new AbortFilesCommand();
      case AR:
        return new AbortRequestCommand();

      case PTP:
        return new PrepareToPutRequestCommand();
      case SPTP:
        return new PrepareToPutRequestStatusCommand();
      case PTG:
        return new PrepareToGetRequestCommand();
      case SPTG:
        return new PrepareToGetRequestStatusCommand();

      default:
        String msg = String.format("No command found for OperationType %s", type);
        log.error(msg);
        throw new IllegalArgumentException(msg);
    }
  }
}
