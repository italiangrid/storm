package it.grid.storm.rest.metadata;

import static it.grid.storm.rest.auth.TokenVerifier.getTokenVerifier;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.rest.auth.TokenVerifier;

@Path("/metadata")
public class Metadata {

	private static final Logger log = LoggerFactory.getLogger(Metadata.class);

	private TokenVerifier tokenVerifier;
	private FileMetadataService metadataService;

	public Metadata() throws NamespaceException {

		StormEA ea = StormEA.getDefaultStormExtendedAttributes();
		NamespaceInterface namespace = NamespaceDirector.getNamespace();
		metadataService = new FileMetadataService(namespace.getAllDefinedVFS(), namespace.getAllDefinedMappingRules(), ea);
		Configuration configuration = Configuration.getInstance();
		tokenVerifier = getTokenVerifier(configuration.getRestTokenValue(), configuration.getRestTokenEnabled());
	}

	public Metadata(FileMetadataService metadataService, TokenVerifier tokenVerifier) {
		this.metadataService = metadataService;
		this.tokenVerifier = tokenVerifier;
	}

	@GET
	@Produces(APPLICATION_JSON)
	@Path("/{stfnPath:.*}")
	public FileMetadata getFileMetadata(@PathParam("stfnPath") String stfnPath,
			@HeaderParam("token") String token) {

		tokenVerifier.verify(token);
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
