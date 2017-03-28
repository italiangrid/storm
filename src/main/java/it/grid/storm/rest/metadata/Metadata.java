package it.grid.storm.rest.metadata;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.rest.metadata.model.FileMetadata;
import it.grid.storm.rest.metadata.service.FileMetadataService;

@Path("/metadata")
public class Metadata {

	private static final Logger log = LoggerFactory.getLogger(Metadata.class);

	private FileMetadataService metadataService;

	public Metadata() throws NamespaceException {

		NamespaceInterface namespace = NamespaceDirector.getNamespace();
		metadataService = new FileMetadataService(namespace.getAllDefinedVFS(), namespace.getAllDefinedMappingRules());
	}

	public Metadata(FileMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	@GET
	@Produces(APPLICATION_JSON)
	@Path("/{stfnPath:.*}")
	public FileMetadata getFileMetadata(@PathParam("stfnPath") String stfnPath) {

		log.debug("GET metadata request for: {}", stfnPath);
		FileMetadata fileMetadata = metadataService.getMetadata(beginWithSlash(stfnPath));
		log.debug("metadata retrieved for {}: {}", stfnPath, fileMetadata);
		return fileMetadata;
	}

	private String beginWithSlash(String stfnPath) {

		if (stfnPath.startsWith(File.separator)) {
			return stfnPath;
		}
		return File.separator + stfnPath;
	}
}
