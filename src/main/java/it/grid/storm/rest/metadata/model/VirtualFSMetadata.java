package it.grid.storm.rest.metadata.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class VirtualFSMetadata {

	@NotNull
	private final String name;
	@NotNull
	private final String root;

	@JsonCreator
	private VirtualFSMetadata(@JsonProperty("name") String name, @JsonProperty("root") String root) {
		this.name = name;
		this.root = root;
	}

	private VirtualFSMetadata(Builder builder) {
		this.name = builder.name;
		this.root = builder.root;
	}

	public String getName() {
		return name;
	}

	public String getRoot() {
		return root;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String name;
		private String root;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder root(String root) {
			this.root = root;
			return this;
		}

		public VirtualFSMetadata build() {
			return new VirtualFSMetadata(this);
		}
	}
}
