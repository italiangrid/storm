package it.grid.storm.rest.metadata;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.ResourceService;
import jersey.repackaged.com.google.common.collect.Lists;

public class ResourceServiceTest {

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_ROOTPATH = "/storage/test.vo";

	private static final String RULE_NAME = "test.vo-rule";
	private static final String RULE_STFNROOT = "/test.vo";

	private static final String STFN_PATH = "/test.vo/path/to/filename.dat";
	private static final String NOT_FOUND_STFNPATH = "/test.vo2/path/to/filename.dat";
	private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";

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

	private MappingRule getMappingRule(String name, String stfnRoot, VirtualFSInterface vfs) {

		return new MappingRule(name, stfnRoot, vfs);
	}

	private ResourceService getStoRIResourceService() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		return new ResourceService(Lists.newArrayList(vfs), Lists.newArrayList(rule));
	}

	private ResourceService getStoRIResourceServiceNoRules() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		return new ResourceService(Lists.newArrayList(vfs), Collections.<MappingRule>emptyList());
	}

	private ResourceService getStoRIResourceServiceRulesNULL() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		return new ResourceService(Lists.newArrayList(vfs), null);
	}

	private ResourceService getStoRIResourceServiceVfsListNULL() throws NamespaceException {

		return new ResourceService(null, Collections.<MappingRule>emptyList());
	}

	private ResourceService getStoRIResourceServiceNoVFSs() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		return new ResourceService(Collections.<VirtualFSInterface>emptyList(),
				Lists.newArrayList(rule));
	}

	@Test
	public void testMappingSuccess() throws NamespaceException, ResourceNotFoundException {

		ResourceService service = getStoRIResourceService();
		StoRI stori = service.getResource(STFN_PATH);
		assertThat(stori.getAbsolutePath(), equalTo(FILE_PATH));
	}

	@Test
	public void testMappingFailBadRequest() throws NamespaceException {

		ResourceService service = getStoRIResourceService();
		try {
			service.getResource(NOT_FOUND_STFNPATH);
			fail();
		} catch (ResourceNotFoundException e) {
			assertThat(e.getMessage(),
					containsString("Unable to map " + NOT_FOUND_STFNPATH + " to a rule"));
		}
	}

	@Test
	public void testMappingFailInternalErrorEmptyRules() throws ResourceNotFoundException, NamespaceException {

		ResourceService service = getStoRIResourceServiceNoRules();
		try {
			service.getResource(STFN_PATH);
			fail();
		} catch (ResourceNotFoundException e) {
			assertThat(e.getMessage(),
					containsString("Unable to map " + STFN_PATH + " to a rule"));
		}
	}

	@Test(expected=NullPointerException.class)
	public void testMappingFailInternalErrorNullRules()
			throws NamespaceException, ResourceNotFoundException {

		getStoRIResourceServiceRulesNULL();
	}

	@Test(expected=NullPointerException.class)
	public void testMappingFailInternalErrorNullVfsList()
			throws NamespaceException, ResourceNotFoundException {

		getStoRIResourceServiceVfsListNULL();
	}

	@Test
	public void testMappingFailInternalErrorEmptyVFSs()
			throws NamespaceException, ResourceNotFoundException {

		ResourceService service = getStoRIResourceServiceNoVFSs();
		try {
			service.getResource(STFN_PATH);
			fail();
		} catch (ResourceNotFoundException e) {
			assertThat(e.getMessage(),
					containsString("Unable to map " + STFN_PATH + " to a rule"));
		}
	}

}
