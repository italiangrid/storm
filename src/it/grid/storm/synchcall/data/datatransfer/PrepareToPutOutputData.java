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

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.OutputData;

/**
 * @author Michele Dibenedetto
 *
 */
public class PrepareToPutOutputData  implements OutputData 
{

    private final TSURL surl;
    private final TTURL turl;
    private final TReturnStatus status;

    public PrepareToPutOutputData(TSURL surl, TTURL turl, TReturnStatus status)
    {
        this.surl = surl;
        this.turl = turl;
        this.status = status;
    }
    
    @Override
    public boolean isSuccess()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public TSURL getSurl()
    {
        return this.surl;
        
    }

    public TTURL getTurl()
    {
        return this.turl;
        
    }

    public TReturnStatus getStatus()
    {
        return this.status;
        
    }

}
