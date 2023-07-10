/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.tape.recalltable.model;

@FunctionalInterface
public interface RequestValidator {

  public boolean validate();
}
