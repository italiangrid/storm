/**
 * 
 */
package component.tape.recalltable;

import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zappi
 *
 */
public class TaskResourceTest {
    
    private static final Logger log = LoggerFactory.getLogger(TaskResourceTest.class);
   
    public static String hostName = "http://storm-test-04-a.cr.cnaf.infn.it:9998"; 
    
    
    private static void testPutOnTask(String taskId, String putBodyString) throws IOException, HttpException {

        PutMethod putMethod = new PutMethod(hostName + "/recalltable/task/" + taskId);
        RequestEntity entity = new InputStreamRequestEntity(new ByteArrayInputStream(putBodyString.getBytes()),
                "text/plain");
        putMethod.setRequestEntity(entity);
        HttpClient client = new HttpClient();
        try {
            int result = client.executeMethod(putMethod);
            log.debug("Response status code: " + result);
            log.debug("Response headers:");
            Header[] headers = putMethod.getResponseHeaders();
            for (Header header : headers) {
                log.debug(header.toString());
            }
        } finally {
            putMethod.releaseConnection();
        }
    }
 
    private static String testPostNewTask(String postBodyString) throws IOException, HttpException {

        String taskId = null;
        PostMethod postMethod = new PostMethod(hostName + "/recalltable/task");
        RequestEntity entity = new InputStreamRequestEntity(new ByteArrayInputStream(postBodyString.getBytes()),
                "text/plain");
        postMethod.setRequestEntity(entity);
        HttpClient client = new HttpClient();
        try {
            int result = client.executeMethod(postMethod);
            log.debug("Response status code: " + result);
            log.debug("Response headers:");
            Header[] headers = postMethod.getResponseHeaders();
            for (Header header : headers) {
                if (header.getName().equals("Location")) {
                    // log.debug("found");
                    String newResource = header.getValue();
                    // log.debug("New resource = '" + newResource + "'");
                    String[] elements = newResource.split("/");
                    // log.debug("elements : " + elements.length);
                    taskId = elements[elements.length - 1];
                    log.debug("Task-id='" + taskId + "'");
                    
                }
                // log.debug(header.toString());
            }
            String responseBody = postMethod.getResponseBodyAsString();
            log.debug("Body Response = '" + responseBody + "'");
            
        } finally {
            postMethod.releaseConnection();
        }
        return taskId;
    }

    private void testSetStatus(String taskId, String statusString) throws HttpException, IOException {
        testPutOnTask(taskId, statusString);
    }

    private void testSetRetryValue(String taskId, String retryValueString) throws HttpException, IOException {
        testPutOnTask(taskId, retryValueString);
    }

    private String createTextPostBody(String filename, String dn, String[] fqans, String voName) {
        String result = "";
        /**
         * String fnExample = "/gpfs_tsm/dtem/test1.txt"; String dnExample ="/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni/Email=luca.magnoni@cnaf.infn.it"
         * ; String dnExample2 =
         * "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Alberto Forti";
         * String fqanExample1 = RecallTaskBuilder.fqanPrefix +
         * "/infngrid/prod"; String fqanExample2 = RecallTaskBuilder.fqanPrefix
         * + "/infngrid/test"; String voNameExample1 = "ciccioVO";
         **/

        String fnElement = RecallTaskBuilder.fnPrefix + filename;
        String dnElement = RecallTaskBuilder.dnPrefix + dn;
        String fqansElement = RecallTaskBuilder.fqansPrefix + RecallTaskBuilder.fqansArrayStart;
        for (int i = 0; i < fqans.length; i++) {
            fqansElement += RecallTaskBuilder.fqanPrefix + fqans[i];
            if (i < fqans.length - 1) {
                fqansElement += RecallTaskBuilder.fqanSep;
            }
        }

        fqansElement += RecallTaskBuilder.fqansArrayEnd;
        String voNameElement = RecallTaskBuilder.voNamePrefix + voName;

        String rtdS = RecallTaskBuilder.taskStart + fnElement + RecallTaskBuilder.elementSep + dnElement
                + RecallTaskBuilder.elementSep + fqansElement + RecallTaskBuilder.elementSep + voNameElement
                + RecallTaskBuilder.taskEnd;
        return rtdS;
    }

    private String testCreateNewTask(String newTaskString) throws HttpException, IOException {
        return testPostNewTask(newTaskString);
    }    
    
    public static void main(String[] args) {
        TaskResourceTest tester = new TaskResourceTest();
        
        try {
            // Update the Status of a Task ID
            // tester.testSetStatus("status=1");

            // Update the Retry value of a Task ID
            // tester.testSetRetryValue("retry-value=1");
            
            String fnExample = "/gpfs_tsm/dtem/test1.txt";
            String dnExample = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Alberto Forti";
            String[] fqansExample = new String[2];
            fqansExample[0] = "/infngrid/prod";
            fqansExample[1] = "/infngrid/test";
            String voNameExample = "ciccioVO";
            
            String postBody = tester.createTextPostBody(fnExample, dnExample, fqansExample, voNameExample);
            log.debug(postBody);
            String taskId = tester.testCreateNewTask(postBody);
            
            tester.testSetStatus(taskId, "status=2");
            tester.testSetRetryValue(taskId, "retry-value=7");
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}
