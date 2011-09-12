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

package it.grid.storm.space.quota;


import it.grid.storm.info.SpaceInfoManager;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.space.ExecCommand;
import it.grid.storm.space.quota.GPFSQuotaCommand.ExitCode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPFSLsQuotaCommand extends GPFSQuotaCommand {

    private static final Logger log = LoggerFactory.getLogger(GPFSLsQuotaCommand.class);

    private static String pathSep = File.separator;
    private static String gpfsCommandPath = pathSep + "usr" + pathSep + "lpp" + pathSep + "mmfs" + pathSep + "bin";
    private static String gpfsCommand = gpfsCommandPath + pathSep + "mmlsquota";

    public String getQuotaCommandString() {
        return gpfsCommand;
    }
    

    @Override
    public GPFSQuotaInfo executeGetQuotaInfo(Quota quotaElement, boolean test) throws QuotaException {
        GPFSQuotaInfo result = new GPFSQuotaInfo();
        String command = getQuotaCommandString();
        List<String> commandList = new ArrayList<String>();
        commandList.add(command);
        //Retrieve the option to use and add to the command
        commandList.addAll(retrieveOptions(quotaElement));
        ExecCommand ec = new ExecCommand(commandList, this.timeout);
        String output = null;

        ExitCode cmdResult;
        if (test) {
            cmdResult = ExitCode.SUCCESS;
            log.debug("[TEST-MODE] Command result: " + cmdResult);
            output = MockGPFSQuotaResult.getMockOutputLs();
            log.debug("[TEST-MODE] Output: '" + output + "'");
        }
        else {
            cmdResult = ExitCode.getExitCode(ec.runCommand());
            log.debug("Command result: " + cmdResult);
            output = ec.getOutput();
            log.debug(" Output: '" + output + "'");
        }
        
        //Checking the result
        if (cmdResult.equals(ExitCode.SUCCESS)) {
            // Manage success
            result = manageSuccess(output, quotaElement);
            if (!(result.isInitializated())) {
                cmdResult = ExitCode.EMPTY_OUTPUT;
                result.setFailure(true);
            }
        } else {
             // Manage failure
            log.warn("Failed to use MMLSQUOTA! on device: '"+quotaElement.getDevice()+"' and element: '"+quotaElement.getQuotaElementName()+"'");
            result.setFailure(true);
        }
        if (result.isFailure()) {
            log.warn("Quota execution returned nothing usefull. ("+quotaElement.getDevice()+":"+quotaElement.getQuotaElementName()+")  Check QUOTA on GPFS! ");
        }
        ec.stopExecution();
        
        return result;
    }
    
    public ArrayList<GPFSQuotaInfo> executeGetQuotaInfo(boolean test) throws QuotaException {
        ArrayList<GPFSQuotaInfo> result = new ArrayList<GPFSQuotaInfo>();
        List<VirtualFSInterface> vfsS = SpaceInfoManager.getInstance().retrieveSAtoInitializeWithQuota();
        for (VirtualFSInterface vfs : vfsS) {
            try {
                CapabilityInterface cap = vfs.getCapabilities();
                if (cap!=null) {
                    Quota quotaElement = cap.getQuota();
                    GPFSQuotaInfo qInfo = executeGetQuotaInfo(quotaElement, test);
                    result.add(qInfo);
                } else {
                    log.warn("Capability of VFS: "+vfs.getAliasName()+" is null?!");
                }
              
            } catch (NamespaceException e) {
                log.error("Unable to retrieve virtual file system list. NamespaceException : " + e.getMessage());    
            }
            
        }

        return result;
    }


    private ArrayList<String> retrieveOptions(Quota quotaElement) {
        ArrayList<String> options = new ArrayList<String>();
        // Check if QuotaID is fileset and retrieve Fileset Value (param1)
        String quotaNameParam = null;
        int quotaT = quotaElement.getQuotaType().getOrdinalNumber();
        switch (quotaT) {
            case 0: //FileSet
                quotaNameParam = "-j " + quotaElement.getQuotaElementName();
                break;
            case 1: // User
                quotaNameParam = "-u " + quotaElement.getQuotaElementName();
                break;
            case 2: // Group 
                quotaNameParam = "-g " + quotaElement.getQuotaElementName();
                break;
            default:
                throw new QuotaException("Unable to execute a quota command because Quota Type '"
                        + QuotaType.string(quotaT) + "' is not supported");
        }
        options.add(quotaNameParam);
        // Retrieve Device
        String device = quotaElement.getDevice();
        options.add(device);
        return options;
    }


    private GPFSQuotaInfo manageSuccess(String output, Quota quotaElement) {
        GPFSQuotaInfo qInfo = new GPFSQuotaInfo();
        if (output != null) {
            // Expected multi-line output
            String eol = System.getProperty("line.separator");
            String[] outputArray = output.split(eol);
            log.debug(" Output lines: "+outputArray.length);
            for (int i = 0; i < outputArray.length; i++) {
                log.trace("outputArray[" + i + "]=" + outputArray[i]);
            }
            
            //Manage the case of single line output
            if (outputArray.length<=1) {
                //Looking for the word "Remarks" and take in account chars after that.
                int remarkIndex = output.indexOf("Remarks");
                log.debug("Splittin 1 line into more lines.. Remark-index:"+remarkIndex);
                if (remarkIndex>0) {
                    if (output.length()>7) {
                      output = output.substring(remarkIndex+7);
                    }
                }
                
                outputArray = output.split(eol);
                log.debug("new output: "+output);
            }
            
            List<String> outputList = parseOutputLines(outputArray);
                
            boolean meaningfullFound = false;
            // Parsing the lines
            for (String line : outputList) {
                if (GPFSQuotaInfo.meaningfullLineForLS(line)) {
                    log.debug("MMLSQUOTA - line: '"+line+"' is meaningfull!");
                    qInfo.buildLs(line, quotaElement);
                    meaningfullFound = true;
                } else {
                    log.trace("MMLSQUOTA - line: '"+line+"' doesn't contain any usefull info.");
                }
            } 
            
            if (!(meaningfullFound)) {
                qInfo.setFailure(true);
            }
        }
        return qInfo;
    }
    
    



        
    
    /**
     * @param outputArray
     *                          Block Limits                                    |     File Limits
     * Filesystem type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace  Remarks
     * gemss_test FILESET         512 2147483648 2147483648          0     none |     3128       0        0        0     none 
     * @return
     */
    private List<String> parseOutputLines(String[] outputArray) {
        List<String> result = new ArrayList<String>();
        if (outputArray != null) {
            for (int i = 0; i < outputArray.length; i++) {
                String line = outputArray[i];
                result.add(line); 
            }
        }
        return result;
    }





}
