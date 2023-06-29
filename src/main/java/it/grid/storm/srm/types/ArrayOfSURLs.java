/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents a TExtraInfoArray
 * 
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 23rd, 2005
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class ArrayOfSURLs implements Serializable {

  private static final long serialVersionUID = -6162739978949956886L;

  public static final String ARRAY_OF_SURLS = "arrayOfSURLs";

  private List<TSURL> surls;

  /**
   * Constructor that requires a String. If it is null, then an
   * InvalidArrayOfTExtraInfoAttributeException is thrown.
   */
  public ArrayOfSURLs(TSURL[] surlArray) {

    if (surlArray == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, null arguments: surlArray=" + surlArray);
    }
    surls = Lists.newArrayList(surlArray);
  }

  public ArrayOfSURLs() {

    surls = Lists.newArrayList();
  }

  public List<TSURL> getArrayList() {

    return surls;
  }

  public TSURL getTSURL(int i) {

    return surls.get(i);
  }

  public void setTSURL(int index, TSURL surl) {

    surls.set(index, surl);
  }

  public void addTSURL(TSURL surl) {

    surls.add(surl);
  }

  public int size() {

    return surls.size();
  }

  public String toString() {

    StringBuilder buf = new StringBuilder("");

    if (surls != null) {
      for (int i = 0; i < surls.size(); i++) {
        buf.append("'" + surls.get(i) + "'");
        if (i + 1 < surls.size())
          buf.append(",");
      }

    } else {
      return buf.toString();
    }

    return buf.toString();

  }

  public List<String> asStringList() {

    List<String> stringList = Lists.newArrayList();
    if (surls != null) {
      for (TSURL surl : surls) {
        stringList.add(surl.toString());
      }
    }
    return stringList;
  }

  public static ArrayOfSURLs decode(Map inputParam, String name)
      throws InvalidArrayOfSURLsAttributeException {

    List<Object> list = null;
    ArrayOfSURLs surlArray = new ArrayOfSURLs();
    try {
      /*
       * here we can have a cast exception if the array contained in the hashmap has been created as
       * an object array!
       */
      list = Arrays.asList((Object[]) inputParam.get(name));
    } catch (NullPointerException e) {

    }

    if (list == null) {
      throw new InvalidArrayOfSURLsAttributeException(list);
    }
    for (Object surlString : list) {
      TSURL surl = null;
      try {
        surl = TSURL.makeFromStringValidate((String) surlString);
      } catch (InvalidTSURLAttributesException e) {
        throw new InvalidArrayOfSURLsAttributeException(null);
      }
      surlArray.addTSURL(surl);
    }
    return surlArray;
  }
}
