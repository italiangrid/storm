package it.grid.storm.rest.metadata;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.rest.metadata.model.StoRIMetadata;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.StoRIMetadataService;

@Path("/metadata")
public class Metadata {

	private static final Logger log = LoggerFactory.getLogger(Metadata.class);

	private StoRIMetadataService metadataService;

	public Metadata() throws NamespaceException {

		NamespaceInterface namespace = NamespaceDirector.getNamespace();
		metadataService = new StoRIMetadataService(namespace.getAllDefinedVFS(), namespace.getAllDefinedMappingRules());
	}

	public Metadata(StoRIMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	@GET
	@Produces(APPLICATION_JSON)
	@Path("/{stfnPath:.*}")
	public StoRIMetadata getFileMetadata(@PathParam("stfnPath") String stfnPath) {

		log.debug("GET metadata request for: {}", stfnPath);
		if (isRootPath(stfnPath)) {
			throw new WebApplicationException("invalid stfnPath provided", BAD_REQUEST);
		}
		StoRIMetadata fileMetadata;
		try {
			fileMetadata = metadataService.getMetadata(beginWithSlash(stfnPath));
		} catch (ResourceNotFoundException e) {
			log.debug(e.getMessage());
			throw new WebApplicationException(e.getMessage(), NOT_FOUND);
		} catch (Throwable e) {
			log.error(e.getMessage());
			throw new WebApplicationException(e.getMessage(), INTERNAL_SERVER_ERROR);
		}
		log.debug("metadata retrieved for {}: {}", stfnPath, fileMetadata);
		return fileMetadata;
	}

	private boolean isRootPath(String stfnPath) {
		return stfnPath.isEmpty() || stfnPath.equals("/");
	}

	private String beginWithSlash(String stfnPath) {

		if (stfnPath.startsWith(File.separator)) {
			return stfnPath;
		}
		return File.separator + stfnPath;
	}
}
