package it.grid.storm.rest.metadata;

import static it.grid.storm.ea.StormEA.EA_CHECKSUM;
import static it.grid.storm.ea.StormEA.EA_MIGRATED;
import static it.grid.storm.ea.StormEA.EA_PINNED;
import static it.grid.storm.ea.StormEA.EA_PREMIGRATE;
import static it.grid.storm.ea.StormEA.EA_TSMRECD;
import static it.grid.storm.ea.StormEA.EA_TSMRECR;
import static it.grid.storm.ea.StormEA.EA_TSMRECT;
import static it.grid.storm.namespace.model.StoRIType.FILE;
import static it.grid.storm.namespace.model.StoRIType.FOLDER;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.ea.ExtendedAttributes;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.FilesystemError;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.rest.metadata.model.StoRIMetadata;
import it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus;
import it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.StoRIMetadataService;
import it.grid.storm.srm.types.TDirOption;
import jersey.repackaged.com.google.common.collect.Lists;

public class StoRIMetadataServiceTest {

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_ROOTPATH = "/tmp/test.vo";

	private static final String RULE_NAME = "test.vo-rule";
	private static final String RULE_STFNROOT = "/test.vo";

	private static final String FILE_NAME = "filename.dat";
	private static final String FILE_STFN_PATH = "/test.vo/dir/" + FILE_NAME;
	private static final String FILE_PATH = "/tmp/test.vo/dir/" + FILE_NAME;
	private static final String DIR_STFN_PATH = "/test.vo/dir";
	private static final String DIR_PATH = "/tmp/test.vo/dir";

	private static final boolean EXISTS = true;
	private static final boolean NOT_EXISTS = false;
	private static final boolean IS_DIRECTORY = true;
	private static final boolean IS_FILE = false;
	private static final boolean ONLINE = true;
	private static final boolean MIGRATED = false;

	private static final String CHECKSUM = "ADLER32:12345678";

	private VirtualFSInterface vfs;
	private MappingRule rule;
	private StoRIMetadataService service;

	private VirtualFSInterface getVirtualFS(String name, String rootPath, boolean exists,
			boolean isDirectory, boolean isOnline) throws NamespaceException, IOException, FSException {

		VirtualFSInterface vfs = Mockito.mock(VirtualFSInterface.class);
		Mockito.when(vfs.getAliasName()).thenReturn(name);
		Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
		StoRI fileStori = Mockito.mock(StoRI.class);
		Mockito
			.when(vfs.createFile(Mockito.anyString(), Mockito.eq(FILE), Mockito.any(MappingRule.class)))
			.thenReturn(fileStori);
		Mockito.when(fileStori.getAbsolutePath()).thenReturn(FILE_PATH);
		Mockito.when(fileStori.getVirtualFileSystem()).thenReturn(vfs);
		Mockito.when(fileStori.getFilename()).thenReturn(FILE_NAME);
		StoRI dirStori = Mockito.mock(StoRI.class);
		Mockito
			.when(vfs.createFile(Mockito.anyString(), Mockito.eq(FOLDER), Mockito.any(MappingRule.class)))
			.thenReturn(dirStori);
		Mockito.when(dirStori.getAbsolutePath()).thenReturn(DIR_PATH);
		Mockito.when(dirStori.getVirtualFileSystem()).thenReturn(vfs);
		Mockito.when(dirStori.getChildren(Mockito.any(TDirOption.class))).thenReturn(Lists.newArrayList(fileStori));
		LocalFile localFile = Mockito.mock(LocalFile.class);
		Mockito.when(localFile.exists()).thenReturn(exists);
		Mockito.when(localFile.getCanonicalPath()).thenReturn(FILE_PATH);
		Mockito.when(localFile.isDirectory()).thenReturn(isDirectory);
		Mockito.when(localFile.isOnDisk()).thenReturn(isOnline);
		Mockito.when(fileStori.getLocalFile()).thenReturn(localFile);
		LocalFile localDir = Mockito.mock(LocalFile.class);
		Mockito.when(localDir.exists()).thenReturn(exists);
		Mockito.when(localDir.getCanonicalPath()).thenReturn(DIR_PATH);
		Mockito.when(localDir.isDirectory()).thenReturn(isDirectory);
		Mockito.when(dirStori.getLocalFile()).thenReturn(localFile);
		return vfs;
	}

	private MappingRule getMappingRule(String name, String stfnRoot, VirtualFSInterface vfs) {

		return new MappingRule(name, stfnRoot, vfs);
	}

	private void init(boolean fileExists, boolean fileIsDirectory, boolean fileIsOnline)
			throws NamespaceException, IOException, FSException {

		vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH, fileExists, fileIsDirectory, fileIsOnline);
		rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		initStormEA();
		service = new StoRIMetadataService(Lists.newArrayList(vfs), Lists.newArrayList(rule));
	}

	private void initStormEA() {

		ExtendedAttributes ea = Mockito.mock(ExtendedAttributes.class);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_PINNED)).thenReturn(false);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_CHECKSUM + "adler32")).thenReturn(true);
		Mockito.when(ea.getXAttr(FILE_PATH, EA_CHECKSUM + "adler32")).thenReturn(CHECKSUM);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_MIGRATED)).thenReturn(false);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_PREMIGRATE)).thenReturn(false);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_TSMRECD)).thenReturn(false);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_TSMRECR)).thenReturn(false);
		Mockito.when(ea.hasXAttr(FILE_PATH, EA_TSMRECT)).thenReturn(false);
		StormEA.init(ea);
	}

	@Test
	public void testSuccess() throws NamespaceException, IOException, ResourceNotFoundException, SecurityException, FilesystemError, FSException {

		init(EXISTS, IS_FILE, ONLINE);
		StoRIMetadata metadata = service.getMetadata(FILE_STFN_PATH);
		assertThat(metadata.getAbsolutePath(), equalTo(FILE_PATH));
		assertThat(metadata.getType(), equalTo(ResourceType.FILE));
		assertThat(metadata.getStatus(), equalTo(ResourceStatus.ONLINE));
		assertThat(metadata.getAttributes().getPinned(), equalTo(false));
		assertThat(metadata.getAttributes().getMigrated(), equalTo(false));
		assertThat(metadata.getAttributes().getPremigrated(), equalTo(false));
		assertThat(metadata.getAttributes().getTSMRecD(), equalTo(null));
		assertThat(metadata.getAttributes().getTSMRecR(), equalTo(null));
		assertThat(metadata.getAttributes().getTSMRecT(), equalTo(null));
		assertThat(metadata.getAttributes().getChecksum(), equalTo(CHECKSUM));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}

	@Test
	public void testSuccessDirectory()
			throws NamespaceException, IOException, ResourceNotFoundException, SecurityException, FilesystemError, FSException {

		init(EXISTS, IS_DIRECTORY, ONLINE);
		StoRIMetadata metadata = service.getMetadata(DIR_STFN_PATH);
		assertThat(metadata.getAbsolutePath(), equalTo(DIR_PATH));
		assertThat(metadata.getType(), equalTo(ResourceType.FOLDER));
		assertThat(metadata.getStatus(), equalTo(ResourceStatus.ONLINE));
		assertThat(metadata.getChildren().size(), equalTo(1));
		assertThat(metadata.getChildren().get(0), equalTo(FILE_NAME));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}

	@Test
	public void testFileNotFound() throws NamespaceException, IOException, SecurityException, FilesystemError, FSException {

		init(NOT_EXISTS, IS_FILE, ONLINE);
		try {
			service.getMetadata(FILE_STFN_PATH);
		} catch (ResourceNotFoundException e) {
			assertThat(e.getMessage(), containsString("not exists"));
		}
	}

	@Test
	public void testFileIsDirectory()
			throws NamespaceException, IOException, ResourceNotFoundException, SecurityException, FilesystemError, FSException {

		init(EXISTS, IS_DIRECTORY, ONLINE);
		StoRIMetadata metadata = service.getMetadata(FILE_STFN_PATH);
		assertThat(metadata.getAbsolutePath(), equalTo(FILE_PATH));
		assertThat(metadata.getType(), equalTo(ResourceType.FOLDER));
		assertThat(metadata.getStatus(), equalTo(ResourceStatus.ONLINE));
		assertThat(metadata.getAttributes(), equalTo(null));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}

	@Test
	public void testSuccessFileExistsButMigrated()
			throws NamespaceException, IOException, ResourceNotFoundException, SecurityException, FilesystemError, FSException {

		init(EXISTS, IS_FILE, MIGRATED);
		StoRIMetadata metadata = service.getMetadata(FILE_STFN_PATH);
		assertThat(metadata.getAbsolutePath(), equalTo(FILE_PATH));
		assertThat(metadata.getType(), equalTo(ResourceType.FILE));
		assertThat(metadata.getStatus(), equalTo(ResourceStatus.NEARLINE));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}
}
