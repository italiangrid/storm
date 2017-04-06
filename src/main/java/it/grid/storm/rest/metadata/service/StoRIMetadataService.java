package it.grid.storm.rest.metadata.service;

import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FOLDER;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FilesystemError;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoRIMetadata;
import it.grid.storm.rest.metadata.model.VirtualFSMetadata;
import it.grid.storm.srm.types.TDirOption;

public class StoRIMetadataService extends ResourceService {

	private static final Logger log = LoggerFactory.getLogger(StoRIMetadataService.class);

	public StoRIMetadataService(Collection<VirtualFSInterface> vfsList,
			Collection<MappingRule> rulesList) {

		super(vfsList, rulesList);
	}

	public StoRIMetadata getMetadata(String stfnPath)
			throws ResourceNotFoundException, NamespaceException, IOException {

		StoRI stori = getResource(stfnPath);
		LocalFile localFile = stori.getLocalFile();
		if (localFile.exists()) {
			log.debug("{} exists", localFile.getAbsolutePath());
			return buildFileMetadata(stori);
		}
		String errorMessage = String.format("%s not exists", localFile.getAbsolutePath());
		throw new ResourceNotFoundException(errorMessage);
	}

	private StoRIMetadata buildFileMetadata(StoRI stori)
			throws IOException, SecurityException, FilesystemError, NamespaceException {

		VirtualFSInterface vfs = stori.getVirtualFileSystem();
		String canonicalPath = stori.getLocalFile().getCanonicalPath();
		log.debug("VirtualFS is {}", vfs.getAliasName());
		VirtualFSMetadata vfsMeta =
				VirtualFSMetadata.builder().name(vfs.getAliasName()).root(vfs.getRootPath()).build();

		FileAttributes attributes = null;
		List<String> children = null;
		if (stori.getLocalFile().isDirectory()) {
			children = Lists.newArrayList();
			for (StoRI child : stori.getChildren(TDirOption.makeFirstLevel())) {
				children.add(child.getFilename());
			}
		} else {
			attributes = FileAttributes.builder()
				.pinned(StormEA.isPinned(canonicalPath))
				.migrated(StormEA.getMigrated(canonicalPath))
				.premigrated(StormEA.getPremigrated(canonicalPath))
				.checksum(StormEA.getChecksum(canonicalPath, "adler32"))
				.TSMRecD(StormEA.getTSMRecD(canonicalPath))
				.TSMRecR(StormEA.getTSMRecR(canonicalPath))
				.TSMRecT(StormEA.getTSMRecT(canonicalPath))
				.build();
		}
		return StoRIMetadata.builder()
			.absolutePath(stori.getAbsolutePath())
			.lastModified(new Date((new File(canonicalPath)).lastModified()))
			.type(stori.getLocalFile().isDirectory() ? FOLDER : FILE)
			.status(
					vfs.getFSDriverInstance().is_file_on_disk(stori.getAbsolutePath()) ? ONLINE : NEARLINE)
			.filesystem(vfsMeta)
			.attributes(attributes)
			.children(children)
			.build();
	}
}
