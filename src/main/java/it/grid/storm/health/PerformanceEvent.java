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

/**
 * 
 */
package it.grid.storm.health;


/**
 * @author zappi
 *
 */
public class PerformanceEvent {

    private OperationType opType = OperationType.UNDEF;
    private int numberOfOperation = 0;
    private long minExecutionDuration = Long.MAX_VALUE;
    private long maxExecutionDuration = Long.MIN_VALUE;
    private long meanExecutionDuration = 0;
    private long totExecutionDuration = 0;

    public PerformanceEvent(OperationType op) {
        opType = op;
    }

    public void addLogEvent(LogEvent logEvent) {
        if (logEvent.getOperationType().equals(this.opType)) {
            this.numberOfOperation++;
            long executionDuration = logEvent.getDuration();
            if (executionDuration<minExecutionDuration) {
                this.minExecutionDuration = executionDuration;
            }
            if (executionDuration>maxExecutionDuration) {
                this.maxExecutionDuration = executionDuration;
            }
            this.totExecutionDuration = totExecutionDuration + executionDuration;
            this.meanExecutionDuration = totExecutionDuration / numberOfOperation;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(opType.toString());
        sb.append(" [ ");
        sb.append("#"+this.numberOfOperation);
        sb.append(", ");
        sb.append(" min:"+this.minExecutionDuration);
        sb.append(", ");
        sb.append(" Max:"+this.maxExecutionDuration);
        sb.append(", ");
        sb.append(" Mean:"+this.meanExecutionDuration);
        sb.append(", ");
        sb.append(" TOT:"+this.totExecutionDuration);
        sb.append(" ]");
        return sb.toString();
    }
}
