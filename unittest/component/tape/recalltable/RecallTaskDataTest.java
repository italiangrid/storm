/**
 * 
 */
package component.tape.recalltable;

import it.grid.storm.tape.recalltable.RecallTableException;
import it.grid.storm.tape.recalltable.model.RecallTaskData;
import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;

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
        log.debug("RTD : " + rtd.getRecallTaskData_textFormat());
        return rtd;
    }
    
    private void test1() {
        String fnExample = "/gpfs_tsm/dtem/test1.txt";
        String dnExample = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni/Email=luca.magnoni@cnaf.infn.it";
        String dnExample2 = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Alberto Forti";
        String fqanExample1 = RecallTaskBuilder.fqanPrefix + RecallTaskBuilder.equalChar + "/infngrid/prod";
        String fqanExample2 = RecallTaskBuilder.fqanPrefix + RecallTaskBuilder.equalChar + "/infngrid/test";
        String voNameExample1 = "ciccioVO";

        String fnElement = RecallTaskBuilder.fnPrefix + RecallTaskBuilder.equalChar + fnExample;
        String dnElement = RecallTaskBuilder.dnPrefix + RecallTaskBuilder.equalChar + dnExample2;
        String fqansElement = RecallTaskBuilder.fqansPrefix + RecallTaskBuilder.fqansArrayStart + fqanExample1
                + RecallTaskBuilder.fqanSep + fqanExample2 + RecallTaskBuilder.fqansArrayEnd;
        String voNameElement = RecallTaskBuilder.voNamePrefix + RecallTaskBuilder.equalChar + voNameExample1;

        String rtdS = RecallTaskBuilder.taskStart + fnElement + RecallTaskBuilder.elementSep + dnElement
                + RecallTaskBuilder.elementSep + fqansElement + RecallTaskBuilder.elementSep + voNameElement
                + RecallTaskBuilder.taskEnd;

        log.debug(rtdS);
        
        try {
            RecallTaskData rtd = new RecallTaskData(rtdS);
            log.debug("RTD = " + rtd.getRecallTaskData_textFormat());
        } catch (RecallTableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void test2() {
        String fromTSM = "{filename=/gpfs_tsm/dtem/test1.txt \n dn=C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,1.2.840.113549.1.9.1=#16196c7563612e6d61676e6f6e6940636e61662e696e666e2e6974 \n fqans=[fqan=/infngrid/prod,fqan=/infngrid/test] \n vo-name=ciccioVO}";
        try {
            RecallTaskData rtd = new RecallTaskData(fromTSM);
            log.debug("RTD = " + rtd.getRecallTaskData_textFormat());
        } catch (RecallTableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void test3() {
        String fromTSM = "{filename=/gpfs_tsm/dtem/test1.txt \n dn=C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Alberto Forti \n fqans=[fqan=/infngrid/prod,fqan=/infngrid/test] \n vo-name=ciccioVO}";
        try {
            RecallTaskData rtd = new RecallTaskData(fromTSM);
            log.debug("RTD = " + rtd.getRecallTaskData_textFormat());
        } catch (RecallTableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    /**
     * 
     */
    private void test4() {
        String fromString = RecallTaskBuilder.taskStart;
        fromString += RecallTaskBuilder.fnPrefix + RecallTaskBuilder.equalChar + "pincopallo fn";
        fromString += RecallTaskBuilder.elementSep;
        fromString += RecallTaskBuilder.voNamePrefix + RecallTaskBuilder.equalChar + "vo-ciccio";
        fromString += RecallTaskBuilder.taskEnd;    
        log.debug(fromString);   
        RecallTaskData rtd = new RecallTaskData();
        try {
            rtd = RecallTaskData.buildFromString(fromString);
        } catch (RecallTableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.debug("RTD = " + rtd.toString());

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        
        RecallTaskDataTest test = new RecallTaskDataTest();
        // test.test1();
        // test.test2();
        // test.test3();
        test.test4();
    }


}
