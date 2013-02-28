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

package it.grid.storm.wrapper;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>Title: Passwd </p>
 *
 * <p>Description: Read and Digest of passwd file</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */


public class Passwd {

  private Map nameMap = new Hashtable();
  private Map uidMap = new Hashtable();
  private PasswdRefresher refresher;
  private File passwdFile;

  /** Access an arbitrary passwd format file. */
  public Passwd(String path, boolean refresh) {
    this.passwdFile = new File(path);
    if (refresh) {
      refresher = new PasswdRefresher(this);
    }
  }

  /** Access the standard passwd file.  For unix, this is
      <code>/etc/passwd</code>.
   */
  public Passwd() {
    this("/etc/passwd", false);
  }

  /**
   * Digest Passwd file
   */
  public void digestPasswd() {
    emptyMap();
    try {
      BufferedReader in = new BufferedReader(new FileReader(passwdFile));
      String str;
      PasswdRow row;
      while ( (str = in.readLine()) != null) {
        row = digestPasswdRow(str);
        if (row != null)
          insertRow(row);
      }
      in.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Clean both Map
   */
  private void emptyMap()
  {
    nameMap.clear();
    uidMap.clear();
  }

  /**
   * insertRow
   *
   * @param row PasswdRow
   */
  private void insertRow(PasswdRow row) {
    nameMap.put(row.getName(), row);
    uidMap.put(new Integer(row.getUID()), row);
  }

  /**
   *
   * @param line String
   * @return PasswdRow
   */
  private PasswdRow digestPasswdRow(String line) {
    PasswdRow passwdRow = null;
    if (line != null) {
      StringTokenizer tok = new StringTokenizer(line, ":");
      String[] row = new String[7];
      for (int i = 0; i < row.length && tok.hasMoreTokens(); ++i) {
        row[i] = tok.nextToken();
      }
      passwdRow = new PasswdRow(row);
    }
    return passwdRow;
  }

  /**
   *
   * @param name String
   * @return boolean
   */
  public boolean existsByName(String name) {
    return nameMap.containsKey(name);
  }

  /**
   *
   * @param uid int
   * @return boolean
   */
  public boolean existsByUID(int uid) {
    return uidMap.containsKey(new Integer(uid));
  }

  /**
   *
   * @return File
   */
  protected File getPasswdFile()
  {
    return this.passwdFile;
  }

  /**
   *
   * @param name String
   * @return PasswdRow
   */
  protected PasswdRow getEntryByName(String name) {
    return (PasswdRow) nameMap.get(name);
  }

  /**
   *
   * @param name String
   * @return PasswdRow
   */
  protected PasswdRow getEntryByUID(int uid) {
    return (PasswdRow) uidMap.get(new Integer(uid));
  }

  /**
   *
   * @param uid int
   * @return String
   */
  public String getNameByUID(int uid) {
    return getEntryByUID(uid).getName();
  }

  /**
   *
   * @param name String
   * @return int
   */
  public int getUIDbyName(String name) {
    int result = -1;
    PasswdRow row = getEntryByName(name);
    if (row != null) result = row.getUID();
    return result;
  }

  /**
   *
   * @return int
   */
  public int getRowNumber() {
    return (nameMap.size());
  }

  /**
   *
   * <p>Title: PasswdRefresher </p>
   *
   * <p>Description: </p>
   *
   * <p>Copyright: Copyright (c) 2005</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  private static class PasswdRefresher {

    int delay = 5000; // delay for 5 sec.
    int period = 1000; // repeat every sec.
    Timer timer = new Timer();
    Passwd passwd;
    private long modifiedTime = -1;

    /**
     *
     * @param passwd Passwd
     * @param period int
     */
    public PasswdRefresher(Passwd passwd, int period) {
      this.period = period;
      this.passwd = passwd;
      schedule();
    }

    /**
     *
     * @param passwd Passwd
     */
    public PasswdRefresher(Passwd passwd) {
      this.passwd = passwd;
      // Get the last modified time
      modifiedTime = (passwd.getPasswdFile()).lastModified();
      schedule();
    }

    /**
     *
     */
    private void schedule() {
      timer.scheduleAtFixedRate(new TimerTask() {
        long newTime = -1;
        public void run() {
          newTime = (passwd.getPasswdFile()).lastModified();
          if (newTime>modifiedTime)
          {
            System.out.println("####");
            modifiedTime = newTime;
            passwd.digestPasswd();
          }
        }
      }, delay, period);
    }
  }
    /**
     *
     * <p>Title: PasswdRow</p>
     *
     * <p>Description: Single row of Passwd File</p>
     *
     * <p>Copyright: Copyright (c) 2005</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    private static class PasswdRow {

      public String name;
      public String passwd;
      public int uid;
      public int gid;
      public String gecos;
      public String homedir;
      public String shell;

      /**
       *
       * @param name String
       * @param passwd String
       * @param uid int
       * @param gid int
       * @param gesos String
       * @param homedir String
       * @param shell String
       */
      public PasswdRow(String item[]) {
        this.name = item[0];
        this.passwd = item[1];
        this.uid = parseNum(item[2]);
        this.gid = parseNum(item[3]);
        this.gecos = item[4];
        ;
        this.homedir = item[5];
        this.shell = item[6];
      }

      /**
       *
       * @param s String
       * @return int
       */
      private static int parseNum(String s) {
        if (s == null) {
          return -1;
        }
        try {
          return Integer.parseInt(s.trim());
        }
        catch (NumberFormatException n) {
          return -1;
        }
      }

      /**
       *
       * @return String
       */
      public String getName() {
        return name;
      }

      /**
       *
       * @return int
       */
      public int getUID() {
        return uid;
      }

      /**
       *
       * @return String
       */
      public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("Name : ");
        result.append(name);
        result.append(" UID : ");
        result.append(gid);
        result.append(" GID : ");
        result.append(uid);
        return result.toString();
      }
    }
  }

