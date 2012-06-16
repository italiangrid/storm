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

package it.grid.storm.namespace.model;

public class PoolMember {

  private int memberID;
  private int memberWeight = -1; //-1 means undefined
  private TransportProtocol memberProtocol;

  public PoolMember(int memberID) {
	this.memberID = memberID;
  }
  
  public PoolMember(int memberID, int weight) {
    this.memberID = memberID;
    this.memberWeight = weight;
  }

  public int getMemberID() {
    return this.memberID;
  }

  public int getMemberWeight() {
    return this.memberWeight;
  }

  public void setMemberProtocol(TransportProtocol protocol) {
    this.memberProtocol = protocol;
  }

  public TransportProtocol getMemberProtocol() {
    return this.memberProtocol;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(memberProtocol + " --> Weight: "+this.memberWeight );
    return sb.toString();
  }

}
