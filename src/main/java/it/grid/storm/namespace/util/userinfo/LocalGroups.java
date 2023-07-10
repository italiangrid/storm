/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.namespace.util.userinfo;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public final class LocalGroups {

  private static final Logger log = LoggerFactory.getLogger(LocalGroups.class);

  private static final String UNKNOWN_GROUP = "unknown";

  private Map<String, Integer> nameIdMap = new ConcurrentHashMap<String, Integer>();
  private Map<Integer, String> idNameMap = new ConcurrentHashMap<Integer, String>();
  private static final LocalGroups instance = new LocalGroups();
  private long parsingInstant = 0;
  private static final long minimumLifetime = 1000 * 60 * 5; // 10 minutes;

  private LocalGroups() {

    init();
  }

  private synchronized void init() {

    nameIdMap.clear();
    idNameMap.clear();
    // Parsing all the database and cache it
    nameIdMap.putAll(UserInfoExecutor.digestGroupDatabase());
    for (Entry<String, Integer> nameIdEntry : nameIdMap.entrySet()) {
      idNameMap.put(nameIdEntry.getValue(), nameIdEntry.getKey());
    }
    parsingInstant = System.currentTimeMillis();
  }

  public static synchronized LocalGroups getInstance() {

    if (instance.computeParsedAge() > LocalGroups.minimumLifetime) {
      instance.init();
    }
    return instance;
  }

  private long computeParsedAge() {

    return System.currentTimeMillis() - parsingInstant;
  }

  public boolean isGroupDefined(String groupName) {

    boolean result = nameIdMap.keySet().contains(groupName);
    if (!result) {
      try {
        Integer grupId = Integer.valueOf(groupName);
        if (grupId != null) {
          result = idNameMap.containsKey(grupId);
        }
      } catch (NumberFormatException e) {
        // not a number, just an attempt failed
      }
    }
    return result;
  }

  public int getGroupId(String groupName) {

    int result = -1;
    if (isGroupDefined(groupName)) {
      result = nameIdMap.get(groupName).intValue();
    }
    return result;
  }

  public String getGroupName(int groupId) {

    String result = UNKNOWN_GROUP;
    if (idNameMap.containsKey(Integer.valueOf(groupId))) {
      result = idNameMap.get(Integer.valueOf(groupId));
    } else {
      log.warn("Unable to find a group with GID='" + groupId + "'");
    }
    return result;
  }
}
