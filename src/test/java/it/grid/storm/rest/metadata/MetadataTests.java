package it.grid.storm.rest.metadata;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.FilesystemError;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.rest.metadata.model.StoriMetadata;
import it.grid.storm.rest.metadata.model.VirtualFsMetadata;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.StoriMetadataService;

public class MetadataTests {

  private static final String VFS_NAME = "test.vo";
  private static final String VFS_ROOTPATH = "/storage/test.vo";

  private static final String STFN_PATH = "/test.vo/path/to/filename.dat";
  private static final String STFN_NOSLASH_PATH = "test.vo/path/to/filename.dat";
  private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";

  private VirtualFS vfs;
  private StoriMetadata expected;
  
  private VirtualFS getVirtualFS(String name, String rootPath) throws NamespaceException {

    VirtualFS vfs = Mockito.mock(VirtualFS.class);
    Mockito.when(vfs.getAliasName()).thenReturn(name);
    Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
    StoRI stori = Mockito.mock(StoRI.class);
    Mockito.when(vfs.createFile(Mockito.anyString(), Mockito.any(StoRIType.class),
        Mockito.any(MappingRule.class)))
      .thenReturn(stori);
    Mockito.when(stori.getAbsolutePath()).thenReturn(FILE_PATH);
    return vfs;
  }

  private Metadata getMetadataServiceSuccess(StoriMetadata output) throws ResourceNotFoundException,
      NamespaceException, IOException, SecurityException, FilesystemError, FSException {
    StoriMetadataService service = Mockito.mock(StoriMetadataService.class);
    Mockito.when(service.getMetadata(Mockito.anyString())).thenReturn(output);
    return getMetadataServlet(service);
  }

  private Metadata getMetadataServiceNotFound() throws ResourceNotFoundException,
      NamespaceException, IOException, SecurityException, FilesystemError, FSException {
    StoriMetadataService service = Mockito.mock(StoriMetadataService.class);
    Mockito.when(service.getMetadata(Mockito.anyString()))
      .thenThrow(new ResourceNotFoundException(FILE_PATH + " not exists"));
    return getMetadataServlet(service);
  }

  private Metadata getMetadataServiceNamespaceException() throws ResourceNotFoundException,
      NamespaceException, IOException, SecurityException, FilesystemError, FSException {
    StoriMetadataService service = Mockito.mock(StoriMetadataService.class);
    Mockito.when(service.getMetadata(Mockito.anyString()))
      .thenThrow(new NamespaceException("Mocked namespace excpetion"));
    return getMetadataServlet(service);
  }

  private Metadata getMetadataServlet(StoriMetadataService s) {
    return new Metadata(s);
  }

  @Before
  public void init() throws NamespaceException, IOException {

    Configuration.init("src/test/resources/storm.properties");

    vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
    expected = StoriMetadata.builder()
      .absolutePath(FILE_PATH)
      .filesystem(
          VirtualFsMetadata.builder().name(vfs.getAliasName()).root(vfs.getRootPath()).build())
      .build();
  }

  @Test
  public void testSuccess() throws NamespaceException, ResourceNotFoundException, IOException,
      SecurityException, FilesystemError, FSException {

    Metadata servlet = getMetadataServiceSuccess(expected);
    StoriMetadata response = servlet.getFileMetadata(STFN_PATH);
    assertEquals(response.getAbsolutePath(), expected.getAbsolutePath());
    assertEquals(response.getFilesystem().getName(), expected.getFilesystem().getName());
    assertEquals(response.getFilesystem().getRoot(), expected.getFilesystem().getRoot());
  }

  @Test
  public void testSuccessWithWrongToken() throws NamespaceException, ResourceNotFoundException,
      IOException, SecurityException, FilesystemError, FSException {

    Metadata servlet = getMetadataServiceSuccess(expected);
    StoriMetadata response = servlet.getFileMetadata(STFN_PATH);
    assertEquals(response.getAbsolutePath(), expected.getAbsolutePath());
    assertEquals(response.getFilesystem().getName(), expected.getFilesystem().getName());
    assertEquals(response.getFilesystem().getRoot(), expected.getFilesystem().getRoot());
  }

  @Test
  public void testSuccessStfnNoSlash() throws NamespaceException, ResourceNotFoundException,
      IOException, SecurityException, FilesystemError, FSException {

    Metadata servlet = getMetadataServiceSuccess(expected);
    StoriMetadata response = servlet.getFileMetadata(STFN_NOSLASH_PATH);
    assertEquals(response.getAbsolutePath(), expected.getAbsolutePath());
    assertEquals(response.getFilesystem().getName(), expected.getFilesystem().getName());
    assertEquals(response.getFilesystem().getRoot(), expected.getFilesystem().getRoot());
  }

  @Test
  public void testMetadataNotFound() throws NamespaceException, ResourceNotFoundException,
      IOException, SecurityException, FilesystemError, FSException {
    Metadata servlet = getMetadataServiceNotFound();
    try {
      servlet.getFileMetadata(STFN_PATH);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), NOT_FOUND.getStatusCode());
    }
  }

  @Test
  public void testMetadataNamespaceException() throws NamespaceException, ResourceNotFoundException,
      IOException, SecurityException, FilesystemError, FSException {
    Metadata servlet = getMetadataServiceNamespaceException();
    try {
      servlet.getFileMetadata(STFN_PATH);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  @Test
  public void testMetadataBadRequest() throws NamespaceException, ResourceNotFoundException,
      IOException, SecurityException, FilesystemError, FSException {
    Metadata servlet = getMetadataServiceSuccess(expected);
    try {
      servlet.getFileMetadata("/");
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }
  }

  @Test
  public void testMetadataBadRequestEmptyStfn() throws NamespaceException,
      ResourceNotFoundException, IOException, SecurityException, FilesystemError, FSException {
    Metadata servlet = getMetadataServiceSuccess(expected);
    try {
      servlet.getFileMetadata("");
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }
  }
}
