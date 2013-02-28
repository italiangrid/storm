/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Platform {

    private static final String platformName = System.getProperty("os.name") + "-"
            + System.getProperty("os.arch") + "-" + System.getProperty("sun.arch.data.model");

    /**
     * Get the complete platform as per the java-vm.
     * @return returns the complete platform as per the java-vm.
     */
    public static String getPlatformName() {
        return platformName;
    }

    public static String getOSDitribution() {
        String os_dist = "N/A";
        String releaseFileStr = File.separatorChar + "etc" + File.separatorChar + "redhat-release";
        File releaseFile = new File(releaseFileStr);
        try {
            if (releaseFile.exists() && releaseFile.isFile() && releaseFile.canRead()) {
                BufferedReader releaseReader = new BufferedReader(new FileReader(releaseFile));
                String output = releaseReader.readLine();
                if ((output != null) && (output.length() > 0)) {
                    os_dist = output;
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        //String os_dist="Scientific Linux SL release 4.8 (Beryllium)";
        //String os_dist="Scientific Linux SL release 5.3 (Boron)";
        if (os_dist != null) {
            int pos = os_dist.indexOf("release");
            if ((pos > 0) && (os_dist.length() > pos + 9)) {
                String rel = os_dist.substring(pos + 7, pos + 9).trim();
                return "sl"+rel;
            }
        }
        return os_dist;
    }
    
    
    
    public static void main(String[] args) {
        if ((args==null)||(args.length==0)||(args[0].equals("platform"))) {
            System.out.println(platformName);    
        } else if (args[0].equals("os_dist")) {
            System.out.println(getOSDitribution());
        }
    }
}
