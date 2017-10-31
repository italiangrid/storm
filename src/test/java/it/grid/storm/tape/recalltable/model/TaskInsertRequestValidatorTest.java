package it.grid.storm.tape.recalltable.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import it.grid.storm.tape.recalltable.resources.TaskInsertRequest;

import org.junit.Test;

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
		assertThat(validator.validate(), equalTo(true));
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
		assertThat(validator.validate(), equalTo(false));
		assertThat(validator.getErrorMessage(), equalTo("Request must contain a STFN"));
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
		assertThat(validator.validate(), equalTo(false));
		assertThat(validator.getErrorMessage().contains("Request must contain a STFN"), equalTo(true));
		assertThat(validator.getErrorMessage().contains("Request must contain a userId"), equalTo(true));
	}

}
