/**
 * 
 */
package component.tape.recalltable;

import it.grid.storm.tape.recalltable.RecallTableException;
import it.grid.storm.tape.recalltable.model.RecallTaskData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 *
 */
public class RecallTaskDataTest {

    private static final Logger log = LoggerFactory.getLogger(RecallTaskDataTest.class);

    private RecallTaskData createTaskData(String stringFormat) {
        RecallTaskData rtd = null;
        try {
            rtd = new RecallTaskData(stringFormat);
        } catch (RecallTableException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        log.debug("RTD : " + rtd);
        return rtd;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String fnExample = "/gpfs_tsm/dtem/test1.txt";
        String dnExample = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni/Email=luca.magnoni@cnaf.infn.it";
        String dnExample2 = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Alberto Forti";
        String fqans = "[fqan:/infngrid/prod,fqan:/infngrid/test]";
        String rtdS = "{ filename:" + fnExample + " # dn:" + dnExample2;
        rtdS += "# fqans:" + fqans + "# vo-name:infngrid }";
        RecallTaskDataTest test = new RecallTaskDataTest();
        test.createTaskData(rtdS);
    }

}
