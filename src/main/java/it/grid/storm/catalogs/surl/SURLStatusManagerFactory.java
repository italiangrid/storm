/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs.surl;


public class SURLStatusManagerFactory {

  public static SURLStatusManager newSURLStatusManager(){
    
    SURLStatusManager delegate = new SURLStatusManagerImpl(); 
    return new InMemorySURLStatusManager(delegate);
  }
  
}
