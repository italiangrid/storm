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

package it.grid.storm.synchcall.command;

import it.grid.storm.common.OperationType;
import it.grid.storm.synchcall.command.datatransfer.AbortFilesCommand;
import it.grid.storm.synchcall.command.datatransfer.AbortRequestCommand;
import it.grid.storm.synchcall.command.datatransfer.ExtendFileLifeTimeCommand;
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

/**
 * This class is part of the StoRM project.
 * 
 * @author lucamag
 * @date May 27, 2008
 * 
 */

public class CommandFactory  {

    public static Command getCommand(OperationType type) {
        switch(type) {
        case RM: return new RmCommand();
        case RMD: return new RmdirCommand();
        case MKD:  return new MkdirCommand();
        case MV: return new MvCommand();
        case LS: return new LsCommand();
        
        case PNG: return new PingCommand();
        
        case GST: return new GetSpaceTokensCommand();
        case GSM: return new GetSpaceMetaDataCommand();
        case RESSP: return new ReserveSpaceCommand();
        case RELSP: return new ReleaseSpaceCommand();
        
        case PD: return new PutDoneCommand();
        case RF: return new ReleaseFilesCommand();
        case EFL: return new ExtendFileLifeTimeCommand(); 
        case AF: return new AbortFilesCommand();
        case AR: return new AbortRequestCommand();
        
        }
        throw new AssertionError("SimpleCommandFactory: Unknown op: ");

    }


}
