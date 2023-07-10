/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.tape.recalltable.model;

import it.grid.storm.tape.recalltable.resources.TaskInsertRequest;
import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom validation class. Jersey validation works but don't show the validation message. The
 * exception mapper cannot be implemented cause of a Jersey bug:
 * https://java.net/jira/browse/JERSEY-3153 This class can be used to validate a request object.
 */
public class TaskInsertRequestValidator implements RequestValidator {

  private static final Logger log = LoggerFactory.getLogger(TaskInsertRequestValidator.class);

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private TaskInsertRequest request;
  private String errorMessage;

  public TaskInsertRequestValidator(TaskInsertRequest request) {
    this.request = request;
    this.errorMessage = "";
  }

  /**
   * Validate request object.
   *
   * @return @true if request is valid, @false otherwise and an error message can be retrieved
   *     with @getErrorMessage.
   */
  @Override
  public boolean validate() {

    log.debug("Validating {}", request);
    Set<ConstraintViolation<TaskInsertRequest>> constraintViolations = validator.validate(request);
    if (constraintViolations.isEmpty()) {
      log.debug("Request {} is valid", request);
      return true;
    }
    log.debug(
        "Request is invalid, {} violation(s) found: {}",
        constraintViolations.size(),
        constraintViolations);
    StringBuilder b = new StringBuilder();
    Iterator<ConstraintViolation<TaskInsertRequest>> itr = constraintViolations.iterator();
    while (itr.hasNext()) {
      ConstraintViolation<TaskInsertRequest> constraint = itr.next();
      b.append(constraint.getMessage());
      if (itr.hasNext()) {
        b.append(", ");
      }
    }
    errorMessage = b.toString();
    return false;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
