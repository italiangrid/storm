/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents a TExtraInfoArray
 *
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 23rd, 2005
 * @version 2.0
 */
package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ArrayOfTSURLReturnStatus {

  public static String PNAME_ARRAYOFFILESTATUSES = "arrayOfFileStatuses";

  ArrayList<TSURLReturnStatus> surlRetList;

  /** Construct an ArrayOfTSURLReturnStatus of numItems empty elements. */
  public ArrayOfTSURLReturnStatus(int numItems) {

    surlRetList = new ArrayList<TSURLReturnStatus>(numItems);
  }

  /**
   * Constructor that requires a String. If it is null, then an
   * InvalidArrayOfTExtraInfoAttributeException is thrown.
   */
  public ArrayOfTSURLReturnStatus(TSURLReturnStatus[] surlArray)
      throws InvalidArrayOfTSURLReturnStatusAttributeException {

    if (surlArray == null) {
      throw new InvalidArrayOfTSURLReturnStatusAttributeException(surlArray);
    }
    this.surlRetList = new ArrayList<TSURLReturnStatus>(Arrays.asList(surlArray));
  }

  public ArrayOfTSURLReturnStatus() {

    surlRetList = new ArrayList<TSURLReturnStatus>();
  }

  public ArrayList<TSURLReturnStatus> getArray() {

    return surlRetList;
  }

  public TSURLReturnStatus getTSURLReturnStatus(int i) {

    return (TSURLReturnStatus) surlRetList.get(i);
  }

  public void setTSURLReturnStatus(int index, TSURLReturnStatus surl) {

    surlRetList.set(index, surl);
  }

  public void addTSurlReturnStatus(TSURLReturnStatus surl) {

    surlRetList.add(surl);
  }

  public int size() {

    return surlRetList.size();
  }

  /**
   * @param surl
   * @throws IllegalArgumentException if null argument or not contained surl
   */
  public void updateStatus(TSURLReturnStatus surlStatus, TReturnStatus newStatus)
      throws IllegalArgumentException {

    if (surlStatus == null || newStatus == null) {
      throw new IllegalArgumentException(
          "Unable to update the status,null arguments: surlStatus="
              + surlStatus
              + " newStatus="
              + newStatus);
    }
    int index = surlRetList.indexOf(surlStatus);
    if (index < 0) {
      throw new IllegalArgumentException(
          "Unable to update the status,unknown TSURLReturnStatus" + surlStatus);
    }
    surlRetList.get(index).setStatus(newStatus);
  }

  public void encode(Map outputParam, String name) {

    List list = new ArrayList();
    for (int i = 0; i < surlRetList.size(); i++) {
      ((TSURLReturnStatus) surlRetList.get(i)).encode(list);
    }

    outputParam.put(name, list);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("ArrayOfTSURLReturnStatus [surlRetList=");
    builder.append(surlRetList);
    builder.append("]");
    return builder.toString();
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
    result = prime * result + ((surlRetList == null) ? 0 : surlRetList.hashCode());
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
    ArrayOfTSURLReturnStatus other = (ArrayOfTSURLReturnStatus) obj;
    if (surlRetList == null) {
      if (other.surlRetList != null) {
        return false;
      }
    } else if (!surlRetList.equals(other.surlRetList)) {
      return false;
    }
    return true;
  }
}
