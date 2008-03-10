package it.grid.storm.namespace.config;

import java.util.*;

import it.grid.storm.namespace.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */

public interface NamespaceParser {

    public String getNamespaceVersion();

    public Map getVFSs();

    public VirtualFSInterface getVFS(String vfsName);

    public List getAllVFS_Roots();

    public Map getMapVFS_Root();

    public List getAllMappingRule_StFNRoots();

    public Map getMappingRules();

    public Map getMappingRuleMAP();

    public Map getApproachableRules();

    public long getLastUpdateTime();

}
