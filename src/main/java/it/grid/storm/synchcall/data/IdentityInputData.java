/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data;

import it.grid.storm.griduser.GridUserInterface;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 *
 * <p>Authors:
 *
 * @author lucamag luca.magnoniATcnaf.infn.it
 * @date = Dec 9, 2008
 */
public interface IdentityInputData extends InputData {

  public String getPrincipal();

  public GridUserInterface getUser();
}
