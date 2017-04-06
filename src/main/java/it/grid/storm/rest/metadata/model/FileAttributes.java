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
	private final Long TSMRecD;
	private final Integer TSMRecR;
	private final String TSMRecT;

	@JsonCreator
	public FileAttributes(@JsonProperty("pinned") Boolean pinned,
			@JsonProperty("migrated") Boolean migrated, @JsonProperty("premigrated") Boolean premigrated,
			@JsonProperty("checksum") String checksum, @JsonProperty("TSMRecD") Long TSMRecD,
			@JsonProperty("TSMRecR") Integer TSMRecR, @JsonProperty("TSMRecT") String TSMRecT) {

		this.pinned = pinned;
		this.migrated = migrated;
		this.premigrated = premigrated;
		this.checksum = checksum;
		this.TSMRecD = TSMRecD;
		this.TSMRecR = TSMRecR;
		this.TSMRecT = TSMRecT;
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
		return TSMRecD;
	}

	public Integer getTSMRecR() {
		return TSMRecR;
	}

	public String getTSMRecT() {
		return TSMRecT;
	}

	public FileAttributes(Builder builder) {

		this.pinned = builder.pinned;
		this.migrated = builder.migrated;
		this.premigrated = builder.premigrated;
		this.checksum = builder.checksum;
		this.TSMRecD = builder.TSMRecD;
		this.TSMRecR = builder.TSMRecR;
		this.TSMRecT = builder.TSMRecT;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Boolean pinned;
		private Boolean migrated;
		private Boolean premigrated;
		private String checksum;
		private Long TSMRecD;
		private Integer TSMRecR;
		private String TSMRecT;

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

		public Builder TSMRecD(Long TSMRecD) {
			this.TSMRecD = TSMRecD;
			return this;
		}

		public Builder TSMRecR(Integer TSMRecR) {
			this.TSMRecR = TSMRecR;
			return this;
		}

		public Builder TSMRecT(String TSMRecT) {
			this.TSMRecT = TSMRecT;
			return this;
		}

		public FileAttributes build() {
			return new FileAttributes(this);
		}
	}
}
