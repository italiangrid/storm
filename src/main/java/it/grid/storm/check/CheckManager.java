/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.check;


import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 * @author Michele Dibenedetto
 */
public abstract class CheckManager
{

    /**
     * An ordered list of checks to be executed
     */
    private final List<Check> checkSchedule = new ArrayList<Check>();

    /**
     * Initializes the CheckManager loading the checks and organizing them in a checkSchedule
     */
    public void init()
    {
        getLogger().debug("Initializing Check Manager");
        loadChecks();
        checkSchedule.addAll(prepareSchedule());
        getLogger().debug("Initialization completed");
    }

    /**
     * Load the check classes and eventually initialize them
     */
    protected abstract void loadChecks();

    /**
     * Provides an ordered schedule of the loaded checks
     * 
     * @return
     */
    protected abstract List<Check> prepareSchedule();

    /**
     * @return a logger
     */
    protected abstract Logger getLogger();

    /**
     * Executes the checkSchedule
     * 
     * @return true if all the checks succeeds, false otherwise
     */
    public CheckResponse lauchChecks()
    {
        getLogger().debug("Executing check schedule");
        CheckResponse result = new CheckResponse(CheckStatus.SUCCESS, "");
        for (Check check : checkSchedule)
        {
            getLogger().info("Executing check : " + check.getName());
            getLogger().info("Check description : " + check.getDescription());
            CheckResponse response;
            try
            {
                response = check.execute();
            }
            catch (GenericCheckException e)
            {
                getLogger().warn("Received a GenericCheckException during " + check.getName()
                        + " check execution : " + e.getMessage());
                response = new CheckResponse(CheckStatus.INDETERMINATE,
                                             "Received a GenericCheckException during " + check.getName()
                                                     + " check execution : " + e.getMessage());
            }
            getLogger().info("Check \'" + check.getName() + "\' response is : " + response.toString());
            if(!response.isSuccessfull() && check.isCritical())
            {
                result.setStatus(CheckStatus.and(result.getStatus(), CheckStatus.CRITICAL_FAILURE));   
            }
            else
            {
                result.setStatus(CheckStatus.and(result.getStatus(), response.getStatus()));                
            }
            getLogger().debug("Partial result is " + (result.isSuccessfull() ? "success" : "failure"));
        }
        return result;
    }
}
