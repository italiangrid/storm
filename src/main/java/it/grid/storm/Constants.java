/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class Constants {

  private static final Logger log = LoggerFactory.getLogger(Constants.class);

  public static final Entry BE_VERSION;
  public static final Entry NAMESPACE_VERSION;
  public static final Entry BE_OS_DISTRIBUTION;
  public static final Entry BE_OS_PLATFORM;
  public static final Entry BE_OS_KERNEL_RELEASE;

  private static final String BE_OS_PLATFORM_KEY = "BE-OS-Platform";
  private static final String BE_OS_KERNEL_RELEASE_KEY = "BE-OS-Kernel-Release";

  private static final String NOT_AVAILABLE = "N/A";

  private Constants() {}

  static {
    BE_VERSION = new Entry("BE-Version", Constants.class.getPackage().getImplementationVersion());
    NAMESPACE_VERSION = new Entry("Namespace-version", "1.5.0");
    BE_OS_DISTRIBUTION = new Entry("BE-OS-Distribution", getDistribution());
    Map<String, String> map = getPlatformKernel();
    BE_OS_PLATFORM = new Entry(BE_OS_PLATFORM_KEY, map.get(BE_OS_PLATFORM_KEY));
    BE_OS_KERNEL_RELEASE = new Entry(BE_OS_KERNEL_RELEASE_KEY, map.get(BE_OS_KERNEL_RELEASE_KEY));
  }

  /**
   * 
   */
  private static String getDistribution() {

    String distribution = NOT_AVAILABLE;
    String releaseFilePath = File.separatorChar + "etc" + File.separatorChar + "redhat-release";
    File releaseFile = new File(releaseFilePath);
    if (!releaseFile.exists() || !releaseFile.isFile() || !releaseFile.canRead()) {
      log.warn("Unable to read {} file!!", releaseFile.getAbsolutePath());
      return distribution;
    }
    FileReader fr;
    try {
      fr = new FileReader(releaseFile);
    } catch (FileNotFoundException e) {
      log.error("Unable to find file '{}'. {}", releaseFile.getAbsolutePath(), e);
      return distribution;
    }
    BufferedReader br = new BufferedReader(fr);
    try {
      String line = br.readLine();
      if (line == null) {
        log.warn("The file {} is empty!", releaseFile.getAbsolutePath());
      } else {
        distribution = line;
      }
    } catch (IOException e) {
      log.error("Unable to read file '{}'. {}", releaseFile.getAbsolutePath(), e);
    } finally {
      try {
        br.close();
        fr.close();
      } catch (IOException e) {
        log.error("Unable to close file '{}'. {}", releaseFile.getAbsolutePath(), e);
      }
    }
    return distribution;
  }

  /**
   * 
   */
  private static Map<String, String> getPlatformKernel() {

    Map<String, String> map = Maps.newHashMap();
    map.put(BE_OS_KERNEL_RELEASE_KEY, NOT_AVAILABLE);
    map.put(BE_OS_PLATFORM_KEY, NOT_AVAILABLE);
    try {
      Process p = Runtime.getRuntime().exec("uname -ri");
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

      String error = stdError.readLine();
      String output = stdInput.readLine();
      if (output == null || stdInput.read() != -1 || error != null) {
        while (error != null) {
          error += stdError.readLine();
        }
        log.error("Unable to invoke \'uname -ri\' . Standard error : {}", error);
      } else {
        String[] fields = output.trim().split(" ");
        map.put(BE_OS_KERNEL_RELEASE_KEY, fields[0]);
        map.put(BE_OS_PLATFORM_KEY, fields[1]);
      }
    } catch (IOException e) {
      log.error("Unable to invoke \'uname -ri\' . IOException {}", e);
    }
    return map;
  }

  public static class Entry {

    private final String key;
    private final String value;

    private Entry(String key, String value) {

      this.key = key;
      this.value = value;
    }

    public String getKey() {

      return key;
    }

    public String getValue() {

      return value;
    }
  }
}
