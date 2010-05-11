package component.namespace.userinfo;

import it.grid.storm.config.Configuration;
import it.grid.storm.startup.Bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RetrieveGroupInfo {

    

    /**
     * @param args
     */
    public static void main(String[] args) {
        String configurationDir = Configuration.getInstance().configurationDir();
        if ((configurationDir == null) || (configurationDir.length() == 0)) {
            configurationDir = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        }
        String logFile = configurationDir + "logging-test.xml";

        Bootstrap.initializeLogging(logFile);
        //getent group storm  | awk -F"," '{print $1}'| awk -F":" '{print $3}'
        try { // Execute a command with an argument that contains a space 
            String[] commands = new String[]{"getent", "group", "ritz"}; 
            Process child = Runtime.getRuntime().exec(commands); 
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
            //process the Command Output (Input for StoRM ;) )
            String line;
            int row = 0;
            System.out.println("UserInfo Command Output :");
            while ( (line = stdInput.readLine()) != null) {
                System.out.print(line);
            }
            
            String patternStr = ":"; 
            String[] fields = inputStr.split(patternStr); 
           
            String groupName = null;
            String gid = null;
            int gidInt = -1;
            
            if (fields[0]!=null) {
                System.out.println("field[0], group name ='"+groupName+"'");
                groupName = fields[0];
            } 
            if ((fields.length>1) and (fields[1]!=null) {
               System.out.println("field[1], encrypted group password (or x if shadow passwords are in use) ='"+fields[1]+"'");
            }
            if ((fields.length>2) and (fields[2]!=null) {  
               gid = fields[2];   
               System.out.println("field[2], GID ='"+gid+"'");
               try {
                 gidInt = Integer.parseInt(gid);
               } catch (NumberFormatException nfe) {
                   log.error("Unable to retrieve the GID number of groupName '"+groupName+"'");
               }
            }
            if ((fields.length>3) and (fields[3]!=null) {
                System.out.println("field[3], group members' usernames, comma-separated ='"+fields[3]+"'");
            }      
        } catch (IOException e) { 
            log.error("Unable to retrieve the GID number ");
        } 
              
    }

}
