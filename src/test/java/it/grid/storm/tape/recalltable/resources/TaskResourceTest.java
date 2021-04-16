package it.grid.storm.tape.recalltable.resources;

import static it.grid.storm.config.Configuration.CONFIG_FILE_PATH;
import static it.grid.storm.tape.recalltable.model.PutUpdateTaskValidator.INVALID_BODY;
import static it.grid.storm.tape.recalltable.model.PutUpdateTaskValidator.NOT_INT_VALUE;
import static it.grid.storm.tape.recalltable.resources.TaskInsertRequest.MAX_RETRY_ATTEMPTS;
import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.grid.storm.griduser.VONameMatchingRule;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.SubjectRules;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.ResourceService;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

public class TaskResourceTest {

  private static final String VFS_NAME = "test.vo";
  private static final String VFS_VONAME = "test.vo";
  private static final String VFS_ROOTPATH = "/storage/test.vo";

  private static final String REQUEST_WRONG_VONAME = "tester.vo";

  private static final String FILE_PATH = "/storage/test.vo/path/to/filename.dat";
  private static final String STFN_PATH = "/test.vo/path/to/filename.dat";

  private VirtualFSInterface VFS = getVirtualFS(VFS_NAME, VFS_ROOTPATH, VFS_VONAME);

  private StoRI STORI = getStoRI(VFS);

  private UUID groupTaskID = UUID.randomUUID();

  private TapeRecallCatalog RECALL_CATALOG = getTapeRecallCatalogInsertSuccess(groupTaskID);
  private TapeRecallCatalog BROKEN_RECALL_CATALOG = getTapeRecallCatalogInsertError();

  static {
    System.setProperty(CONFIG_FILE_PATH, "storm.properties");
  }

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

  private StoRI getStoRI(VirtualFSInterface virtualFS) {
    StoRI sto = Mockito.mock(StoRI.class);
    Mockito.when(sto.getAbsolutePath()).thenReturn(FILE_PATH);
    Mockito.when(sto.getVirtualFileSystem()).thenReturn(virtualFS);
    return sto;
  }

  private ResourceService getResourceService(StoRI storiToReturn)
      throws ResourceNotFoundException, NamespaceException {
    ResourceService service = Mockito.mock(ResourceService.class);
    Mockito.when(service.getResource(Mockito.anyString())).thenReturn(storiToReturn);
    return service;
  }

  private ResourceService getResourceNotFoundService()
      throws ResourceNotFoundException, NamespaceException {
    ResourceService service = Mockito.mock(ResourceService.class);
    Mockito.when(service.getResource(Mockito.anyString()))
      .thenThrow(new ResourceNotFoundException("Unable to map " + STFN_PATH + " to a rule"));
    return service;
  }

  private ResourceService getResourceNamespaceErrorService()
      throws ResourceNotFoundException, NamespaceException {
    ResourceService service = Mockito.mock(ResourceService.class);
    Mockito.when(service.getResource(Mockito.anyString()))
      .thenThrow(new NamespaceException("Mocked namespace exception"));
    return service;
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

  private TaskResource getTaskResource(ResourceService service, TapeRecallCatalog catalog)
      throws NamespaceException {
    return new TaskResource(service, catalog);
  }

  private void testGETTaskInfo(Response res)
      throws InvalidTRequestTokenAttributesException, DataAccessException, JsonParseException,
      JsonMappingException, IOException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI), RECALL_CATALOG);

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
    assertEquals(res.getStatus(), OK.getStatusCode());
    ObjectMapper mapper = new ObjectMapper();
    TapeRecallTO t = mapper.readValue(res.getEntity().toString(), TapeRecallTO.class);
    assertNotNull(t);
  }

  @Test
  public void testPOSTSuccess()
      throws DataAccessException, NamespaceException, JsonParseException, JsonMappingException,
      IOException, InvalidTRequestTokenAttributesException, ResourceNotFoundException {

    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI), RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder()
      .stfn(STFN_PATH)
      .retryAttempts(0)
      .voName(VFS_VONAME)
      .pinLifetime(1223123)
      .userId("test")
      .build();
    Response res = recallEndpoint.postNewTask(request);
    assertNotNull(res.getHeaderString("Location"));
    assertEquals(res.getStatus(), CREATED.getStatusCode());

    testGETTaskInfo(res);
  }

  @Test
  public void testPOSTSuccessWithNullVoName()
      throws DataAccessException, NamespaceException, JsonParseException, JsonMappingException,
      IOException, InvalidTRequestTokenAttributesException, ResourceNotFoundException {

    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI), RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder()
      .stfn(STFN_PATH)
      .retryAttempts(0)
      .voName(null)
      .pinLifetime(1223123)
      .userId("test")
      .build();
    Response res = recallEndpoint.postNewTask(request);
    assertNotNull(res.getHeaderString("Location"));
    assertEquals(res.getStatus(), CREATED.getStatusCode());

    testGETTaskInfo(res);
  }

  @Test
  public void testPOSTNamespaceErrorOnResolvingStfnPath()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        new TaskResource(getResourceNamespaceErrorService(), RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder()
      .stfn(STFN_PATH)
      .retryAttempts(0)
      .voName(VFS_VONAME)
      .pinLifetime(1223123)
      .userId("test")
      .build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertTrue(e.getCause() instanceof NamespaceException);
      assertEquals(e.getResponse().getStatus(), INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  @Test
  public void testPOSTBadVoNameRequested()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint = new TaskResource(getResourceService(STORI), RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder()
      .stfn(STFN_PATH)
      .retryAttempts(0)
      .voName(REQUEST_WRONG_VONAME)
      .pinLifetime(1223123)
      .userId("test")
      .build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }
  }

  @Test
  public void testPOSTUnableToMapStfnPath()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint = new TaskResource(getResourceNotFoundService(), RECALL_CATALOG);
    TaskInsertRequest request =
        TaskInsertRequest.builder().stfn(STFN_PATH).retryAttempts(0).userId("test").build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertTrue(e.getCause() instanceof ResourceNotFoundException);
      assertEquals(e.getResponse().getStatus(), NOT_FOUND.getStatusCode());
    }
  }

  @Test
  public void testPOSTDbException()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        new TaskResource(getResourceService(STORI), BROKEN_RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder()
      .stfn(STFN_PATH)
      .retryAttempts(0)
      .voName(VFS_VONAME)
      .pinLifetime(1223123)
      .userId("test")
      .build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  @Test
  public void testPOSTValidationRequestNullFilePath()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        new TaskResource(getResourceService(STORI), BROKEN_RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder().userId("test").build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
      assertEquals(e.getResponse().getEntity().toString(), "Request must contain a STFN");
    }
  }

  @Test
  public void testPOSTValidationRequestNullUserId()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        new TaskResource(getResourceService(STORI), BROKEN_RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder().stfn(STFN_PATH).build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
      assertEquals(e.getResponse().getEntity().toString(), "Request must contain a userId");
    }
  }

  @Test
  public void testPOSTValidationRequestInvalidNegativeRetryAttempts()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        new TaskResource(getResourceService(STORI), BROKEN_RECALL_CATALOG);
    TaskInsertRequest request =
        TaskInsertRequest.builder().stfn(STFN_PATH).userId("test").retryAttempts(-1).build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
      assertEquals(e.getResponse().getEntity().toString(),
          "Retry attempts must be more or equal than zero.");
    }
  }

  @Test
  public void testPOSTValidationRequestInvalidTooManyRetryAttempts()
      throws DataAccessException, NamespaceException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        new TaskResource(getResourceService(STORI), BROKEN_RECALL_CATALOG);
    TaskInsertRequest request = TaskInsertRequest.builder()
      .stfn(STFN_PATH)
      .userId("test")
      .retryAttempts(Integer.valueOf(MAX_RETRY_ATTEMPTS) + 1)
      .build();
    try {
      recallEndpoint.postNewTask(request);
      fail();
    } catch (WebApplicationException e) {
      assertEquals(e.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
      assertEquals(e.getResponse().getEntity().toString(),
          "Retry attempts must be less or equal than " + MAX_RETRY_ATTEMPTS + ".");
    }
  }

  private TapeRecallCatalog getTapeRecallCatalogInProgressNotEmpty() {

    List<TapeRecallTO> emptyList = new ArrayList<TapeRecallTO>();
    TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
    Mockito.when(catalog.getAllInProgressTasks(Mockito.anyInt())).thenReturn(emptyList);
    return catalog;
  }

  @Test
  public void testGETTasksInProgressEmpty()
      throws DataAccessException, NamespaceException, JsonParseException, JsonMappingException,
      IOException, InvalidTRequestTokenAttributesException, ResourceNotFoundException {

    TaskResource recallEndpoint =
        getTaskResource(getResourceService(STORI), getTapeRecallCatalogInProgressNotEmpty());
    Response res = recallEndpoint.getTasksInProgress(10);
    assertEquals(res.getStatus(), OK.getStatusCode());
  }


  private TapeRecallCatalog getTapeRecallCatalogGroupTaskIdNotExists(UUID groupTaskId)
      throws DataAccessException {

    TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
    Mockito.when(catalog.existsGroupTask(groupTaskId)).thenReturn(false);
    Mockito
      .when(catalog.changeGroupTaskStatus(Mockito.eq(groupTaskId),
          Mockito.any(TapeRecallStatus.class), Mockito.any(Date.class)))
      .thenThrow(new DataAccessException());
    return catalog;
  }

  private TapeRecallCatalog getTapeRecallCatalogGroupTaskIdExistsAndUpdateIsOk(UUID groupTaskId)
      throws DataAccessException {

    TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
    Mockito.when(catalog.existsGroupTask(groupTaskId)).thenReturn(true);
    Mockito
      .when(catalog.changeGroupTaskStatus(Mockito.eq(groupTaskId),
          Mockito.any(TapeRecallStatus.class), Mockito.any(Date.class)))
      .thenReturn(true);
    return catalog;
  }

  private TapeRecallCatalog getTapeRecallCatalogGroupTaskIdThrowExceptionOnUpdate(UUID groupTaskId,
      String errorMessage) throws DataAccessException {

    TapeRecallCatalog catalog = Mockito.mock(TapeRecallCatalog.class);
    Mockito.when(catalog.existsGroupTask(groupTaskId)).thenReturn(true);
    Mockito
      .when(catalog.changeGroupTaskStatus(Mockito.eq(groupTaskId),
          Mockito.any(TapeRecallStatus.class), Mockito.any(Date.class)))
      .thenThrow(new DataAccessException(errorMessage));
    return catalog;
  }

  @Test
  public void testPUTTaskStatusToSuccessWorks()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    final String BODY = "status=0";
    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    UUID groupTaskId = UUID.randomUUID();
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdExistsAndUpdateIsOk(groupTaskId));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), NO_CONTENT.getStatusCode());
  }

  @Test
  public void testPUTTaskRetryValueWorks()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    final String BODY = "retry-value=0";
    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    UUID groupTaskId = UUID.randomUUID();
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdExistsAndUpdateIsOk(groupTaskId));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), NO_CONTENT.getStatusCode());
  }

  @Test
  public void testPUTTaskStatusOnTaskIdNotFound()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    final String BODY = "status=0";
    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    UUID groupTaskId = UUID.randomUUID();
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdNotExists(groupTaskId));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), NOT_FOUND.getStatusCode());
    assertEquals(res.getEntity().toString(),
        "No Recall Group Task found with ID = '" + groupTaskId + "'");
  }

  @Test
  public void testPUTTaskStatusWithWrongKeyInBody()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    final String BODY = "wrong=0";
    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    UUID groupTaskId = UUID.randomUUID();
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdExistsAndUpdateIsOk(groupTaskId));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), BAD_REQUEST.getStatusCode());
    assertEquals(res.getEntity().toString(), INVALID_BODY);
  }

  @Test
  public void testPUTTaskStatusWithWrongValueInBody()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    final String BODY = "status=queued";
    final String EXPECTED_BODY = format(NOT_INT_VALUE, "queued");
    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    UUID groupTaskId = UUID.randomUUID();
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdExistsAndUpdateIsOk(groupTaskId));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), BAD_REQUEST.getStatusCode());
    assertEquals(res.getEntity().toString(), EXPECTED_BODY);
  }

  @Test
  public void testPUTTaskStatusWithBothStatusAndRetryValuesInBody()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    final String BODY = "status=0\nretry-value=2";
    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    UUID groupTaskId = UUID.randomUUID();
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdExistsAndUpdateIsOk(groupTaskId));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), BAD_REQUEST.getStatusCode());
    assertEquals(res.getEntity().toString(), "Expected one property. Found 2.");

  }

  @Test
  public void testPUTTaskStatusExceptionOnUpdate()
      throws NamespaceException, ResourceNotFoundException, DataAccessException {

    UUID groupTaskId = UUID.randomUUID();

    final String BODY = "status=0";
    final String EXPECTED_BODY = format(
        "Unable to change the status for group task id %s to status 0 DataAccessException : ErrorMessage",
        groupTaskId);

    InputStream stubInputStream = IOUtils.toInputStream(BODY, Charset.defaultCharset());
    TaskResource recallEndpoint = getTaskResource(getResourceService(STORI),
        getTapeRecallCatalogGroupTaskIdThrowExceptionOnUpdate(groupTaskId, "ErrorMessage"));
    Response res = recallEndpoint.updateGroupTasks(groupTaskId, stubInputStream);
    assertEquals(res.getStatus(), INTERNAL_SERVER_ERROR.getStatusCode());
    assertEquals(res.getEntity().toString(), EXPECTED_BODY);

  }
}
