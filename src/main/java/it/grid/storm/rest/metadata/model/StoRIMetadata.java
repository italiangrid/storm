package it.grid.storm.rest.metadata.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class StoRIMetadata {

	public enum ResourceType {
		FILE, FOLDER
	};

	public enum ResourceStatus {
		ONLINE, NEARLINE
	};

	@NotNull
	private String absolutePath;
	@NotNull
	private VirtualFSMetadata filesystem;
	@NotNull
	private ResourceType type;
	@NotNull
	private ResourceStatus status;
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
	private Date lastModified;

	private List<String> children;

	private FileAttributes attributes;

	@JsonCreator
	public StoRIMetadata(@JsonProperty("absolutePath") String absolutePath,
			@JsonProperty("type") ResourceType type, @JsonProperty("status") ResourceStatus status,
			@JsonProperty("filesystem") VirtualFSMetadata filesystem,
			@JsonProperty("attributes") FileAttributes attributes,
			@JsonProperty("lastModified") Date lastModified,
			@JsonProperty("children") List<String> children) {
		this.absolutePath = absolutePath;
		this.type = type;
		this.status = status;
		this.filesystem = filesystem;
		this.attributes = attributes;
		this.lastModified = lastModified;
		this.children = children;
	}

	public StoRIMetadata(Builder builder) {
		this.absolutePath = builder.absolutePath;
		this.type = builder.type;
		this.status = builder.status;
		this.filesystem = builder.filesystem;
		this.attributes = builder.attributes;
		this.lastModified = builder.lastModified;
		this.children = builder.children;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public ResourceType getType() {
		return type;
	}

	public ResourceStatus getStatus() {
		return status;
	}

	public VirtualFSMetadata getFilesystem() {
		return filesystem;
	}

	public FileAttributes getAttributes() {
		return attributes;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public List<String> getChildren() {
		return children;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Date lastModified;
		private String absolutePath;
		private ResourceType type;
		private ResourceStatus status;
		private VirtualFSMetadata filesystem;
		private FileAttributes attributes;
		private List<String> children;

		public Builder absolutePath(String absolutePath) {
			this.absolutePath = absolutePath;
			return this;
		}

		public Builder type(ResourceType type) {
			this.type = type;
			return this;
		}

		public Builder status(ResourceStatus status) {
			this.status = status;
			return this;
		}

		public Builder filesystem(VirtualFSMetadata filesystem) {
			this.filesystem = filesystem;
			return this;
		}

		public Builder attributes(FileAttributes attributes) {
			this.attributes = attributes;
			return this;
		}

		public Builder lastModified(Date lastModified) {
			this.lastModified = lastModified;
			return this;
		}

		public Builder children(List<String> children) {
			this.children = children;
			return this;
		}

		public StoRIMetadata build() {
			return new StoRIMetadata(this);
		}
	}
}
