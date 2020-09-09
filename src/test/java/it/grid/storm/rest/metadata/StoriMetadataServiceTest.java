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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.ea.ExtendedAttributes;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.FilesystemError;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.rest.metadata.model.StoriMetadata;
import it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus;
import it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.ResourceService;
import it.grid.storm.rest.metadata.service.StoriMetadataService;
import it.grid.storm.srm.types.TDirOption;

public class StoriMetadataServiceTest {

  private static final Logger log = LoggerFactory.getLogger(StoriMetadataServiceTest.class);

  private static final String FILE_NAME = "filename.dat";
  private static final String FILE_STFN_PATH = "/test.vo/dir/" + FILE_NAME;
  private static final String FILE_PATH = "/tmp/test.vo/dir/" + FILE_NAME;
  private static final String DIR_STFN_PATH = "/test.vo/dir";
  private static final String DIR_PATH = "/tmp/test.vo/dir";
  private static final String VFS_NAME = "TESTVO";
  private static final String VFS_ROOT_PATH = "/tmp/test.vo";
  private static final String TASK_ID = "1234";

  private static final String CHECKSUM = "ADLER32:12345678";

  private StoriMetadataService service;

  private void init(boolean dirExists, boolean fileExists, boolean isMigrated, boolean isRecalled) throws IOException, FSException, ResourceNotFoundException, NamespaceException {

    initStormEA(isMigrated, isRecalled);

    VirtualFSInterface vfs = Mockito.mock(VirtualFSInterface.class);
    Mockito.when(vfs.getAliasName()).thenReturn(VFS_NAME);
    Mockito.when(vfs.getRootPath()).thenReturn(VFS_ROOT_PATH);

    LocalFile localDir = Mockito.mock(LocalFile.class);
    Mockito.when(localDir.isDirectory()).thenReturn(true);
    Mockito.when(localDir.exists()).thenReturn(dirExists);
    Mockito.when(localDir.getCanonicalPath()).thenReturn(DIR_PATH);
    Mockito.when(localDir.isOnDisk()).thenReturn(true);
    Mockito.when(localDir.getAbsolutePath()).thenReturn(DIR_PATH);

    LocalFile localFile = Mockito.mock(LocalFile.class);
    Mockito.when(localFile.isDirectory()).thenReturn(false);
    Mockito.when(localFile.getCanonicalPath()).thenReturn(FILE_PATH);
    Mockito.when(localFile.getAbsolutePath()).thenReturn(FILE_PATH);
    Mockito.when(localFile.isOnDisk()).thenReturn(!isMigrated);
    Mockito.when(localFile.exists()).thenReturn(fileExists);

    StoRI fileStori = Mockito.mock(StoRI.class);
    Mockito.when(fileStori.getAbsolutePath()).thenReturn(FILE_PATH);
    Mockito.when(fileStori.getFilename()).thenReturn(FILE_NAME);
    Mockito.when(fileStori.getLocalFile()).thenReturn(localFile);
    Mockito.when(fileStori.getVirtualFileSystem()).thenReturn(vfs);

    StoRI dirStori = Mockito.mock(StoRI.class);
    Mockito.when(dirStori.getAbsolutePath()).thenReturn(DIR_PATH);
    Mockito.when(dirStori.getLocalFile()).thenReturn(localDir);
    Mockito.when(dirStori.getVirtualFileSystem()).thenReturn(vfs);
    Mockito.when(dirStori.getChildren(Mockito.any(TDirOption.class)))
        .thenReturn(Lists.newArrayList(fileStori));

    ResourceService resourceService = Mockito.mock(ResourceService.class);
    Mockito.when(resourceService.getResource(DIR_STFN_PATH)).thenReturn(dirStori);
    Mockito.when(resourceService.getResource(FILE_STFN_PATH)).thenReturn(fileStori);

    service = new StoriMetadataService(resourceService);
  }

  private void initAsFileOnline() throws IOException, FSException, ResourceNotFoundException, NamespaceException {
    init(true, true, false, false);
  }

  private void initAsFileOffline() throws IOException, FSException, ResourceNotFoundException, NamespaceException {
    init(true, true, true, false);
  }

  private void initAsFileNotFound() throws IOException, FSException, ResourceNotFoundException, NamespaceException {
    init(true, false, false, false);
  }

  private void initAsFileMigratedAndRecalled() throws IOException, FSException, ResourceNotFoundException, NamespaceException {
    init(true, true, true, true);
  }

  private void initStormEA(boolean hasMigrated, boolean hasTSMRecT) {

    ExtendedAttributes ea = Mockito.mock(ExtendedAttributes.class);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_PINNED)).thenReturn(false);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_CHECKSUM + "adler32")).thenReturn(true);
    Mockito.when(ea.getXAttr(FILE_PATH, EA_CHECKSUM + "adler32")).thenReturn(CHECKSUM);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_MIGRATED)).thenReturn(hasMigrated);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_PREMIGRATE)).thenReturn(false);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_TSMRECD)).thenReturn(false);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_TSMRECR)).thenReturn(false);
    Mockito.when(ea.hasXAttr(FILE_PATH, EA_TSMRECT)).thenReturn(hasTSMRecT);
    if (hasTSMRecT) {
      Mockito.when(ea.getXAttr(FILE_PATH, EA_TSMRECT)).thenReturn("1234");
    }
    StormEA.init(ea);
  }

  @Test
  public void testSuccess() throws NamespaceException, IOException, ResourceNotFoundException,
      SecurityException, FilesystemError, FSException {

    initAsFileOnline();
    StoriMetadata metadata = service.getMetadata(FILE_STFN_PATH);
    log.info("Metadata: {}", metadata);
    assertThat(metadata.getAbsolutePath(), equalTo(FILE_PATH));
    assertThat(metadata.getType(), equalTo(ResourceType.FILE));
    assertThat(metadata.getStatus(), equalTo(ResourceStatus.ONLINE));
    assertThat(metadata.getAttributes().getPinned(), equalTo(false));
    assertThat(metadata.getAttributes().getMigrated(), equalTo(false));
    assertThat(metadata.getAttributes().getPremigrated(), equalTo(false));
    assertThat(metadata.getAttributes().getTsmRecD(), equalTo(null));
    assertThat(metadata.getAttributes().getTsmRecR(), equalTo(null));
    assertThat(metadata.getAttributes().getTsmRecT(), equalTo(null));
    assertThat(metadata.getAttributes().getChecksum(), equalTo(CHECKSUM));
    assertThat(metadata.getFilesystem().getName(), equalTo(VFS_NAME));
  }

  @Test
  public void testSuccessDirectory() throws NamespaceException, IOException,
      ResourceNotFoundException, SecurityException, FilesystemError, FSException {

    initAsFileOnline();
    StoriMetadata metadata = service.getMetadata(DIR_STFN_PATH);
    log.info("Metadata: {}", metadata);
    assertThat(metadata.getAbsolutePath(), equalTo(DIR_PATH));
    assertThat(metadata.getType(), equalTo(ResourceType.FOLDER));
    assertThat(metadata.getStatus(), equalTo(ResourceStatus.ONLINE));
    assertThat(metadata.getChildren().size(), equalTo(1));
    assertThat(metadata.getChildren().get(0), equalTo(FILE_NAME));
    assertThat(metadata.getFilesystem().getName(), equalTo(VFS_NAME));
  }

  @Test
  public void testFileNotFound()
      throws NamespaceException, IOException, SecurityException, FilesystemError, FSException, ResourceNotFoundException {

    initAsFileNotFound();
    try {
      service.getMetadata(FILE_STFN_PATH);
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage(), containsString("not exists"));
    }
  }

  @Test
  public void testSuccessFileExistsButMigrated() throws NamespaceException, IOException,
      ResourceNotFoundException, SecurityException, FilesystemError, FSException {

    initAsFileOffline();
    StoriMetadata metadata = service.getMetadata(FILE_STFN_PATH);
    log.info("Metadata: {}", metadata);
    assertThat(metadata.getAbsolutePath(), equalTo(FILE_PATH));
    assertThat(metadata.getType(), equalTo(ResourceType.FILE));
    assertThat(metadata.getStatus(), equalTo(ResourceStatus.NEARLINE));
    assertThat(metadata.getFilesystem().getName(), equalTo(VFS_NAME));
  }

  @Test
  public void testSuccessFileMigratedAndRecalled() throws NamespaceException, IOException,
      ResourceNotFoundException, SecurityException, FilesystemError, FSException {

    initAsFileMigratedAndRecalled();
    StoriMetadata metadata = service.getMetadata(FILE_STFN_PATH);
    log.info("Metadata: {}", metadata);
    assertThat(metadata.getAbsolutePath(), equalTo(FILE_PATH));
    assertThat(metadata.getType(), equalTo(ResourceType.FILE));
    assertThat(metadata.getStatus(), equalTo(ResourceStatus.NEARLINE));
    assertThat(metadata.getAttributes().getTsmRecT(), equalTo(TASK_ID));
    assertThat(metadata.getFilesystem().getName(), equalTo(VFS_NAME));
  }
}
