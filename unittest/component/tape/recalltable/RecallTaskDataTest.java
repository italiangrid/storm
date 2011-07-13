/**
 * 
 */
package component.tape.recalltable;

import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.TapeRecallData;
import it.grid.storm.tape.recalltable.persistence.TapeRecallBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 *
 */
public class RecallTaskDataTest {

    private static final Logger log = LoggerFactory.getLogger(RecallTaskDataTest.class);

    private TapeRecallData createTaskData(String stringFormat) {
        TapeRecallData rtd = null;
        try {
            rtd = new TapeRecallData(stringFormat);
        } catch (TapeRecallException e) {
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
        String fqanExample1 = TapeRecallBuilder.fqanPrefix + TapeRecallBuilder.equalChar + "/infngrid/prod";
        String fqanExample2 = TapeRecallBuilder.fqanPrefix + TapeRecallBuilder.equalChar + "/infngrid/test";
        String voNameExample1 = "ciccioVO";

        String fnElement = TapeRecallBuilder.fnPrefix + TapeRecallBuilder.equalChar + fnExample;
        String dnElement = TapeRecallBuilder.dnPrefix + TapeRecallBuilder.equalChar + dnExample2;
        String fqansElement = TapeRecallBuilder.fqansPrefix + TapeRecallBuilder.fqansArrayStart + fqanExample1
                + TapeRecallBuilder.fqanSep + fqanExample2 + TapeRecallBuilder.fqansArrayEnd;
        String voNameElement = TapeRecallBuilder.voNamePrefix + TapeRecallBuilder.equalChar + voNameExample1;

        String rtdS = TapeRecallBuilder.taskStart + fnElement + TapeRecallBuilder.elementSep + dnElement
                + TapeRecallBuilder.elementSep + fqansElement + TapeRecallBuilder.elementSep + voNameElement
                + TapeRecallBuilder.taskEnd;

        log.debug(rtdS);
        
        try {
            TapeRecallData rtd = new TapeRecallData(rtdS);
            log.debug("RTD = " + rtd.getRecallTaskData_textFormat());
        } catch (TapeRecallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void test2() {
        String fromTSM = "{filename=/gpfs_tsm/dtem/test1.txt \n dn=C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Luca Magnoni,1.2.840.113549.1.9.1=#16196c7563612e6d61676e6f6e6940636e61662e696e666e2e6974 \n fqans=[fqan=/infngrid/prod,fqan=/infngrid/test] \n vo-name=ciccioVO}";
        try {
            TapeRecallData rtd = new TapeRecallData(fromTSM);
            log.debug("RTD = " + rtd.getRecallTaskData_textFormat());
        } catch (TapeRecallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void test3() {
        String fromTSM = "{filename=/gpfs_tsm/dtem/test1.txt \n dn=C=IT,O=INFN,OU=Personal Certificate,L=CNAF,CN=Alberto Forti \n fqans=[fqan=/infngrid/prod,fqan=/infngrid/test] \n vo-name=ciccioVO}";
        try {
            TapeRecallData rtd = new TapeRecallData(fromTSM);
            log.debug("RTD = " + rtd.getRecallTaskData_textFormat());
        } catch (TapeRecallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    /**
     * 
     */
    private void test4() {
        String fromString = TapeRecallBuilder.taskStart;
        fromString += TapeRecallBuilder.fnPrefix + TapeRecallBuilder.equalChar + "pincopallo fn";
        fromString += TapeRecallBuilder.elementSep;
        fromString += TapeRecallBuilder.voNamePrefix + TapeRecallBuilder.equalChar + "vo-ciccio";
        fromString += TapeRecallBuilder.taskEnd;    
        log.debug(fromString);   
        TapeRecallData rtd = new TapeRecallData();
        try {
            rtd = TapeRecallData.buildFromString(fromString);
        } catch (TapeRecallException e) {
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
