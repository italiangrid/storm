/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package org.italiangrid.storm.test;

import static org.junit.Assert.*;
import it.grid.storm.util.TokenValidator;

import org.junit.Test;


public class TestTokenValidator {

  public static final String[] validTokens = {
    "dc99d6d9-4ec5-4e29-aebd-51a8cc06a2f9",
    "15e9515d-c0d0-412d-89b2-134fe4fe1655"
  };

  public static final String[] invalidTokens = {
    "dc99d6d9-4ec5-4e29-aebd-51a806a2f9",
    "15e9515d",
    "srm://ciccio.caio.org",
    ""
  };
  
  @Test
  public void testValid() {
    for (String tok: validTokens){
      assertTrue("Valid token considered invalid: "+
        tok,TokenValidator.valid(tok));
    }
  }

  @Test
  public void testInvalid() {
    for (String tok: invalidTokens){
      assertFalse("Invalid token considered valid: "+
        tok,TokenValidator.valid(tok));
    }
  }
}
