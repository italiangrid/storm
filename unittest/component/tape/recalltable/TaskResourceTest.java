/**
 * 
 */
package component.tape.recalltable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
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
    
    private static void testPutOnTask(String putBodyString) throws IOException, HttpException {

        PutMethod putMethod = new PutMethod("http://localhost:9998/recalltable/task/abc123");
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

    public static void main(String[] args) {
        try {
            // Update the Status of a Task ID
            TaskResourceTest.testPutOnTask("status=1");

            // Update the Retry value of a Task ID
            TaskResourceTest.testPutOnTask("retry-value=1");
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}
