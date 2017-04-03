package it.grid.storm.rest.metadata;

import static it.grid.storm.ea.StormEA.EA_CHECKSUM;
import static it.grid.storm.ea.StormEA.EA_MIGRATED;
import static it.grid.storm.ea.StormEA.EA_PINNED;
import static it.grid.storm.ea.StormEA.EA_PREMIGRATE;
import static it.grid.storm.ea.StormEA.EA_TSMRECD;
import static it.grid.storm.ea.StormEA.EA_TSMRECR;
import static it.grid.storm.ea.StormEA.EA_TSMRECT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.ea.ExtendedAttributes;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.rest.metadata.model.FileMetadata;
import it.grid.storm.rest.metadata.service.FileMetadataService;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import jersey.repackaged.com.google.common.collect.Lists;

public class FileMetadataServiceTest {

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_ROOTPATH = "/storage/test.vo";

	private static final String RULE_NAME = "test.vo-rule";
	private static final String RULE_STFNROOT = "/test.vo";

	private static final String STFN_PATH = "/test.vo/path/to/filename.dat";
	private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";

	private static final boolean EXISTS = true;
	private static final boolean NOT_EXISTS = false;
	private static final boolean IS_DIRECTORY = true;
	private static final boolean IS_FILE = false;
	private static final boolean ONLINE = true;
	private static final boolean MIGRATED = false;

	private static final String CHECKSUM = "ADLER32:12345678";

	private VirtualFSInterface vfs;
	private MappingRule rule;
	private FileMetadataService service;

	private VirtualFSInterface getVirtualFS(String name, String rootPath, boolean fileExists,
			boolean fileIsDirectory, boolean fileIsOnline) throws NamespaceException, IOException {

		genericfs fsDriver = Mockito.mock(genericfs.class);
		Mockito.when(fsDriver.is_file_on_disk(FILE_PATH)).thenReturn(fileIsOnline);
		VirtualFSInterface vfs = Mockito.mock(VirtualFSInterface.class);
		Mockito.when(vfs.getAliasName()).thenReturn(name);
		Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
		Mockito.when(vfs.getFSDriverInstance()).thenReturn(fsDriver);
		StoRI stori = Mockito.mock(StoRI.class);
		Mockito.when(vfs.createFile(Mockito.anyString(), Mockito.any(StoRIType.class),
				Mockito.any(MappingRule.class)))
			.thenReturn(stori);
		Mockito.when(stori.getAbsolutePath()).thenReturn(FILE_PATH);
		Mockito.when(stori.getVirtualFileSystem()).thenReturn(vfs);
		LocalFile localFile = Mockito.mock(LocalFile.class);
		Mockito.when(localFile.exists()).thenReturn(fileExists);
		Mockito.when(localFile.getCanonicalPath()).thenReturn(FILE_PATH);
		Mockito.when(localFile.isDirectory()).thenReturn(fileIsDirectory);
		Mockito.when(stori.getLocalFile()).thenReturn(localFile);
		return vfs;
	}

	private MappingRule getMappingRule(String name, String stfnRoot, VirtualFSInterface vfs) {

		return new MappingRule(name, stfnRoot, vfs);
	}

	private void init(boolean fileExists, boolean fileIsDirectory, boolean fileIsOnline)
			throws NamespaceException, IOException {

		vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH, fileExists, fileIsDirectory, fileIsOnline);
		rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		initStormEA();
		service = new FileMetadataService(Lists.newArrayList(vfs), Lists.newArrayList(rule));
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
	public void testSuccess() throws NamespaceException, IOException, ResourceNotFoundException {

		init(EXISTS, IS_FILE, ONLINE);
		FileMetadata metadata = service.getMetadata(STFN_PATH);
		assertThat(metadata.getPath(), equalTo(FILE_PATH));
		assertThat(metadata.isDirectory(), equalTo(IS_FILE));
		assertThat(metadata.isOnline(), equalTo(ONLINE));
		assertThat(metadata.getAttributes().isPinned(), equalTo(false));
		assertThat(metadata.getAttributes().isMigrated(), equalTo(false));
		assertThat(metadata.getAttributes().isPremigrated(), equalTo(false));
		assertThat(metadata.getAttributes().getTSMRecD(), equalTo(null));
		assertThat(metadata.getAttributes().getTSMRecR(), equalTo(null));
		assertThat(metadata.getAttributes().getTSMRecT(), equalTo(null));
		assertThat(metadata.getAttributes().getChecksum(), equalTo(CHECKSUM));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}

	@Test
	public void testFileNotFound() throws NamespaceException, IOException {

		init(NOT_EXISTS, IS_FILE, ONLINE);
		try {
			service.getMetadata(STFN_PATH);
		} catch (ResourceNotFoundException e) {
			assertThat(e.getMessage(), containsString("not exists"));
		}
	}

	@Test
	public void testFileIsDirectory() throws NamespaceException, IOException, ResourceNotFoundException {

		init(EXISTS, IS_DIRECTORY, ONLINE);
		FileMetadata metadata = service.getMetadata(STFN_PATH);
		assertThat(metadata.getPath(), equalTo(FILE_PATH));
		assertThat(metadata.isDirectory(), equalTo(IS_DIRECTORY));
		assertThat(metadata.isOnline(), equalTo(ONLINE));
		assertThat(metadata.getAttributes(), equalTo(null));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}

	@Test
	public void testSuccessFileExistsButMigrated() throws NamespaceException, IOException, ResourceNotFoundException {

		init(EXISTS, IS_FILE, MIGRATED);
		FileMetadata metadata = service.getMetadata(STFN_PATH);
		assertThat(metadata.getPath(), equalTo(FILE_PATH));
		assertThat(metadata.isDirectory(), equalTo(IS_FILE));
		assertThat(metadata.isOnline(), equalTo(MIGRATED));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}
}
