package it.grid.storm.rest.metadata;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;

public class FileMetadataService extends StoRIResourceService
		implements MetadataService<FileMetadata> {

	private static final Logger log = LoggerFactory.getLogger(FileMetadataService.class);

	private final StormEA ea;

	public FileMetadataService(Collection<VirtualFSInterface> vfsList,
			Collection<MappingRule> rulesList, StormEA ea) {

		super(vfsList, rulesList);
		this.ea = ea;
	}

	@Override
	public FileMetadata getMetadata(String stfnPath) throws WebApplicationException {

		StoRI stori = getResource(stfnPath);
		LocalFile localFile = stori.getLocalFile();
		if (localFile.exists()) {
			log.debug("{} exists", localFile.getAbsolutePath());
			return buildFileMetadata(stori);
		}
		String errorMessage = String.format("%s not exists", localFile.getAbsolutePath());
		throw new WebApplicationException(errorMessage, NOT_FOUND);
	}

	private FileMetadata buildFileMetadata(StoRI stori) throws WebApplicationException {

		VirtualFSInterface vfs = stori.getVirtualFileSystem();
		String canonicalPath = null;
		try {
			canonicalPath = stori.getLocalFile().getCanonicalPath();
		} catch (IOException e) {
			throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
		}
		log.debug("VirtualFS is {}", vfs.getAliasName());
		VirtualFSMetadata vfsMeta =
				VirtualFSMetadata.builder().name(vfs.getAliasName()).root(vfs.getRootPath()).build();
		FileAttributes attributes = null;
		if (!stori.getLocalFile().isDirectory()) {
			attributes = FileAttributes.builder()
				.isPinned(ea.isPinned(canonicalPath))
				.migrated(ea.getMigrated(canonicalPath))
				.premigrated(ea.getPremigrated(canonicalPath))
				.checksum(ea.getChecksum(canonicalPath, "adler32"))
				.TSMRecD(ea.getTSMRecD(canonicalPath))
				.TSMRecR(ea.getTSMRecR(canonicalPath))
				.TSMRecT(ea.getTSMRecT(canonicalPath))
				.build();
		}
		FileMetadata fileMeta = null;
		try {
			fileMeta = FileMetadata.builder()
				.path(stori.getAbsolutePath())
				.creationDate(new Date((new File(canonicalPath)).lastModified()))
				.isDirectory(stori.getLocalFile().isDirectory())
				.online(vfs.getFSDriverInstance().is_file_on_disk(stori.getAbsolutePath()))
				.filesystem(vfsMeta)
				.attributes(attributes)
				.build();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
		}
		return fileMeta;
	}
}
