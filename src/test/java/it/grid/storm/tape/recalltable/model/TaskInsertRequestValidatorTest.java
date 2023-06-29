/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.tape.recalltable.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.grid.storm.tape.recalltable.resources.TaskInsertRequest;

public class TaskInsertRequestValidatorTest {

  @Test
  public void testSuccess() {
    TaskInsertRequest request = TaskInsertRequest.builder()
      .voName("test.vo")
      .userId("user")
      .retryAttempts(0)
      .pinLifetime(1000)
      .stfn("/test.vo")
      .build();
    TaskInsertRequestValidator validator = new TaskInsertRequestValidator(request);
    assertTrue(validator.validate());
  }

  @Test
  public void testNullStfn() {
    TaskInsertRequest request = TaskInsertRequest.builder()
      .voName("test.vo")
      .userId("user")
      .retryAttempts(0)
      .pinLifetime(1000)
      .stfn(null)
      .build();
    TaskInsertRequestValidator validator = new TaskInsertRequestValidator(request);
    assertFalse(validator.validate());
    assertEquals(validator.getErrorMessage(), "Request must contain a STFN");
  }

  @Test
  public void testNullStfnAndUserId() {
    TaskInsertRequest request = TaskInsertRequest.builder()
      .voName("test.vo")
      .userId(null)
      .retryAttempts(0)
      .pinLifetime(1000)
      .stfn(null)
      .build();
    TaskInsertRequestValidator validator = new TaskInsertRequestValidator(request);
    assertFalse(validator.validate());
    assertTrue(validator.getErrorMessage().contains("Request must contain a STFN"));
    assertTrue(validator.getErrorMessage().contains("Request must contain a userId"));
  }

}
