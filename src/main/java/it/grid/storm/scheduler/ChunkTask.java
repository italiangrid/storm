/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.scheduler;

import it.grid.storm.asynch.BoL;
import it.grid.storm.asynch.PtG;
import it.grid.storm.asynch.PtP;
import it.grid.storm.asynch.Request;
import it.grid.storm.asynch.RequestChunk;
import it.grid.storm.health.BookKeeper;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.health.LogEvent;
import it.grid.storm.health.OperationType;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy
 * </p>
 * 
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * 
 * @author Michele Dibenedetto
 * @version 1.1
 * 
 */
public class ChunkTask extends Task {

	private static final Logger log = LoggerFactory
		.getLogger(ChunkTask.class);

	private final Delegable todo;
	private final String userDN;
	private final String surl;
	private final String requestToken;

	private boolean successResult = false;

	public ChunkTask(Delegable todo) {

		super(todo.getName());
		this.todo = todo;
		if (todo instanceof Request) {
			userDN = ((Request) todo).getUserDN();
			surl = ((Request) todo).getSURL();
			if (todo instanceof PersistentRequestChunk) {
				requestToken = ((PersistentRequestChunk) todo).getRequestToken();
			} else {
				requestToken = "Empty";
			}
		} else {
			userDN = "unknonw";
			surl = "unknonw";
			requestToken = "unknonw";
		}
	}

	private boolean isAsynchTask() {

		return todo instanceof PersistentRequestChunk;
	}
	
	private boolean isChunkTask() {

		return todo instanceof RequestChunk;
	}

	public void setResult(boolean result) {

		this.successResult = result;
	}

	/**
	 * Compares this object with the specified object for order. Note that this
	 * method is used by priority queue.
	 * 
	 * @param o
	 *          the Object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * @todo Implement this java.lang.Comparable method. In this implementation
	 *       all chunk tasks are considered equals.
	 * 
	 */
	@Override
	public int compareTo(Task o) {

		return 0;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used to
	 * create a thread, starting the thread causes the object's <code>run</code>
	 * method to be called in that separately executing thread.
	 */
	@Override
	public void run() {

		runEvent();
		todo.doIt();
		endEvent();
		logExecution();
	}

	protected void endEvent() {

		super.endEvent();
		if (todo instanceof Request) {
			successResult = ((Request) todo).isResultSuccess();
		}
		if (isAsynchTask()) {
			((PersistentRequestChunk) todo).persistStatus();
		}
		if (isChunkTask()) {
			((RequestChunk) todo).updateGlobalStatus();
		}
	}

	/**
	 * Method used to book the execution of this chunk
	 */
	public void logExecution() {

		if (!isAsynchTask()) {
			log.debug("logExecution disabled for synch chuncks");
			return;
		}

		ArrayList<BookKeeper> bks = HealthDirector.getHealthMonitor()
			.getBookKeepers();
		if (bks.isEmpty()) {
			return;
		}
		LogEvent event = new LogEvent(buildOperationType(), userDN, surl,
			getStartExecutionTime(), howlongInExecution(),
			requestToken, successResult);
		log.debug("Booking Asynch event {}", event);
		for (int i = 0; i < bks.size(); i++) {
			bks.get(i).addLogEvent(event);
		}
	}

	/**
	 * @return
	 */
	private OperationType buildOperationType() {

		if (todo instanceof PtP) {
			return OperationType.PTP;
		}
		if (todo instanceof PtG) {
			return OperationType.PTG;
		}
		if (todo instanceof BoL) {
			return OperationType.BOL;
		}
		return OperationType.UNDEF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + (isAsynchTask() ? 1231 : 1237);
		result = prime * result
			+ ((requestToken == null) ? 0 : requestToken.hashCode());
		result = prime * result + (successResult ? 1231 : 1237);
		result = prime * result + ((surl == null) ? 0 : surl.hashCode());
		result = prime * result + ((todo == null) ? 0 : todo.hashCode());
		result = prime * result + ((userDN == null) ? 0 : userDN.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChunkTask other = (ChunkTask) obj;
		if (isAsynchTask() != other.isAsynchTask()) {
			return false;
		}
		if (requestToken == null) {
			if (other.requestToken != null) {
				return false;
			}
		} else if (!requestToken.equals(other.requestToken)) {
			return false;
		}
		if (successResult != other.successResult) {
			return false;
		}
		if (surl == null) {
			if (other.surl != null) {
				return false;
			}
		} else if (!surl.equals(other.surl)) {
			return false;
		}
		if (todo == null) {
			if (other.todo != null) {
				return false;
			}
		} else if (!todo.equals(other.todo)) {
			return false;
		}
		if (userDN == null) {
			if (other.userDN != null) {
				return false;
			}
		} else if (!userDN.equals(other.userDN)) {
			return false;
		}
		return true;
	}

}
