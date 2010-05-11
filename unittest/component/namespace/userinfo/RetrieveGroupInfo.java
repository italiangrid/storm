package component.namespace.userinfo;

import it.grid.storm.config.Configuration;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
         
        //getent group storm  | awk -F"," '{print $1}'| awk -F":" '{print $3}'
        try { // Execute a command with an argument that contains a space 
            String[] commands = new String[]{"getent", "group", "ritz"}; 
            Process child = Runtime.getRuntime().exec(commands); 
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
            //process the Command Output (Input for StoRM ;) )
            String line = null;
  
            log.info("UserInfo Command Output :");
            line = stdInput.readLine();
            
            String groupName = null;
            String gid = null;
            int gidInt = -1;
            
            String patternStr = ":"; 
            String[] fields = null;
            if (line!=null) {
                log.info("LINE = "+line);
                fields = line.split(patternStr); 
                if (fields[0]!=null) {
                    groupName = fields[0];
                    log.debug("field[0], group name ='"+groupName+"'");
                } 
                if ((fields.length>1) && (fields[1]!=null)) {
                   log.debug("field[1], encrypted group password (or x if shadow passwords are in use) ='"+fields[1]+"'");
                }
                if ((fields.length>2) && (fields[2]!=null) ){  
                   gid = fields[2];   
                   log.debug("field[2], GID ='"+gid+"'");
                   try {
                     gidInt = Integer.parseInt(gid);
                   } catch (NumberFormatException nfe) {
                       log.error("Unable to retrieve the GID number of groupName '"+groupName+"'");
                   }
                }
                if ((fields.length>3) && (fields[3]!=null)) {
                    log.debug("field[3], group members' usernames, comma-separated ='"+fields[3]+"'");
                }      
                    
            }
            
                        
        } catch (IOException e) { 
            log.error("Unable to retrieve the GID number ");
        } 
              
    }

}
