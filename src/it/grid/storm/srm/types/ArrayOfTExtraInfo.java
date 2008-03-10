/**
 * This class represents a TExtraInfoArray
 *
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date   July, 2006
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.io.*;
import java.util.*;

public class ArrayOfTExtraInfo
    implements Serializable {
  public static String PNAME_STORAGESYSTEMINFO = "storageSystemInfo";

  private ArrayList extraInfoList;

  /**
   * Constructor that requires a String. If it is null, then an
   * InvalidArrayOfTExtraInfoAttributeException is thrown.
   */
  public ArrayOfTExtraInfo(TExtraInfo[] infoArray) throws InvalidArrayOfTExtraInfoAttributeException {
    if (infoArray == null) {
      throw new InvalidArrayOfTExtraInfoAttributeException(infoArray);
    }
    //FIXME this.tokenArray = tokenArray;
  }

  public ArrayOfTExtraInfo() {
    extraInfoList = new ArrayList();
  }

  public Object[] getArray() {
    return extraInfoList.toArray();
  }

  public TExtraInfo getTSpaceToken(int i) {
    return (TExtraInfo) extraInfoList.get(i);
  }

  public void setTExtraInfo(int index, TExtraInfo info) {
    extraInfoList.set(index, info);
  }

  public void addTExtraInfo(TExtraInfo info) {
    extraInfoList.add(info);
  }

  public int size() {
    return extraInfoList.size();
  }

  /**
   * Fills this class using the values found in a structure inside a Hashtable. The Hashtable may contain
   * different structures inside, all are identifiend by a name.
   * Used for communication with the FE.
   * @param inputParam Hashtable to read.
   * @param fieldName Name that identifies the ArrayOfTExtraInfo structure in the Hashtable.
   * @return A new ArrayOfTExtraInfo instance.
   */
  public static ArrayOfTExtraInfo decode(Map inputParam, String fieldName) throws
      InvalidArrayOfTExtraInfoAttributeException {
    List list = null;
    try {
      list = Arrays.asList( (Object[]) inputParam.get(fieldName));
    }
    catch (NullPointerException e) {
      //log.warn("Empty SURL array found!");
    }

    if (list == null) {
      throw new InvalidArrayOfTExtraInfoAttributeException(null);
    }

    ArrayOfTExtraInfo extraInfoArray = new ArrayOfTExtraInfo();

    for (int i = 0; i < list.size(); i++) {
      Hashtable extraInfo;

      extraInfo = (Hashtable) list.get(i);
      try {
        extraInfoArray.addTExtraInfo(TExtraInfo.decode(extraInfo));
      }
      catch (InvalidTExtraInfoAttributeException e) {
        throw new InvalidArrayOfTExtraInfoAttributeException(null);
      }
    }
    return extraInfoArray;
  }

  public void encode(Map outputParam, String name) {
    Hashtable extraInfoStruct = new Hashtable();
    Vector vector = new Vector();

    for (int i = 0; i < extraInfoList.size(); i++) {
      ( (TExtraInfo) extraInfoList.get(i)).encode(extraInfoStruct);
      vector.add(extraInfoStruct);
    }
    outputParam.put(name, vector);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (extraInfoList!=null) {
      sb.append("[");
      for (Iterator it=extraInfoList.iterator(); it.hasNext(); ) {
         TExtraInfo element = (TExtraInfo)it.next();
         sb.append(element.toString());
      }
      sb.append("]");
    } else {
      sb.append("EMPTY LIST");
    }
    return sb.toString();
  }

}
