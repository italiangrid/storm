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

package it.grid.storm.scheduler;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy </p>
 *
 * @author Zappi Riccardo  <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 * @date
 *
 */

public class SchedulerStatus {

    //represent the name of scheduler
    private String name = null;

    //nr. of threads that are actively executing tasks. (approximate)
    private int activeCount = -1;

    //Total number of tasks that have completed execution
    private long completedTaskCount = -1;

    //Core number of threads
    private int corePoolSize = -1;

    //Largest number of threads
    private int largestPoolSize = -1;

    //Maximum allowed number of threads
    private int maxPoolSize = -1;

    //Actual pool size
    private int poolSize = -1;

    //Actual number of Task scheduled
    private long taskCount = -1;

    //Queue Size
    private int queueSize = -1;

    //Queue : remaining capacity
    private int remainingCapacity = -1;


    protected SchedulerStatus(String name)
    {
        this.name = name;
    }


    public int getActiveCount()
    {
        return activeCount;
    }


    public long getCompletedTaskCount()
    {
        return completedTaskCount;
    }


    public int getCorePoolSize()
    {
        return corePoolSize;
    }


    public int getLargestPoolSize()
    {
        return largestPoolSize;
    }


    public int getMaxPoolSize()
    {
        return maxPoolSize;
    }


    public int getPoolSize()
    {
        return poolSize;
    }


    public long getTaskCount()
    {
        return taskCount;
    }


    public int getQueueSize() {
        return this.queueSize;
    }

    public int getRemainingSize() {
        return this.remainingCapacity;
    }


    protected void setActiveCount(int activeCount)
    {
        this.activeCount = activeCount;
    }


    protected void setCompletedTaskCount(long completedTaskCount)
    {
        this.completedTaskCount = completedTaskCount;
    }


    protected void setCorePoolSize(int corePoolSize)
    {
        this.corePoolSize = corePoolSize;
    }


    protected void setLargestPoolSize(int largestPoolSize)
    {
        this.largestPoolSize = largestPoolSize;
    }


    protected void setMaxPoolSize(int maxPoolSize)
    {
        this.maxPoolSize = maxPoolSize;
    }


    protected void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
    }


    protected void setTaskCount(long taskCount)
    {
        this.taskCount = taskCount;
    }

    protected void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    protected void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("WorkerPool (Sched-Name=");
        sb.append(this.name);
        sb.append(") = [core:"+corePoolSize+" ; largest:"+maxPoolSize+" ; size:"+poolSize+"]" +"\n");
        sb.append( "Queue (Sched-Name=" );
        sb.append( this.name );
        sb.append( ") = [size:" + queueSize + " ; remaining capacity:" + remainingCapacity + "]" );
        return sb.toString();
    }


}
