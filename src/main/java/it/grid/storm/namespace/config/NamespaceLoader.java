/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.config;

import org.apache.commons.configuration.*;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2006
 *
 * <p>Company: INFN-CNAF
 *
 * @author Riccardoi Zappi
 * @version 1.0
 */
public interface NamespaceLoader {

  public Configuration getConfiguration();
}
