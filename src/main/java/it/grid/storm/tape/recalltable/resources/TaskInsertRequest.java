package it.grid.storm.tape.recalltable.resources;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class TaskInsertRequest {

	public static final String MAX_RETRY_ATTEMPTS = "4";

	@NotNull(message = "Request must contain a filename")
	private String filename;
	@NotNull(message = "Request must contain a userId")
	private String userId;

	@DecimalMin(value = "0", message = "Retry attempts must be more or equal than zero.")
	@DecimalMax(value = MAX_RETRY_ATTEMPTS,
			message = "Retry attempts must be less or equal than " + MAX_RETRY_ATTEMPTS + ".")
	private int retryAttempts;

	private String voName;
	private Integer pinLifetime;

	@JsonCreator
	public TaskInsertRequest(@JsonProperty("filename") String filename,
			@JsonProperty("userId") String userId, @JsonProperty("retryAttempts") int retryAttempts,
			@JsonProperty("voName") String voName, @JsonProperty("pinLifetime") Integer pinLifetime) {

		this.filename = filename;
		this.retryAttempts = retryAttempts;
		this.voName = voName;
		this.pinLifetime = pinLifetime;
		this.userId = userId;
	}

	public TaskInsertRequest(Builder builder) {
		this.filename = builder.filename;
		this.retryAttempts = builder.retryAttempts;
		this.voName = builder.voName;
		this.pinLifetime = builder.pinLifetime;
		this.userId = builder.userId;
	}

	public String getFilename() {
		return filename;
	}

	public String getUserId() {
		return userId;
	}

	public int getRetryAttempts() {
		return retryAttempts;
	}

	public String getVoName() {
		return voName;
	}

	public Integer getPinLifetime() {
		return this.pinLifetime;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "TaskInsertRequest [filename=" + filename + ", userId=" + userId + ", retryAttempts="
				+ retryAttempts + ", voName=" + voName + ", pinLifetime=" + pinLifetime + "]";
	}

	public static class Builder {

		private String filename;
		private String userId;
		private int retryAttempts;

		private String voName;
		private Integer pinLifetime;

		public Builder() {
			this.retryAttempts = 0;
		}

		public Builder filename(String filename) {
			this.filename = filename;
			return this;
		}

		public Builder userId(String userId) {
			this.userId = userId;
			return this;
		}

		public Builder retryAttempts(int retryAttempts) {
			this.retryAttempts = retryAttempts;
			return this;
		}

		public Builder voName(String voName) {
			this.voName = voName;
			return this;
		}

		public Builder pinLifetime(int pinLifetime) {
			this.pinLifetime = pinLifetime;
			return this;
		}

		public TaskInsertRequest build() {
			return new TaskInsertRequest(this);
		}
	}
}
