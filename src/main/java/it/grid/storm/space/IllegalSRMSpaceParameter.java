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

package it.grid.storm.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TSizeInBytes;

/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class IllegalSRMSpaceParameter extends Exception {

    private boolean nullAuth = true;
    private boolean nullSpaceDes = true;
    private boolean nullRetentionPolicyInfo = true;

    public IllegalSRMSpaceParameter(GridUserInterface guser,
                                                  TSizeInBytes spaceDes,
                                                  TRetentionPolicyInfo retentionPolicyInfo)
    {
        nullAuth = (guser == null);
        nullSpaceDes = (spaceDes == null);
        nullRetentionPolicyInfo = (retentionPolicyInfo == null);
    }


    public String toString()
    {
        return "The Problem is: null-Auth= " + nullAuth
                + ", nullSpaceDesired= " + nullSpaceDes
                + ", nullRetentionPolicyInfo= " + nullRetentionPolicyInfo;
    }
}
