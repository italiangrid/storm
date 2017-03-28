package it.grid.storm.info.remote.resources;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.rest.metadata.Metadata;
import it.grid.storm.rest.metadata.model.FileMetadata;
import it.grid.storm.rest.metadata.model.VirtualFSMetadata;
import it.grid.storm.rest.metadata.service.FileMetadataService;

public class MetadataTests {

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_ROOTPATH = "/storage/test.vo";

	private static final String STFN_PATH = "/test.vo/path/to/filename.dat";
	private static final String STFN_NOSLASH_PATH = "test.vo/path/to/filename.dat";
	private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";

	private VirtualFSInterface vfs;
	private FileMetadata expected;

	private VirtualFSInterface getVirtualFS(String name, String rootPath) throws NamespaceException {

		VirtualFSInterface vfs = Mockito.mock(VirtualFSInterface.class);
		Mockito.when(vfs.getAliasName()).thenReturn(name);
		Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
		StoRI stori = Mockito.mock(StoRI.class);
		Mockito.when(vfs.createFile(Mockito.anyString(), Mockito.any(StoRIType.class),
				Mockito.any(MappingRule.class)))
			.thenReturn(stori);
		Mockito.when(stori.getAbsolutePath()).thenReturn(FILE_PATH);
		return vfs;
	}

	private Metadata getMetadataServiceSuccess(FileMetadata output) {
		FileMetadataService service = Mockito.mock(FileMetadataService.class);
		Mockito.when(service.getMetadata(Mockito.anyString())).thenReturn(output);
		return getMetadataServlet(service);
	}

	private Metadata getMetadataServiceNotFound() {
		FileMetadataService service = Mockito.mock(FileMetadataService.class);
		Mockito.when(service.getMetadata(Mockito.anyString())).thenThrow(new WebApplicationException(NOT_FOUND));
		return getMetadataServlet(service);
	}

	private Metadata getMetadataServlet(FileMetadataService s) {
		return new Metadata(s);
	}

	@Before
	public void init() throws NamespaceException {
		vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		expected = FileMetadata.builder()
			.path(FILE_PATH)
			.filesystem(
					VirtualFSMetadata.builder().name(vfs.getAliasName()).root(vfs.getRootPath()).build())
			.build();
	}

	@Test
	public void testSuccess() throws NamespaceException {

		Metadata servlet = getMetadataServiceSuccess(expected);
		FileMetadata response = servlet.getFileMetadata(STFN_PATH);
		assertThat(response.getPath(), equalTo(expected.getPath()));
		assertThat(response.getFilesystem().getName(), equalTo(expected.getFilesystem().getName()));
		assertThat(response.getFilesystem().getRoot(), equalTo(expected.getFilesystem().getRoot()));
	}

	@Test
	public void testSuccessWithWrongToken() throws NamespaceException {

		Metadata servlet = getMetadataServiceSuccess(expected);
		FileMetadata response = servlet.getFileMetadata(STFN_PATH);
		assertThat(response.getPath(), equalTo(expected.getPath()));
		assertThat(response.getFilesystem().getName(), equalTo(expected.getFilesystem().getName()));
		assertThat(response.getFilesystem().getRoot(), equalTo(expected.getFilesystem().getRoot()));
	}

	@Test
	public void testSuccessStfnNoSlash() throws NamespaceException {

		Metadata servlet = getMetadataServiceSuccess(expected);
		FileMetadata response = servlet.getFileMetadata(STFN_NOSLASH_PATH);
		assertThat(response.getPath(), equalTo(expected.getPath()));
		assertThat(response.getFilesystem().getName(), equalTo(expected.getFilesystem().getName()));
		assertThat(response.getFilesystem().getRoot(), equalTo(expected.getFilesystem().getRoot()));
	}

	@Test
	public void testMetadataNotFound() throws NamespaceException {
		Metadata servlet = getMetadataServiceNotFound();
		try {
			servlet.getFileMetadata(STFN_PATH);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(NOT_FOUND.getStatusCode()));
		}
	}
}
