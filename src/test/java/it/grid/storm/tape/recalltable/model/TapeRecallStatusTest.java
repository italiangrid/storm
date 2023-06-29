/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.tape.recalltable.model;

import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.ABORTED;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.ERROR;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.IN_PROGRESS;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.QUEUED;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.SUCCESS;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.UNDEFINED;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.getRecallTaskStatus;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.isFinalStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TapeRecallStatusTest {

  @Test
  public void testStatusId() {

    assertEquals(SUCCESS.getStatusId(), 0);
    assertEquals(QUEUED.getStatusId(), 1);
    assertEquals(IN_PROGRESS.getStatusId(), 2);
    assertEquals(ERROR.getStatusId(), 3);
    assertEquals(ABORTED.getStatusId(), 4);
    assertEquals(UNDEFINED.getStatusId(), 5);
  }

  @Test
  public void testStatusCreationFromId() {

    assertEquals(getRecallTaskStatus(0), SUCCESS);
    assertEquals(getRecallTaskStatus(1), QUEUED);
    assertEquals(getRecallTaskStatus(2), IN_PROGRESS);
    assertEquals(getRecallTaskStatus(3), ERROR);
    assertEquals(getRecallTaskStatus(4), ABORTED);
    assertEquals(getRecallTaskStatus(5), UNDEFINED);
  }

  @Test
  public void testFinalStatuses() {

    assertTrue(SUCCESS.isFinalStatus());
    assertFalse(QUEUED.isFinalStatus());
    assertFalse(IN_PROGRESS.isFinalStatus());
    assertTrue(ERROR.isFinalStatus());
    assertTrue(ABORTED.isFinalStatus());
    assertFalse(UNDEFINED.isFinalStatus());

    assertTrue(isFinalStatus(0));
    assertFalse(isFinalStatus(1));
    assertFalse(isFinalStatus(2));
    assertTrue(isFinalStatus(3));
    assertTrue(isFinalStatus(4));
    assertFalse(isFinalStatus(5));
  }

  @Test
  public void testSuccessPrecedes() {

    assertFalse(SUCCESS.precedes(0));
    assertFalse(SUCCESS.precedes(1));
    assertFalse(SUCCESS.precedes(2));
    assertFalse(SUCCESS.precedes(3));
    assertFalse(SUCCESS.precedes(4));
    assertFalse(SUCCESS.precedes(5));
    assertFalse(SUCCESS.precedes(SUCCESS));
    assertFalse(SUCCESS.precedes(QUEUED));
    assertFalse(SUCCESS.precedes(IN_PROGRESS));
    assertFalse(SUCCESS.precedes(ERROR));
    assertFalse(SUCCESS.precedes(ABORTED));
    assertFalse(SUCCESS.precedes(UNDEFINED));
  }

  @Test
  public void testQueuedPrecedes() {

    assertTrue(QUEUED.precedes(0));
    assertTrue(QUEUED.precedes(SUCCESS));
    assertFalse(QUEUED.precedes(1));
    assertFalse(QUEUED.precedes(QUEUED));
    assertTrue(QUEUED.precedes(2));
    assertTrue(QUEUED.precedes(IN_PROGRESS));
    assertTrue(QUEUED.precedes(3));
    assertTrue(QUEUED.precedes(ERROR));
    assertTrue(QUEUED.precedes(4));
    assertTrue(QUEUED.precedes(ABORTED));
    assertFalse(QUEUED.precedes(5));
    assertFalse(QUEUED.precedes(UNDEFINED));
  }

  @Test
  public void testInProgressPrecedes() {

    assertTrue(IN_PROGRESS.precedes(0));
    assertTrue(IN_PROGRESS.precedes(SUCCESS));
    assertFalse(IN_PROGRESS.precedes(1));
    assertFalse(IN_PROGRESS.precedes(QUEUED));
    assertFalse(IN_PROGRESS.precedes(2));
    assertFalse(IN_PROGRESS.precedes(IN_PROGRESS));
    assertTrue(IN_PROGRESS.precedes(3));
    assertTrue(IN_PROGRESS.precedes(ERROR));
    assertTrue(IN_PROGRESS.precedes(4));
    assertTrue(IN_PROGRESS.precedes(ABORTED));
    assertFalse(IN_PROGRESS.precedes(5));
    assertFalse(IN_PROGRESS.precedes(UNDEFINED));
  }

  @Test
  public void testErrorPrecedes() {

    assertFalse(ERROR.precedes(0));
    assertFalse(ERROR.precedes(1));
    assertFalse(ERROR.precedes(2));
    assertFalse(ERROR.precedes(3));
    assertFalse(ERROR.precedes(4));
    assertFalse(ERROR.precedes(5));
    assertFalse(ERROR.precedes(SUCCESS));
    assertFalse(ERROR.precedes(QUEUED));
    assertFalse(ERROR.precedes(IN_PROGRESS));
    assertFalse(ERROR.precedes(ERROR));
    assertFalse(ERROR.precedes(ABORTED));
    assertFalse(ERROR.precedes(UNDEFINED));
  }

  @Test
  public void testAbortedPrecedes() {

    assertFalse(ABORTED.precedes(0));
    assertFalse(ABORTED.precedes(1));
    assertFalse(ABORTED.precedes(2));
    assertFalse(ABORTED.precedes(3));
    assertFalse(ABORTED.precedes(4));
    assertFalse(ABORTED.precedes(5));
    assertFalse(ABORTED.precedes(SUCCESS));
    assertFalse(ABORTED.precedes(QUEUED));
    assertFalse(ABORTED.precedes(IN_PROGRESS));
    assertFalse(ABORTED.precedes(ERROR));
    assertFalse(ABORTED.precedes(ABORTED));
    assertFalse(ABORTED.precedes(UNDEFINED));
  }

  @Test
  public void testUndefinedPrecedes() {

    assertFalse(UNDEFINED.precedes(0));
    assertFalse(UNDEFINED.precedes(1));
    assertFalse(UNDEFINED.precedes(2));
    assertFalse(UNDEFINED.precedes(3));
    assertFalse(UNDEFINED.precedes(4));
    assertFalse(UNDEFINED.precedes(5));
    assertFalse(UNDEFINED.precedes(SUCCESS));
    assertFalse(UNDEFINED.precedes(QUEUED));
    assertFalse(UNDEFINED.precedes(IN_PROGRESS));
    assertFalse(UNDEFINED.precedes(ERROR));
    assertFalse(UNDEFINED.precedes(ABORTED));
    assertFalse(UNDEFINED.precedes(UNDEFINED));
  }
}
