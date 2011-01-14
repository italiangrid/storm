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

import it.grid.storm.asynch.PtGChunk;
import it.grid.storm.asynch.PtPChunk;
import it.grid.storm.health.BookKeeper;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.health.LogEvent;
import it.grid.storm.health.OperationType;

import java.util.ArrayList;

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
 *
 */
public class ChunkTask extends Task {

    private Delegable todo = null;
    private ChunkType chunkType = null;
    private String userDN;
    private String surl;
    private String requestToken;
    private boolean successResult = false;

    /**
     *
     */
    public ChunkTask(Delegable todo, ChunkType type)
    {
        super();
        this.todo = todo;
        this.taskName = todo.getName();
        this.chunkType = type;
        if (type.equals(ChunkType.PREPARE_TO_GET)) {
            PtGChunk ptgToDo = (PtGChunk)todo;
            setLoggingValues(ptgToDo.getUserDN(), ptgToDo.getSURL(), ptgToDo.getRequestToken());
        } else if (type.equals(ChunkType.PREPARE_TO_PUT)) {
            PtPChunk ptpToDo = (PtPChunk)todo;
            setLoggingValues(ptpToDo.getUserDN(), ptpToDo.getSURL(), ptpToDo.getRequestToken());
        } else {
            setLoggingValues("unknonw", "unknown", "unknown");
        }

    }


    protected ChunkTask(Delegable todo)
    {
        super();
        this.todo = todo;
        this.taskName = todo.getName();
        this.chunkType = ChunkType.GENERIC;
    }


    public void setLoggingValues(String userDN, String surl, String requestToken) {
        this.userDN = userDN;
        this.surl = surl;
        this.requestToken = requestToken;
    }


    public void setResult(boolean result) {
        this.successResult = result;
    }

    /**
     * Compares this object with the specified object for order.
     * Note that this method is used by priority queue.
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this
     *   object is less than, equal to, or greater than the specified object.
     * @todo Implement this java.lang.Comparable method. In this implementation
     * all chunk tasks are considered equals.
     *
     */
    @Override
    public int compareTo(Object o)
    {
        return 0;
    }


    /**
     * Method used to book the execution of this chunk
     */
    public void logExecution() {
        ArrayList<BookKeeper> bks = HealthDirector.getHealthMonitor().getBookKeepers();
        OperationType opType = OperationType.makeFromChunkType(this.getType());
        long startTime = this.getStartExecutionTime();
        long duration = this.howlongInExecution();

        if (chunkType.equals(ChunkType.PREPARE_TO_GET)) {
            PtGChunk ptgToDo = (PtGChunk) todo;
            this.successResult = ptgToDo.isResultSuccess();
        } else if (chunkType.equals(ChunkType.PREPARE_TO_PUT)) {
            PtPChunk ptpToDo = (PtPChunk) todo;
            this.successResult = ptpToDo.isResultSuccess();
        } else {

        }
        LogEvent event = new LogEvent(opType, this.userDN, this.surl, startTime, duration, this.requestToken, successResult);
        if (!(bks.isEmpty())){
            for (int i = 0; i < bks.size(); i++) {
                bks.get(i).addLogEvent(event);
            }
        }


    }


    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     */
    @Override
    public void run()   {
        this.runEvent();
        todo.doIt();
        this.endEvent();
        this.logExecution();
    }


    /**
     *
     * @return ChunkType
     */
    public ChunkType getType() {
        return this.chunkType;
    }


    /**
     *
     * @param type ChunkType
     */
    protected void setType(ChunkType type) {
        this.chunkType = type;
    }

    /**
     *
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj==this) {
            return true;
        }
        if (!(obj instanceof ChunkTask)) {
            return false;
        }
        ChunkTask other = (ChunkTask) obj;
        if (!(other.chunkType.equals(this.chunkType))) {
            return false;
        }
        if (!(other.getName().equals(this.getName()))) {
            return false;
        }
        if (!(other.todo.equals(this.todo))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (this.taskName.length()!=0) {
            hash = 37*hash + taskName.hashCode();
        }
        hash = 37*hash + this.todo.hashCode();
        hash = 37*hash + this.chunkType.hashCode();
        return hash;
    }

}
