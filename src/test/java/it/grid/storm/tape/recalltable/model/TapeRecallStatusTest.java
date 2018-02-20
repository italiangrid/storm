package it.grid.storm.tape.recalltable.model;

import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.ABORTED;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.ERROR;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.IN_PROGRESS;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.QUEUED;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.SUCCESS;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.UNDEFINED;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.getRecallTaskStatus;
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.isFinalStatus;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TapeRecallStatusTest {

	@Test
	public void testStatusId() {

		assertThat(SUCCESS.getStatusId(), equalTo(0));
		assertThat(QUEUED.getStatusId(), equalTo(1));
		assertThat(IN_PROGRESS.getStatusId(), equalTo(2));
		assertThat(ERROR.getStatusId(), equalTo(3));
		assertThat(ABORTED.getStatusId(), equalTo(4));
		assertThat(UNDEFINED.getStatusId(), equalTo(5));
	}

	@Test
	public void testStatusCreationFromId() {

		assertThat(getRecallTaskStatus(0), equalTo(SUCCESS));
		assertThat(getRecallTaskStatus(1), equalTo(QUEUED));
		assertThat(getRecallTaskStatus(2), equalTo(IN_PROGRESS));
		assertThat(getRecallTaskStatus(3), equalTo(ERROR));
		assertThat(getRecallTaskStatus(4), equalTo(ABORTED));
		assertThat(getRecallTaskStatus(5), equalTo(UNDEFINED));
	}

	@Test
	public void testFinalStatuses() {
	
		assertThat(SUCCESS.isFinalStatus(), equalTo(true));
		assertThat(QUEUED.isFinalStatus(), equalTo(false));
		assertThat(IN_PROGRESS.isFinalStatus(), equalTo(false));
		assertThat(ERROR.isFinalStatus(), equalTo(true));
		assertThat(ABORTED.isFinalStatus(), equalTo(true));
		assertThat(UNDEFINED.isFinalStatus(), equalTo(false));

		assertThat(isFinalStatus(0), equalTo(true));
		assertThat(isFinalStatus(1), equalTo(false));
		assertThat(isFinalStatus(2), equalTo(false));
		assertThat(isFinalStatus(3), equalTo(true));
		assertThat(isFinalStatus(4), equalTo(true));
		assertThat(isFinalStatus(5), equalTo(false));
	}

	@Test
	public void testSuccessPrecedes() {

		assertThat(SUCCESS.precedes(0), equalTo(false));
		assertThat(SUCCESS.precedes(1), equalTo(false));
		assertThat(SUCCESS.precedes(2), equalTo(false));
		assertThat(SUCCESS.precedes(3), equalTo(false));
		assertThat(SUCCESS.precedes(4), equalTo(false));
		assertThat(SUCCESS.precedes(5), equalTo(false));
		assertThat(SUCCESS.precedes(SUCCESS), equalTo(false));
		assertThat(SUCCESS.precedes(QUEUED), equalTo(false));
		assertThat(SUCCESS.precedes(IN_PROGRESS), equalTo(false));
		assertThat(SUCCESS.precedes(ERROR), equalTo(false));
		assertThat(SUCCESS.precedes(ABORTED), equalTo(false));
		assertThat(SUCCESS.precedes(UNDEFINED), equalTo(false));
	}

	@Test
	public void testQueuedPrecedes() {

		assertThat(QUEUED.precedes(0), equalTo(true));
		assertThat(QUEUED.precedes(SUCCESS), equalTo(true));
		assertThat(QUEUED.precedes(1), equalTo(false));
		assertThat(QUEUED.precedes(QUEUED), equalTo(false));
		assertThat(QUEUED.precedes(2), equalTo(true));
		assertThat(QUEUED.precedes(IN_PROGRESS), equalTo(true));
		assertThat(QUEUED.precedes(3), equalTo(true));
		assertThat(QUEUED.precedes(ERROR), equalTo(true));
		assertThat(QUEUED.precedes(4), equalTo(true));
		assertThat(QUEUED.precedes(ABORTED), equalTo(true));
		assertThat(QUEUED.precedes(5), equalTo(false));
		assertThat(QUEUED.precedes(UNDEFINED), equalTo(false));
	}

	@Test
	public void testInProgressPrecedes() {

		assertThat(IN_PROGRESS.precedes(0), equalTo(true));
		assertThat(IN_PROGRESS.precedes(SUCCESS), equalTo(true));
		assertThat(IN_PROGRESS.precedes(1), equalTo(false));
		assertThat(IN_PROGRESS.precedes(QUEUED), equalTo(false));
		assertThat(IN_PROGRESS.precedes(2), equalTo(false));
		assertThat(IN_PROGRESS.precedes(IN_PROGRESS), equalTo(false));
		assertThat(IN_PROGRESS.precedes(3), equalTo(true));
		assertThat(IN_PROGRESS.precedes(ERROR), equalTo(true));
		assertThat(IN_PROGRESS.precedes(4), equalTo(true));
		assertThat(IN_PROGRESS.precedes(ABORTED), equalTo(true));
		assertThat(IN_PROGRESS.precedes(5), equalTo(false));
		assertThat(IN_PROGRESS.precedes(UNDEFINED), equalTo(false));
	}

	@Test
	public void testErrorPrecedes() {

		assertThat(ERROR.precedes(0), equalTo(false));
		assertThat(ERROR.precedes(1), equalTo(false));
		assertThat(ERROR.precedes(2), equalTo(false));
		assertThat(ERROR.precedes(3), equalTo(false));
		assertThat(ERROR.precedes(4), equalTo(false));
		assertThat(ERROR.precedes(5), equalTo(false));
		assertThat(ERROR.precedes(SUCCESS), equalTo(false));
		assertThat(ERROR.precedes(QUEUED), equalTo(false));
		assertThat(ERROR.precedes(IN_PROGRESS), equalTo(false));
		assertThat(ERROR.precedes(ERROR), equalTo(false));
		assertThat(ERROR.precedes(ABORTED), equalTo(false));
		assertThat(ERROR.precedes(UNDEFINED), equalTo(false));
	}

	@Test
	public void testAbortedPrecedes() {

		assertThat(ABORTED.precedes(0), equalTo(false));
		assertThat(ABORTED.precedes(1), equalTo(false));
		assertThat(ABORTED.precedes(2), equalTo(false));
		assertThat(ABORTED.precedes(3), equalTo(false));
		assertThat(ABORTED.precedes(4), equalTo(false));
		assertThat(ABORTED.precedes(5), equalTo(false));
		assertThat(ABORTED.precedes(SUCCESS), equalTo(false));
		assertThat(ABORTED.precedes(QUEUED), equalTo(false));
		assertThat(ABORTED.precedes(IN_PROGRESS), equalTo(false));
		assertThat(ABORTED.precedes(ERROR), equalTo(false));
		assertThat(ABORTED.precedes(ABORTED), equalTo(false));
		assertThat(ABORTED.precedes(UNDEFINED), equalTo(false));
	}

	@Test
	public void testUndefinedPrecedes() {

		assertThat(UNDEFINED.precedes(0), equalTo(false));
		assertThat(UNDEFINED.precedes(1), equalTo(false));
		assertThat(UNDEFINED.precedes(2), equalTo(false));
		assertThat(UNDEFINED.precedes(3), equalTo(false));
		assertThat(UNDEFINED.precedes(4), equalTo(false));
		assertThat(UNDEFINED.precedes(5), equalTo(false));
		assertThat(UNDEFINED.precedes(SUCCESS), equalTo(false));
		assertThat(UNDEFINED.precedes(QUEUED), equalTo(false));
		assertThat(UNDEFINED.precedes(IN_PROGRESS), equalTo(false));
		assertThat(UNDEFINED.precedes(ERROR), equalTo(false));
		assertThat(UNDEFINED.precedes(ABORTED), equalTo(false));
		assertThat(UNDEFINED.precedes(UNDEFINED), equalTo(false));
	}
}
