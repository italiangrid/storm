/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents a TTSpace Token
 *
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 23rd, 2005
 * @version 2.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayOfTSpaceToken implements Serializable {

  private static Logger log = LoggerFactory.getLogger(ArrayOfTSpaceToken.class);

  public static final String PNAME_ARRAYOFSPACETOKENS = "arrayOfSpaceTokens";

  ArrayList<TSpaceToken> tokenList;

  /**
   * Constructor that requires a String. If it is null, then an
   * InvalidArrayOfTTSpaceTokenAttributeException is thrown.
   */
  public ArrayOfTSpaceToken(TSpaceToken[] tokenArray)
      throws InvalidArrayOfTSpaceTokenAttributeException {

    if (tokenArray == null) throw new InvalidArrayOfTSpaceTokenAttributeException(tokenArray);
    // FIXME this.tokenArray = tokenArray;
  }

  public ArrayOfTSpaceToken() {

    tokenList = new ArrayList<TSpaceToken>();
  }

  public static ArrayOfTSpaceToken decode(Map inputParam, String fieldName)
      throws InvalidArrayOfTSpaceTokenAttributeException {

    List<Object> tokensList = null;
    try {
      tokensList = Arrays.asList((Object[]) inputParam.get(fieldName));
    } catch (NullPointerException e) {
      log.warn("");
    }
    if (tokensList == null) throw new InvalidArrayOfTSpaceTokenAttributeException(null);

    ArrayOfTSpaceToken arrayOfTSpaceTokens = new ArrayOfTSpaceToken();

    for (int i = 0; i < tokensList.size(); i++) {
      TSpaceToken token = null;
      try {
        token = TSpaceToken.make((String) tokensList.get(i));
      } catch (InvalidTSpaceTokenAttributesException e) {
        token = TSpaceToken.makeEmpty();
      }
      arrayOfTSpaceTokens.addTSpaceToken(token);
    }

    return arrayOfTSpaceTokens;
  }

  public TSpaceToken getTSpaceToken(int i) {

    return (TSpaceToken) tokenList.get(i);
  }

  public TSpaceToken[] getTSpaceTokenArray() {

    TSpaceToken[] array = new TSpaceToken[0];
    return tokenList.toArray(array);
  }

  public void addTSpaceToken(TSpaceToken token) {

    tokenList.add(token);
  }

  public int size() {

    return tokenList.size();
  }

  /**
   * Encode method, used to create a structured paramter representing this object, for FE
   * communication.
   *
   * @param outputParam
   * @param name
   */
  public void encode(Map outputParam, String name) {

    Vector vector = new Vector();
    for (int i = 0; i < tokenList.size(); i++) {
      ((TSpaceToken) tokenList.get(i)).encode(vector);
    }

    outputParam.put(name, vector);
  }
}
