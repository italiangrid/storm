/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the PutDone Input Data associated with the SRM request PutDone
 *
 * @author Magnoni Luca
 * @author CNAF -INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;

public class AnonymousAbortRequestInputData extends AnonymousAbortGeneralInputData {

  public AnonymousAbortRequestInputData(TRequestToken reqToken) throws IllegalArgumentException {

    super(reqToken, AbortType.ABORT_REQUEST);
  }
}
