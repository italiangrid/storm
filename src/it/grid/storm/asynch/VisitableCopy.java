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

import java.util.List;
import org.slf4j.Logger;
import it.grid.storm.asynch.Copy.Result;
import it.grid.storm.catalogs.CopyData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;


/**
 * @author Michele Dibenedetto
 *
 */
public interface VisitableCopy
{

    public TRequestToken getLocalrt();

    public GridUserInterface getGu();

    public CopyData getRequestData();

    public Logger getLog();

    public Result buildOperationResult(String string, Copy.ResultType type) throws IllegalArgumentException;
    
    public Result buildOperationResult(List<Object> argument, Copy.ResultType type) throws IllegalArgumentException;
}
