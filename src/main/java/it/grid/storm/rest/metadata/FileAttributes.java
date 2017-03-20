package it.grid.storm.rest.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class FileAttributes {

	private final Boolean isPinned;
	private final Boolean migrated;
	private final Boolean premigrated;
	private final String checksum;
	private final Long TSMRecD;
	private final Integer TSMRecR;
	private final String TSMRecT;

	@JsonCreator
	public FileAttributes(@JsonProperty("isPinned") Boolean isPinned,
			@JsonProperty("migrated") Boolean migrated, @JsonProperty("premigrated") Boolean premigrated,
			@JsonProperty("checksum") String checksum, @JsonProperty("TSMRecD") Long TSMRecD,
			@JsonProperty("TSMRecR") Integer TSMRecR, @JsonProperty("TSMRecT") String TSMRecT) {

		this.isPinned = isPinned;
		this.migrated = migrated;
		this.premigrated = premigrated;
		this.checksum = checksum;
		this.TSMRecD = TSMRecD;
		this.TSMRecR = TSMRecR;
		this.TSMRecT = TSMRecT;
	}

	public Boolean isPinned() {
		return isPinned;
	}

	public Boolean isMigrated() {
		return migrated;
	}

	public Boolean isPremigrated() {
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

		this.isPinned = builder.isPinned;
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

		private Boolean isPinned;
		private Boolean migrated;
		private Boolean premigrated;
		private String checksum;
		private Long TSMRecD;
		private Integer TSMRecR;
		private String TSMRecT;

		public Builder isPinned(Boolean isPinned) {
			this.isPinned = isPinned;
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
