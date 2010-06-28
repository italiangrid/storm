package component.namespace.userinfo;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.util.userinfo.LocalGroups;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


public class RetrieveGroupInfo {

    private static Logger log = LoggerFactory.getLogger(RetrieveGroupInfo.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        String configurationDir = Configuration.getInstance().configurationDir();
        if ((configurationDir == null) || (configurationDir.length() == 0)) {
            configurationDir = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        }
        String logFile = configurationDir + "logging-test.xml";
        SetUpTest.init(logFile);
         
        int gid = LocalGroups.getGroupId("ritz");         
        log.debug("GID of ritz : "+gid);
        HashMap<String,Integer> gdb = new HashMap<String, Integer>();
        gdb.putAll(LocalGroups.getGroupDB());
        
        Iterator<String> iterator = gdb.keySet().iterator();      
        while (iterator.hasNext()) {  
            String key = iterator.next();  
            String value = gdb.get(key).toString();  
            
            log.debug(key + " " + value);  
        }  
        
    }

}
