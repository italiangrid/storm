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
        } catch (IOException e) { } 
              
    }

}
