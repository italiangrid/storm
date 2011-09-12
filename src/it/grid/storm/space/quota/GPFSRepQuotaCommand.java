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


import it.grid.storm.namespace.model.Quota;
import it.grid.storm.space.ExecCommand;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPFSRepQuotaCommand extends GPFSQuotaCommand {

    private static final Logger log = LoggerFactory.getLogger(GPFSRepQuotaCommand.class);

    private static String pathSep = File.separator;
    private static String gpfsCommandPath = pathSep + "usr" + pathSep + "lpp" + pathSep + "mmfs" + pathSep + "bin";
    private static String gpfsCommand = gpfsCommandPath + pathSep + "mmrepquota";

  
    @Override
    public String getQuotaCommandString() {
        return gpfsCommand;
    }
    
    
    @Override
    public GPFSQuotaInfo executeGetQuotaInfo(Quota quotaElement, boolean test) throws QuotaException {
        GPFSQuotaInfo quotaInfoResult = null;
        ArrayList<GPFSQuotaInfo> listQuotas = executeGetQuotaInfo(test);
        String devName = quotaElement.getDevice();
        String quotaName = quotaElement.getQuotaElementName();
        for (GPFSQuotaInfo gpfsQuotaInfo : listQuotas) {
            if (gpfsQuotaInfo.getDeviceName().equals(devName)) {
                if (gpfsQuotaInfo.getQuotaEntryName().equals(quotaName)) {
                    quotaInfoResult = gpfsQuotaInfo;
                    break;
                }
            }
        }
        if (quotaInfoResult==null) {
            throw new QuotaException("Unable to find quota element with name '"+quotaName+"' in device '"+devName+"'");
        }
        return quotaInfoResult;
    }

    @Override
    public ArrayList<GPFSQuotaInfo> executeGetQuotaInfo(boolean test) throws QuotaException {
        ArrayList<GPFSQuotaInfo> result = new ArrayList<GPFSQuotaInfo>();
        String command = getQuotaCommandString();
        List<String> commandList = new ArrayList<String>();
        commandList.add(command);
        commandList.add("-a");

        ExecCommand ec = new ExecCommand(commandList, this.timeout);
        String output = null;

        ExitCode cmdResult;
        if (test) {
            cmdResult = ExitCode.SUCCESS;
            log.debug("[TEST-MODE] Command result: " + cmdResult);
            output = MockGPFSQuotaResult.getMockOutput();
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
            result = manageSuccess(output);
            if (result.isEmpty()) {
                cmdResult = ExitCode.EMPTY_OUTPUT;
            }
        } else {
             // Manage failure
            log.info("Failed to use mmrepquota! Going to use 'mmlsquota'");
        }
        return result;
    }


    private ArrayList<GPFSQuotaInfo> manageSuccess(String output) {
        ArrayList<GPFSQuotaInfo> qInfoList = new ArrayList<GPFSQuotaInfo>();
        if (output != null) {
            // Expected multi-line output
            String[] outputArray = output.split("\\n");
            log.debug(" Output lines: "+outputArray.length);
            for (int i = 0; i < outputArray.length; i++) {
                log.trace("outputArray[" + i + "]=" + outputArray[i]);
            }

            List<String> outputList = parseOutputLines(outputArray);
            
            GPFSQuotaInfo quotaInfoEntry;
            
            // Parsing the lines
            for (String line : outputList) {
                if (GPFSQuotaInfo.meaningfullLine(line)) {
                    quotaInfoEntry = new GPFSQuotaInfo();
                    quotaInfoEntry.build(line);
                    qInfoList.add(quotaInfoEntry);
                }
            } 
        }
        return qInfoList;
    }
    
    

    /**
     * Return the last word in a given input string.
     * 
     * @param s the input string.
     * @return the object if present, null otherwise
     */
    private String lastWord(String line) {

        String result = null;

        if (line == null)
            return null;
        line = line.trim();
        if (line.equals(""))
            return null;

        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreTokens()) {
            result = st.nextToken();
        }

        return result;
    } 
    
    
    /**
     * @param outputArray
     * @return
     */
    private List<String> parseOutputLines(String[] outputArray) {
        List<String> result = new ArrayList<String>();
        if (outputArray != null) {
            String deviceName = "???";
            for (int i = 0; i < outputArray.length; i++) {
                String line = outputArray[i];
                // Check if it is a valid line
                if (line.startsWith("***")) {
                    // Looking for Device Name
                    deviceName = lastWord(line);
                }
                else {
                    // The line could be a useful line
                    if (!((line.startsWith(" ")) || (line.startsWith("Name")))) {
                        // Add deviceName as prefix
                        line = deviceName + " " + line;
                        result.add(line);
                    }
                }
            }
        }
        return result;
    }





}
