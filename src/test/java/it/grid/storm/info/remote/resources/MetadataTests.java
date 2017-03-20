package it.grid.storm.info.remote.resources;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
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
import it.grid.storm.rest.auth.TokenVerifier;
import it.grid.storm.rest.metadata.FileMetadata;
import it.grid.storm.rest.metadata.FileMetadataService;
import it.grid.storm.rest.metadata.Metadata;
import it.grid.storm.rest.metadata.VirtualFSMetadata;

public class MetadataTests {

	private static final String TOKEN = "abracadabra";
	private static final String WRONG_TOKEN = "alakazam";

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_ROOTPATH = "/storage/test.vo";

	private static final String STFN_PATH = "/test.vo/path/to/filename.dat";
	private static final String STFN_NOSLASH_PATH = "test.vo/path/to/filename.dat";
	private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";

	private TokenVerifier ENABLED_TOKEN_AUTH = TokenVerifier.getTokenVerifier(TOKEN, true);
	private TokenVerifier DISABLED_TOKEN_AUTH = TokenVerifier.getTokenVerifier(TOKEN, false);

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

	private Metadata getMetadataServiceSuccess(FileMetadata output, TokenVerifier t) {
		FileMetadataService service = Mockito.mock(FileMetadataService.class);
		Mockito.when(service.getMetadata(Mockito.anyString())).thenReturn(output);
		return getMetadataServlet(service, t);
	}

	private Metadata getMetadataServlet(FileMetadataService s, TokenVerifier t) {
		return new Metadata(s, t);
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

		Metadata servlet = getMetadataServiceSuccess(expected, ENABLED_TOKEN_AUTH);
		FileMetadata response = servlet.getFileMetadata(STFN_PATH, TOKEN);
		assertThat(response.getPath(), equalTo(expected.getPath()));
		assertThat(response.getFilesystem().getName(), equalTo(expected.getFilesystem().getName()));
		assertThat(response.getFilesystem().getRoot(), equalTo(expected.getFilesystem().getRoot()));
	}

	@Test
	public void testSuccessWithWrongToken() throws NamespaceException {

		Metadata servlet = getMetadataServiceSuccess(expected, DISABLED_TOKEN_AUTH);
		FileMetadata response = servlet.getFileMetadata(STFN_PATH, WRONG_TOKEN);
		assertThat(response.getPath(), equalTo(expected.getPath()));
		assertThat(response.getFilesystem().getName(), equalTo(expected.getFilesystem().getName()));
		assertThat(response.getFilesystem().getRoot(), equalTo(expected.getFilesystem().getRoot()));
	}

	@Test
	public void testSuccessStfnNoSlash() throws NamespaceException {

		Metadata servlet = getMetadataServiceSuccess(expected, ENABLED_TOKEN_AUTH);
		FileMetadata response = servlet.getFileMetadata(STFN_NOSLASH_PATH, TOKEN);
		assertThat(response.getPath(), equalTo(expected.getPath()));
		assertThat(response.getFilesystem().getName(), equalTo(expected.getFilesystem().getName()));
		assertThat(response.getFilesystem().getRoot(), equalTo(expected.getFilesystem().getRoot()));
	}

	@Test
	public void testWrongTokenProvided() throws NamespaceException {
		Metadata servlet = getMetadataServiceSuccess(expected, ENABLED_TOKEN_AUTH);
		try {
			servlet.getFileMetadata(STFN_PATH, WRONG_TOKEN);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(UNAUTHORIZED.getStatusCode()));
		}
	}

	@Test
	public void testMetadataNotFound() throws NamespaceException {

	}
}
