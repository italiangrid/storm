package it.grid.storm.rest.metadata;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
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
	private StormEA ea;
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
		ea = getStormEA();
		service = new FileMetadataService(Lists.newArrayList(vfs), Lists.newArrayList(rule), ea);
	}

	private StormEA getStormEA() {

		StormEA ea = Mockito.mock(StormEA.class);
		Mockito.when(ea.isPinned(FILE_PATH)).thenReturn(false);
		Mockito.when(ea.getChecksum(FILE_PATH, "adler32")).thenReturn(CHECKSUM);
		Mockito.when(ea.getMigrated(FILE_PATH)).thenReturn(false);
		Mockito.when(ea.getPremigrated(FILE_PATH)).thenReturn(false);
		Mockito.when(ea.getTSMRecD(FILE_PATH)).thenReturn(null);
		Mockito.when(ea.getTSMRecR(FILE_PATH)).thenReturn(null);
		Mockito.when(ea.getTSMRecT(FILE_PATH)).thenReturn(null);
		return ea;
	}

	@Test
	public void testSuccess() throws NamespaceException, IOException {

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
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(NOT_FOUND.getStatusCode()));
		}
	}

	@Test
	public void testFileIsDirectory() throws NamespaceException, IOException {

		init(EXISTS, IS_DIRECTORY, ONLINE);
		FileMetadata metadata = service.getMetadata(STFN_PATH);
		assertThat(metadata.getPath(), equalTo(FILE_PATH));
		assertThat(metadata.isDirectory(), equalTo(IS_DIRECTORY));
		assertThat(metadata.isOnline(), equalTo(ONLINE));
		assertThat(metadata.getAttributes(), equalTo(null));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}

	@Test
	public void testSuccessFileExistsButMigrated() throws NamespaceException, IOException {

		init(EXISTS, IS_FILE, MIGRATED);
		FileMetadata metadata = service.getMetadata(STFN_PATH);
		assertThat(metadata.getPath(), equalTo(FILE_PATH));
		assertThat(metadata.isDirectory(), equalTo(IS_FILE));
		assertThat(metadata.isOnline(), equalTo(MIGRATED));
		assertThat(metadata.getFilesystem().getName(), equalTo(vfs.getAliasName()));
	}
}
