package it.grid.storm.rest.metadata.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class FileMetadata {

	@NotNull
	private final String path;
	@NotNull
	private final VirtualFSMetadata filesystem;
	@NotNull
	private final boolean isDirectory;
	@NotNull
	private final boolean online;
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
	private final Date lastModified;

	private final FileAttributes attributes;

	@JsonCreator
	public FileMetadata(@JsonProperty("path") String path,
			@JsonProperty("isDirectory") boolean isDirectory, @JsonProperty("online") boolean online,
			@JsonProperty("filesystem") VirtualFSMetadata filesystem,
			@JsonProperty("attributes") FileAttributes attributes,
			@JsonProperty("lastModified") Date lastModified) {
		this.path = path;
		this.isDirectory = isDirectory;
		this.online = online;
		this.filesystem = filesystem;
		this.attributes = attributes;
		this.lastModified = lastModified;
	}

	public FileMetadata(Builder builder) {
		this.path = builder.path;
		this.isDirectory = builder.isDirectory;
		this.online = builder.online;
		this.filesystem = builder.filesystem;
		this.attributes = builder.attributes;
		this.lastModified = builder.lastModified;
	}

	public String getPath() {
		return path;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean isOnline() {
		return online;
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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Date lastModified;
		private String path;
		private boolean isDirectory;
		private boolean online;
		private VirtualFSMetadata filesystem;
		private FileAttributes attributes;

		public Builder path(String path) {
			this.path = path;
			return this;
		}

		public Builder isDirectory(boolean isDirectory) {
			this.isDirectory = isDirectory;
			return this;
		}

		public Builder online(boolean online) {
			this.online = online;
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

		public FileMetadata build() {
			return new FileMetadata(this);
		}
	}
}
