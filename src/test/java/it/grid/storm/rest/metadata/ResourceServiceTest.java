/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.rest.metadata;

import static it.grid.storm.namespace.model.StoRIType.FILE;
import static it.grid.storm.namespace.model.StoRIType.FOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.ResourceService;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ResourceServiceTest {

  private static final String VFS_NAME = "test.vo";
  private static final String VFS_ROOTPATH = "/tmp/test.vo";
  private static final String VFS_ROOTPATH_ENDING_SLASH = "/tmp/test.vo/";

  private static final String RULE_NAME = "test.vo-rule";
  private static final String RULE_STFNROOT = "/test.vo";
  private static final String RULE_STFNROOT_ENDING_SLASH = "/test.vo/";

  private static final String FILE_STFN_PATH = "/test.vo/dir/filename.dat";
  private static final String FILE_PATH = "/tmp/test.vo/dir/filename.dat";

  private static final String DIR_STFN_PATH = "/test.vo/dir";
  private static final String DIR_STFN_PATH_ENDING_SLASH = "/test.vo/dir/";
  private static final String DIR_PATH = "/tmp/test.vo/dir";

  private static final String NOT_FOUND_STFNPATH = "/test.vo2/dir/filename.dat";

  private VirtualFS getVirtualFS(String name, String rootPath) throws NamespaceException {

    VirtualFS vfs = Mockito.mock(VirtualFS.class);
    Mockito.when(vfs.getAliasName()).thenReturn(name);
    Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
    StoRI fileStori = Mockito.mock(StoRI.class);
    Mockito.when(fileStori.getAbsolutePath()).thenReturn(FILE_PATH);
    Mockito.when(
            vfs.createFile(Mockito.anyString(), Mockito.eq(FILE), Mockito.any(MappingRule.class)))
        .thenReturn(fileStori);
    StoRI dirStori = Mockito.mock(StoRI.class);
    Mockito.when(dirStori.getAbsolutePath()).thenReturn(DIR_PATH);
    Mockito.when(
            vfs.createFile(Mockito.anyString(), Mockito.eq(FOLDER), Mockito.any(MappingRule.class)))
        .thenReturn(dirStori);
    return vfs;
  }

  private MappingRule getMappingRule(String name, String stfnRoot, VirtualFS vfs) {

    return new MappingRule(name, stfnRoot, vfs);
  }

  private ResourceService getStoRIResourceService() throws NamespaceException {

    VirtualFS vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
    MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
    return new ResourceService(Lists.newArrayList(vfs), Lists.newArrayList(rule));
  }

  private ResourceService getStoRIResourceServiceStfnRootEndingSlash() throws NamespaceException {

    VirtualFS vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH_ENDING_SLASH);
    MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT_ENDING_SLASH, vfs);
    return new ResourceService(Lists.newArrayList(vfs), Lists.newArrayList(rule));
  }

  private ResourceService getStoRIResourceServiceNoRules() throws NamespaceException {

    VirtualFS vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
    return new ResourceService(Lists.newArrayList(vfs), Collections.<MappingRule>emptyList());
  }

  private ResourceService getStoRIResourceServiceRulesNULL() throws NamespaceException {

    VirtualFS vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
    return new ResourceService(Lists.newArrayList(vfs), null);
  }

  private ResourceService getStoRIResourceServiceVfsListNULL() throws NamespaceException {

    return new ResourceService(null, Collections.<MappingRule>emptyList());
  }

  private ResourceService getStoRIResourceServiceNoVFSs() throws NamespaceException {

    VirtualFS vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
    MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
    return new ResourceService(Collections.<VirtualFS>emptyList(), Lists.newArrayList(rule));
  }

  @Before
  public void initLocalTmpDirectory() throws IOException {
    new File(DIR_PATH).mkdirs();
    new File(FILE_PATH).createNewFile();
  }

  @After
  public void clearLocalTmpDirectory() {
    new File(VFS_ROOTPATH).delete();
  }

  @Test
  public void testMappingSuccessFile() throws NamespaceException, ResourceNotFoundException {

    ResourceService service = getStoRIResourceService();
    StoRI stori = service.getResource(FILE_STFN_PATH);
    assertEquals(stori.getAbsolutePath(), FILE_PATH);
  }

  @Test
  public void testMappingSuccessDirectory() throws NamespaceException, ResourceNotFoundException {

    ResourceService service = getStoRIResourceService();
    StoRI stori = service.getResource(DIR_STFN_PATH);
    assertEquals(stori.getAbsolutePath(), DIR_PATH);
  }

  @Test
  public void testMappingSuccessDirectoryEndingSlash()
      throws NamespaceException, ResourceNotFoundException {

    ResourceService service = getStoRIResourceServiceStfnRootEndingSlash();
    StoRI stori = service.getResource(DIR_STFN_PATH_ENDING_SLASH);
    assertEquals(stori.getAbsolutePath(), DIR_PATH);
  }

  @Test
  public void testMappingFailBadRequest() throws NamespaceException {

    ResourceService service = getStoRIResourceService();
    try {
      service.getResource(NOT_FOUND_STFNPATH);
      fail();
    } catch (ResourceNotFoundException e) {
      assertTrue(
          e.getMessage().indexOf("Unable to map " + NOT_FOUND_STFNPATH + " to a rule") != -1);
    }
  }

  @Test
  public void testMappingFailInternalErrorEmptyRules()
      throws ResourceNotFoundException, NamespaceException {

    ResourceService service = getStoRIResourceServiceNoRules();
    try {
      service.getResource(FILE_STFN_PATH);
      fail();
    } catch (ResourceNotFoundException e) {
      assertTrue(e.getMessage().indexOf("Unable to map " + FILE_STFN_PATH + " to a rule") != -1);
    }
  }

  @Test(expected = NullPointerException.class)
  public void testMappingFailInternalErrorNullRules()
      throws NamespaceException, ResourceNotFoundException {

    getStoRIResourceServiceRulesNULL();
  }

  @Test(expected = NullPointerException.class)
  public void testMappingFailInternalErrorNullVfsList()
      throws NamespaceException, ResourceNotFoundException {

    getStoRIResourceServiceVfsListNULL();
  }

  @Test
  public void testMappingFailInternalErrorEmptyVFSs()
      throws NamespaceException, ResourceNotFoundException {

    ResourceService service = getStoRIResourceServiceNoVFSs();
    try {
      service.getResource(FILE_STFN_PATH);
      fail();
    } catch (ResourceNotFoundException e) {
      assertTrue(e.getMessage().indexOf("Unable to map " + FILE_STFN_PATH + " to a rule") != -1);
    }
  }
}
