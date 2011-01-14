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

import it.grid.storm.srm.types.TReturnStatus;

/**
 * Class that represents the reply received from issuing an srmPutDone command.
 *
 * @author  EGRID ICTP Trieste
 * @version 1.0
 * @date    August 2006
 */
public class SRMPutDoneReply {

    private TReturnStatus overallRetStat = null; //overall request return status

    /**
     * Constructor that requires the overall TReturnStatus of the reply.
     */
    public SRMPutDoneReply(TReturnStatus overallRetStat) throws InvalidPutDoneReplyAttributeException {
        if (overallRetStat==null) throw new InvalidPutDoneReplyAttributeException();
        this.overallRetStat = overallRetStat;
    }

    /**
     * Method that returns the overll status of the request.
     */
    public TReturnStatus overallRetStat() {
        return overallRetStat;
    }

    public String toString() {
        return "SRMPutDoneReply: overall TReturnStatus is "+overallRetStat.toString();
    }
}
