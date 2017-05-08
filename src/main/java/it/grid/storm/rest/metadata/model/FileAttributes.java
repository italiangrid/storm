package it.grid.storm.rest.metadata.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class FileAttributes {

	private final Boolean pinned;
	private final Boolean migrated;
	private final Boolean premigrated;
	private final String checksum;
	private final Long tsmRecD;
	private final Integer tsmRecR;
	private final String tsmRecT;

	@JsonCreator
	public FileAttributes(@JsonProperty("pinned") Boolean pinned,
			@JsonProperty("migrated") Boolean migrated, @JsonProperty("premigrated") Boolean premigrated,
			@JsonProperty("checksum") String checksum, @JsonProperty("tsmRecD") Long tsmRecD,
			@JsonProperty("tsmRecR") Integer tsmRecR, @JsonProperty("tsmRecT") String tsmRecT) {

		this.pinned = pinned;
		this.migrated = migrated;
		this.premigrated = premigrated;
		this.checksum = checksum;
		this.tsmRecD = tsmRecD;
		this.tsmRecR = tsmRecR;
		this.tsmRecT = tsmRecT;
	}

	public Boolean getPinned() {
		return pinned;
	}

	public Boolean getMigrated() {
		return migrated;
	}

	public Boolean getPremigrated() {
		return premigrated;
	}

	public String getChecksum() {
		return checksum;
	}

	public Long getTSMRecD() {
		return tsmRecD;
	}

	public Integer getTSMRecR() {
		return tsmRecR;
	}

	public String getTSMRecT() {
		return tsmRecT;
	}

	public FileAttributes(Builder builder) {

		this.pinned = builder.pinned;
		this.migrated = builder.migrated;
		this.premigrated = builder.premigrated;
		this.checksum = builder.checksum;
		this.tsmRecD = builder.tsmRecD;
		this.tsmRecR = builder.tsmRecR;
		this.tsmRecT = builder.tsmRecT;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Boolean pinned;
		private Boolean migrated;
		private Boolean premigrated;
		private String checksum;
		private Long tsmRecD;
		private Integer tsmRecR;
		private String tsmRecT;

		public Builder pinned(Boolean pinned) {
			this.pinned = pinned;
			return this;
		}

		public Builder migrated(Boolean migrated) {
			this.migrated = migrated;
			return this;
		}

		public Builder premigrated(Boolean premigrated) {
			this.premigrated = premigrated;
			return this;
		}

		public Builder checksum(String checksum) {
			this.checksum = checksum;
			return this;
		}

		public Builder tsmRecD(Long TSMRecD) {
			this.tsmRecD = TSMRecD;
			return this;
		}

		public Builder tsmRecR(Integer TSMRecR) {
			this.tsmRecR = TSMRecR;
			return this;
		}

		public Builder tsmRecT(String TSMRecT) {
			this.tsmRecT = TSMRecT;
			return this;
		}

		public FileAttributes build() {
			return new FileAttributes(this);
		}
	}
}
