package it.grid.storm.rest.metadata;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import jersey.repackaged.com.google.common.collect.Lists;

public class StoRIResourceServiceTest {

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_ROOTPATH = "/storage/test.vo";

	private static final String RULE_NAME = "test.vo-rule";
	private static final String RULE_STFNROOT = "/test.vo";

	private static final String STFN_PATH = "/test.vo/path/to/filename.dat";
	private static final String BADREQUEST_STFN_PATH = "/test.vo2/path/to/filename.dat";
	private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";

	private VirtualFSInterface getVirtualFS(String name, String rootPath) throws NamespaceException {

		VirtualFSInterface vfs = Mockito.mock(VirtualFSInterface.class);
		Mockito.when(vfs.getAliasName()).thenReturn(name);
		Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
		StoRI stori = Mockito.mock(StoRI.class);
		Mockito.when(vfs.createFile(Mockito.anyString(), Mockito.any(StoRIType.class), Mockito.any(MappingRule.class))).thenReturn(stori);
		Mockito.when(stori.getAbsolutePath()).thenReturn(FILE_PATH);
		return vfs;
	}

	private MappingRule getMappingRule(String name, String stfnRoot, VirtualFSInterface vfs) {

		return new MappingRule(name, stfnRoot, vfs);
	}

	private StoRIResourceService getStoRIResourceService() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		return new StoRIResourceService(Lists.newArrayList(vfs), Lists.newArrayList(rule));
	}

	private StoRIResourceService getStoRIResourceServiceNoRules() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		return new StoRIResourceService(Lists.newArrayList(vfs), Collections.<MappingRule>emptyList());
	}

	private StoRIResourceService getStoRIResourceServiceRulesNULL() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		return new StoRIResourceService(Lists.newArrayList(vfs), null);
	}

	private StoRIResourceService getStoRIResourceServiceNoVFSs() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		return new StoRIResourceService(Collections.<VirtualFSInterface>emptyList(), Lists.newArrayList(rule));
	}

	private StoRIResourceService getStoRIResourceServiceVFSsNULL() throws NamespaceException {

		VirtualFSInterface vfs = getVirtualFS(VFS_NAME, VFS_ROOTPATH);
		MappingRule rule = getMappingRule(RULE_NAME, RULE_STFNROOT, vfs);
		return new StoRIResourceService(null, Lists.newArrayList(rule));
	}

	@Test
	public void testMappingSuccess() throws NamespaceException {

		StoRIResourceService service = getStoRIResourceService();
		StoRI stori = service.getResource(STFN_PATH);
		assertThat(stori.getAbsolutePath(), equalTo(FILE_PATH));
	}

	@Test
	public void testMappingFailBadRequest() throws NamespaceException {

		StoRIResourceService service = getStoRIResourceService();
		try {
			service.getResource(BADREQUEST_STFN_PATH);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
		}
	}

	@Test
	public void testMappingFailInternalErrorEmptyRules() throws NamespaceException {

		StoRIResourceService service = getStoRIResourceServiceNoRules();
		try {
			service.getResource(STFN_PATH);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
		}
	}

	@Test
	public void testMappingFailInternalErrorNoRules() throws NamespaceException {

		StoRIResourceService service = getStoRIResourceServiceRulesNULL();
		try {
			service.getResource(STFN_PATH);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
		}
	}

	@Test
	public void testMappingFailInternalErrorEmptyVFSs() throws NamespaceException {

		StoRIResourceService service = getStoRIResourceServiceNoVFSs();
		try {
			service.getResource(STFN_PATH);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
		}
	}

	@Test
	public void testMappingFailInternalErrorVFSsNULL() throws NamespaceException {

		StoRIResourceService service = getStoRIResourceServiceVFSsNULL();
		try {
			service.getResource(STFN_PATH);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
		}
	}

}
