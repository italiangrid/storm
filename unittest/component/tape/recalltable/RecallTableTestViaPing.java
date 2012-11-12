package component.tape.recalltable;

import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.command.discovery.PingCommand;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.discovery.PingInputData;
import it.grid.storm.synchcall.data.discovery.PingOutputData;
import it.grid.storm.tape.recalltable.persistence.TapeRecallBuilder;

import java.io.File;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.namespace.config.FakeGridUser;
import component.naming.surl.SURLNamingTest;
import component.utiltest.SetUpTest;

public class RecallTableTestViaPing {

    private static Logger log = LoggerFactory.getLogger(RecallTableTestViaPing.class);
   
    
    private static void init() {

        //Logging Set Up
          
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
 

        int refreshInSeconds = 5;
        Configuration config = Configuration.getInstance();
        String configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
        String configurationFileName = configurationPATH + File.separator + "storm-test.properties";
        config.setConfigReader(new ConfigReader(configurationFileName, refreshInSeconds));

        log.debug(" CONFIGURATION = "+configurationFileName);

    }
    
    
    private static String buildTakeOverTasksInput() {
       String result = "take-over";
       String param = "";
       if (param.length()>0) {
          result = result+"="+param;
       }
       return result;
    }
    
    private static String buildPostNewTask(String filename, String dn, String voname) {
        
        String result = "new-task"; 
            
        String param = TapeRecallBuilder.taskStart;
        param += TapeRecallBuilder.fnPrefix;
        param += TapeRecallBuilder.equalChar+filename;
        param += TapeRecallBuilder.elementSep;
        param += TapeRecallBuilder.dnPrefix;
        param += TapeRecallBuilder.equalChar+dn;
        param += TapeRecallBuilder.elementSep;
        param += TapeRecallBuilder.fqansArrayStart;
        param += TapeRecallBuilder.fqanPrefix;
        param += TapeRecallBuilder.equalChar+voname;
        param += TapeRecallBuilder.fqansArrayEnd;
        param += TapeRecallBuilder.taskEnd;
        
        if (param.length()>0) {
            result = result+"="+param;
         }
        
        return result;
    }
    
    /**
     * Build input data
     */
    private static PingInputData buildInputData(FakeGridUser user, String authzId) {
        PingInputData input = new PingInputData(user, authzId);
        return input;         
    }
    
   
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        init();
        String fakeDN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Ciccio Pasticcio";
        String fakeVO = "dteam";
        FakeGridUser fakeUser = new FakeGridUser(fakeDN, fakeVO);
        
        PingCommand pc = new PingCommand();
        PingOutputData od = null;
        
        Random generator = new Random();
        for (int i = 0; i < 10; i++) {
            int randomIndex = generator.nextInt( 100 );
            String newTask = buildPostNewTask("/gpfs/dteam/file_"+randomIndex, fakeDN, fakeVO);
            od = (PingOutputData) pc.execute(buildInputData(fakeUser, newTask));
            log.info(od.toString());    
        }
  
        String takeOver = buildTakeOverTasksInput();
         od = (PingOutputData) pc.execute(buildInputData(fakeUser, takeOver));
        log.info(od.toString());

    }

}
