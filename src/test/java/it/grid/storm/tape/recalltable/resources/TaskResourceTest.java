package it.grid.storm.tape.recalltable.resources;

import static it.grid.storm.tape.recalltable.resources.TaskInsertRequest.MAX_RETRY_ATTEMPTS;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.grid.storm.griduser.VONameMatchingRule;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.SubjectRules;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;

public class TaskResourceTest {

	private static final String VFS_NAME = "test.vo";
	private static final String VFS_VONAME = "test.vo";
	private static final String VFS_ROOTPATH = "/storage/test.vo";

	private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";
	private static final String ANOTHERVFS_FILE_PATH = "/storage/dteam/path/to/filename.dat";

	private VirtualFSInterface VFS = getVirtualFS(VFS_NAME, VFS_ROOTPATH, VFS_VONAME);
	private UUID groupTaskID = UUID.randomUUID();
	private TapeRecallCatalog RECALL_CATALOG = getTapeRecallCatalogInsertSuccess(groupTaskID);
	private TapeRecallCatalog BROKEN_RECALL_CATALOG = getTapeRecallCatalogInsertError();
	private NamespaceInterface NAMESPACE = getNamespace(VFS);
	private NamespaceInterface NOTMAPPED_NAMESPACE = getNotMappedNamespace();

	private TapeRecallCatalog getTapeRecallCatalogInsertSuccess(UUID groupTaskId) {

		TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
		try {
			Mockito.when(catalog.insertNewTask(Mockito.any(TapeRecallTO.class))).thenReturn(groupTaskId);
			Mockito.when(catalog.getGroupTasks(groupTaskId))
				.thenReturn(Lists.newArrayList(TapeRecallTO.createRandom(new Date(), VFS_VONAME)));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return catalog;
	}

	private TapeRecallCatalog getTapeRecallCatalogInsertError() {

		TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
		try {
			Mockito.when(catalog.insertNewTask(Mockito.any(TapeRecallTO.class)))
				.thenThrow(new DataAccessException("Error on db"));
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return catalog;
	}

	private VirtualFSInterface getVirtualFS(String name, String rootPath, String voName) {

		VirtualFSInterface vfs = Mockito.mock(VirtualFSInterface.class);
		ApproachableRule appRule = Mockito.mock(ApproachableRule.class);
		SubjectRules subRules = Mockito.mock(SubjectRules.class);
		VONameMatchingRule matchingRule = Mockito.mock(VONameMatchingRule.class);

		Mockito.when(vfs.getRootPath()).thenReturn(rootPath);
		Mockito.when(vfs.getAliasName()).thenReturn(name);

		try {
			Mockito.when(vfs.getApproachableRules()).thenReturn(Lists.newArrayList(appRule));
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
		Mockito.when(appRule.getSubjectRules()).thenReturn(subRules);
		Mockito.when(subRules.getVONameMatchingRule()).thenReturn(matchingRule);
		Mockito.when(matchingRule.getVOName()).thenReturn(voName);

		return vfs;
	}

	private NamespaceInterface getNotMappedNamespace() {
		NamespaceInterface namespace = Mockito.mock(NamespaceInterface.class);
		try {
			Mockito.when(namespace.getAllDefinedVFSAsDictionary()).thenReturn(null);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
		return namespace;
	}

	private NamespaceInterface getNamespace(VirtualFSInterface vfs) {
		NamespaceInterface namespace = Mockito.mock(NamespaceInterface.class);
		Map<String, VirtualFSInterface> vfsMap = new HashMap<String, VirtualFSInterface>();
		vfsMap.put(vfs.getRootPath(), vfs);
		try {
			Mockito.when(namespace.getAllDefinedVFSAsDictionary()).thenReturn(vfsMap);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
		return namespace;
	}

	private TaskResource getTaskResource(NamespaceInterface namespace, TapeRecallCatalog catalog) {
		return new TaskResource(namespace, catalog);
	}

	private void testGETTaskInfo(Response res) throws InvalidTRequestTokenAttributesException,
			DataAccessException, JsonParseException, JsonMappingException, IOException {

		TaskResource recallEndpoint = getTaskResource(NAMESPACE, RECALL_CATALOG);

		// extract response data
		URI location = URI.create(res.getHeaderString("Location"));
		String[] path = location.getPath().split("/");
		String groupTaskId = path[path.length - 1];
		String requestTokenValue = location.getQuery().split("=")[1];

		// prepare mocks for task info request
		TapeRecallTO task = TapeRecallTO.createRandom(new Date(), VFS_VONAME);
		TRequestToken requestToken = Mockito.mock(TRequestToken.class);
		Mockito.when(requestToken.getValue()).thenReturn(requestTokenValue);
		task.setRequestToken(new TRequestToken(requestTokenValue, new Date()));
		Mockito.when(RECALL_CATALOG.getGroupTasks(UUID.fromString(groupTaskId)))
			.thenReturn(Lists.newArrayList(task));

		// ask for task info
		res = recallEndpoint.getGroupTaskInfo(groupTaskId, requestTokenValue);
		assertThat(res.getStatus(), equalTo(OK.getStatusCode()));
		ObjectMapper mapper = new ObjectMapper();
		TapeRecallTO t = mapper.readValue(res.getEntity().toString(), TapeRecallTO.class);
		assertNotNull(t);
	}

	@Test
	public void testPOSTSuccess() throws DataAccessException, NamespaceException, JsonParseException,
			JsonMappingException, IOException, InvalidTRequestTokenAttributesException {

		TaskResource recallEndpoint = getTaskResource(NAMESPACE, RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder()
			.filename(FILE_PATH)
			.retryAttempts(0)
			.voName(VFS_VONAME)
			.pinLifetime(1223123)
			.userId("test")
			.build();
		Response res = recallEndpoint.postNewTask(request);
		assertNotNull(res.getHeaderString("Location"));
		assertThat(res.getStatus(), equalTo(CREATED.getStatusCode()));

		testGETTaskInfo(res);
	}

	@Test
	public void testPOSTVFSNotFound() throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint =
				new TaskResource(NOTMAPPED_NAMESPACE, RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder()
			.filename(FILE_PATH)
			.retryAttempts(0)
			.voName(VFS_VONAME)
			.pinLifetime(1223123)
			.userId("test")
			.build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
		}
	}

	@Test
	public void testPOSTWrongVFS() throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint = new TaskResource(NAMESPACE, RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder()
			.filename(ANOTHERVFS_FILE_PATH)
			.retryAttempts(0)
			.userId("test")
			.build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
		}
	}

	@Test
	public void testPOSTDbException() throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint =
				new TaskResource(NAMESPACE, BROKEN_RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder()
			.filename(FILE_PATH)
			.retryAttempts(0)
			.voName(VFS_VONAME)
			.pinLifetime(1223123)
			.userId("test")
			.build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
		}
	}

	@Test
	public void testPOSTValidationRequestNullFilePath()
			throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint =
				new TaskResource(NAMESPACE, BROKEN_RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder().userId("test").build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
			assertThat(e.getResponse().getEntity().toString(),
					equalTo("Request must contain a filename"));
		}
	}

	@Test
	public void testPOSTValidationRequestNullUserId() throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint =
				new TaskResource(NAMESPACE, BROKEN_RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder().filename(FILE_PATH).build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
			assertThat(e.getResponse().getEntity().toString(), equalTo("Request must contain a userId"));
		}
	}

	@Test
	public void testPOSTValidationRequestInvalidNegativeRetryAttempts()
			throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint =
				new TaskResource(NAMESPACE, BROKEN_RECALL_CATALOG);
		TaskInsertRequest request =
				TaskInsertRequest.builder().filename(FILE_PATH).userId("test").retryAttempts(-1).build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
			assertThat(e.getResponse().getEntity().toString(),
					equalTo("Retry attempts must be more or equal than zero."));
		}
	}

	@Test
	public void testPOSTValidationRequestInvalidTooManyRetryAttempts()
			throws DataAccessException, NamespaceException {

		TaskResource recallEndpoint =
				new TaskResource(NAMESPACE, BROKEN_RECALL_CATALOG);
		TaskInsertRequest request = TaskInsertRequest.builder()
			.filename(FILE_PATH)
			.userId("test")
			.retryAttempts(Integer.valueOf(MAX_RETRY_ATTEMPTS) + 1)
			.build();
		try {
			recallEndpoint.postNewTask(request);
			fail();
		} catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
			assertThat(e.getResponse().getEntity().toString(),
					equalTo("Retry attempts must be less or equal than " + MAX_RETRY_ATTEMPTS + "."));
		}
	}

	private TapeRecallCatalog getTapeRecallCatalogInProgressNotEmpty() {

		List<TapeRecallTO> emptyList = new ArrayList<TapeRecallTO>();
		TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
		Mockito.when(catalog.getAllInProgressTasks(Mockito.anyInt())).thenReturn(emptyList);
		return catalog;
	}

	@Test
	public void testGETTasksInProgressEmpty() throws DataAccessException, NamespaceException, JsonParseException,
			JsonMappingException, IOException, InvalidTRequestTokenAttributesException {

		TaskResource recallEndpoint = getTaskResource(NAMESPACE, getTapeRecallCatalogInProgressNotEmpty());
		Response res = recallEndpoint.getTasks(10);
		assertThat(res.getStatus(), equalTo(OK.getStatusCode()));
	}


}
