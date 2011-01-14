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

package it.grid.storm.namespace;

import it.grid.storm.common.GUID;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.StFN;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.Space;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.config.NamespaceParser;
import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.naming.NamespaceUtil;
import it.grid.storm.namespace.naming.NamingConst;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.slf4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class Namespace implements NamespaceInterface {

    private final Logger log = NamespaceDirector.getLogger();
    private final NamespaceParser parser;

    /**
     * Class CONSTRUCTOR
     *
     * @param parser NamespaceParser
     */
    public Namespace(NamespaceParser parser) {
        this.parser = parser;
    }

    public String getNamespaceVersion() throws NamespaceException {
        return parser.getNamespaceVersion();
    }

    /* (non-Javadoc)
     * @see it.grid.storm.namespace.NamespaceInterface#getAllDefinedVFS()
     */
    public Collection<VirtualFSInterface> getAllDefinedVFS() throws NamespaceException {
        return parser.getVFSs().values();
    }

    public List<VirtualFSInterface> getApproachableVFS(GridUserInterface user) throws NamespaceException {
        return null;
    }

    public VirtualFSInterface getDefaultVFS(GridUserInterface user) throws NamespaceException {
        TreeSet<ApproachableRule> appRules = new TreeSet<ApproachableRule>(getApproachableRules(user));
        if(appRules.isEmpty())
        {
            if(user instanceof VomsGridUser)
            {
                log.error("No approachable rules found for user with DN='" + user.getDn() + "' and VO = '"
                        + ((VomsGridUser) user).getVO() + "'");
                throw new NamespaceException("No approachable rules found for user with DN='" + user.getDn()
                        + "' and VO = '" + ((VomsGridUser) user).getVO() + "'");
            }
            else
            {
                log.error("No approachable rules found for user with DN='" + user.getDn()
                        + "' User certificate has not VOMS extension");
                throw new NamespaceException("No approachable rules found for user with DN='" + user.getDn()
                        + "' User certificate has not VOMS extension");
            }
        }
        log.debug("Compatible Approachable rules : " + appRules);
        ApproachableRule firstAppRule = appRules.first();
        log.debug("Default APP_RULE is the first (in respsect of name): " + firstAppRule);

        //Retrieve default VFS for the first Approachable Rule compatible for the user.
        VirtualFSInterface vfs = getApproachableDefaultVFS(firstAppRule);
        log.debug("Default VFS for Space Files : " + vfs);
        return vfs;

    }

    public boolean isApproachable(StoRI storageResource, GridUserInterface gridUser) throws NamespaceException {
        return true;
    }

    public StoRI resolveStoRIbySURL(TSURL surl, GridUserInterface user) throws NamespaceException {

        HashSet vfsNamesApproachable = (HashSet) getListOfVFSName(user);

        return resolveStoRIbySURL(surl, vfsNamesApproachable);
    }

    /**
     *
     * The resolution is based on the retrieving of the Winner Rule
     * 1) First attempt is based on StFN-Path
     * 2) Second attempt is based on all StFN. That because is possible that SURL
     *    is expressed without File Name so StFN is a directory.
     *    ( Special case is when the SFN does not contain the File Name and ALL
     *      the StFN is considerable as StFN-Path. )
     *
     * @param surl TSURL
     * @return StoRI
     * @throws NamespaceException
     */
    public StoRI resolveStoRIbySURL(TSURL surl, HashSet vfsNamesApproachable) throws NamespaceException {
        StFN stfn = surl.sfn().stfn();
        String stfnStr = stfn.toString();
        String stfnPath = NamespaceUtil.getStFNPath(stfnStr);
        MappingRule winnerRule = getWinnerRule(stfnPath, vfsNamesApproachable);
        if (winnerRule == null) { //No winner rule found.
            //Last chance thinking stfnStr as stfnPath
            winnerRule = getWinnerRule(stfnStr, vfsNamesApproachable);
            if (winnerRule == null) {
                throw new NamespaceException("Malformed SURL Exception", new MalformedSURLException(surl));
            }
        }
        log.debug("With StFN path =" + stfnPath + " the winner Rule is " + winnerRule.getRuleName());
        VirtualFSInterface vfs = parser.getVFS(winnerRule.getMappedFS());
        if (vfs == null) {
            throw new NamespaceException("Mapping rule '" + winnerRule.getRuleName() + "' does not detect no one VFS");
        }
        String stfnRoot = winnerRule.getStFNRoot();
        log.debug("StFN-root is " + stfnRoot);
        String relat = NamespaceUtil.extractRelativePath(stfnRoot, stfnStr);
        log.debug("Relative StFN is " + relat);

        StoRI stori = vfs.createFile(relat, StoRIType.FILE);
        stori.setStFNRoot(stfnRoot);
        stori.setMappingRule(winnerRule);

        return stori;
    }

    /**
     *
     * The resolution is based on the retrieving of the Winner Rule
     * 1) First attempt is based on StFN-Path
     * 2) Second attempt is based on all StFN. That because is possible that SURL
     *    is expressed without File Name so StFN is a directory.
     *    ( Special case is when the SFN does not contain the File Name and ALL
     *      the StFN is considerable as StFN-Path. )
     *
     * @param surl TSURL
     * @return StoRI
     * @throws NamespaceException
     */
    public StoRI resolveStoRIbySURL(TSURL surl) throws NamespaceException {
        StFN stfn = surl.sfn().stfn();
        String stfnStr = stfn.toString();
        String stfnPath = NamespaceUtil.getStFNPath(stfnStr);
        MappingRule winnerRule = getWinnerRuleWithoutApproachableRule(stfnPath);
        if (winnerRule == null) { //No winner rule found.
            //Last chance thinking stfnStr as stfnPath
            winnerRule = getWinnerRuleWithoutApproachableRule(stfnStr);
            if (winnerRule == null) {
                throw new NamespaceException("Malformed SURL Exception", new MalformedSURLException(surl));
            }
        }
        log.debug("With StFN path =" + stfnPath + " the winner Rule is " + winnerRule.getRuleName());
        VirtualFSInterface vfs = parser.getVFS(winnerRule.getMappedFS());
        if (vfs == null) {
            throw new NamespaceException("Mapping rule '" + winnerRule.getRuleName() + "' does not detect no one VFS");
        }
        String stfnRoot = winnerRule.getStFNRoot();
        log.debug("StFN-root is " + stfnRoot);
        String relat = NamespaceUtil.extractRelativePath(stfnRoot, stfnStr);
        log.debug("Relative StFN is " + relat);

        StoRI stori = vfs.createFile(relat, StoRIType.FILE);
        stori.setStFNRoot(stfnRoot);
        stori.setMappingRule(winnerRule);

        return stori;
    }

    public VirtualFSInterface resolveVFSbySURL(TSURL surl, GridUserInterface user) throws NamespaceException {

        HashSet vfsNamesApproachable = (HashSet) getListOfVFSName(user);
        return resolveVFSbySURL(surl, vfsNamesApproachable);
    }

    public VirtualFSInterface resolveVFSbySURL(TSURL surl, HashSet vfsNamesApproachable) throws NamespaceException {
        StFN stfn = surl.sfn().stfn();
        String stfnStr = stfn.toString();
        String stfnPath = NamespaceUtil.getStFNPath(stfnStr);
        MappingRule winnerRule = getWinnerRule(stfnPath, vfsNamesApproachable);
        log.debug("With StFN path =" + stfnPath + " the winner Rule is " + winnerRule.getRuleName());
        VirtualFSInterface vfs = parser.getVFS(winnerRule.getMappedFS());
        if (vfs == null) {
            throw new NamespaceException("Mapping rule '" + winnerRule.getRuleName() + "' does not detect no one VFS");
        }
        return vfs;
    }

    public StoRI resolveStoRIbyAbsolutePath(String absolutePath, GridUserInterface user) throws NamespaceException {
        return resolveStoRIbyAbsolutePath(absolutePath);
    }

    public StoRI resolveStoRIbyAbsolutePath(String absolutePath) throws NamespaceException {
        VirtualFSInterface vfs = resolveVFSbyAbsolutePath(absolutePath);
        log.debug("VFS retrivied is " + vfs.getAliasName());
        log.debug("VFS instance is " + vfs.hashCode());
        String relativePath = NamespaceUtil.extractRelativePath(vfs.getRootPath(), absolutePath);
        StoRI stori = vfs.createFile(relativePath);
        return stori;
    }

    public VirtualFSInterface resolveVFSbyAbsolutePath(String absolutePath, GridUserInterface user)
            throws NamespaceException {
        /**
         * @todo Check the approachable rules
         */
        return getWinnerVFS(absolutePath);
    }

    /**
     * Method used by srmGetSpaceMetadata
     *
     * @param absolutePath String
     * @return VirtualFSInterface
     * @throws NamespaceException
     */
    public VirtualFSInterface resolveVFSbyRoot(String absolutePath) throws NamespaceException {
        return getWinnerVFS(absolutePath);
    }

    public VirtualFSInterface resolveVFSbyAbsolutePath(String absolutePath) throws NamespaceException {
        return getWinnerVFS(absolutePath);
    }

    public StoRI resolveStoRIbyLocalFile(LocalFile file, GridUserInterface user) throws NamespaceException {
        return null;
    }

    public StoRI resolveStoRIbyLocalFile(LocalFile file) throws NamespaceException {
        return null;
    }

    public VirtualFSInterface resolveVFSbyLocalFile(LocalFile file, GridUserInterface user) throws NamespaceException {
        return null;
    }

    public VirtualFSInterface resolveVFSbyLocalFile(LocalFile file) throws NamespaceException {
        return null;
    }

    public StoRI resolveStoRIbyStFN(StFN stfn, GridUserInterface user) throws NamespaceException {
        return null;
    }

    public StoRI resolveStoRIbyStFN(StFN stfn) throws NamespaceException {
        return null;
    }

    public VirtualFSInterface resolveVFSbyStFN(StFN stfn, GridUserInterface user) throws NamespaceException {
        return null;
    }

    public VirtualFSInterface resolveVFSbyStFN(StFN stfn) throws NamespaceException {
        return null;
    }

    public StoRI resolveStoRIbyPFN(PFN pfn, GridUserInterface user) throws NamespaceException {
        VirtualFSInterface vfs = resolveVFSbyPFN(pfn, user);
        String vfsRoot = vfs.getRootPath();
        String relativePath = NamespaceUtil.extractRelativePath(vfsRoot, pfn.getValue());
        StoRI stori = vfs.createFile(relativePath);
        return stori;
    }

    public StoRI resolveStoRIbyPFN(PFN pfn) throws NamespaceException {
        /**
         * @todo Check the approachable rules
         */
        VirtualFSInterface vfs = resolveVFSbyPFN(pfn);
        String vfsRoot = vfs.getRootPath();
        String relativePath = NamespaceUtil.extractRelativePath(vfsRoot, pfn.getValue());
        StoRI stori = vfs.createFile(relativePath);
        return stori;
    }

    public VirtualFSInterface resolveVFSbyPFN(PFN pfn, GridUserInterface user) throws NamespaceException {
        /**
         * @todo insert checking the approachable rules
         */
        return resolveVFSbyPFN(pfn);
    }

    /**
     * method used by GetSpaceMetaData Executor to retrieve the VFS and Quota Parameters.
     *
     * @param pfn PFN
     * @return VirtualFSInterface
     * @throws NamespaceException
     */
    public VirtualFSInterface resolveVFSbyPFN(PFN pfn) throws NamespaceException {
        return getWinnerVFS(pfn.getValue());
    }

    public StoRI resolveStoRIbyTURL(TTURL turl, GridUserInterface user) throws NamespaceException {
        PFN pfn = turl.tfn().pfn();
        return resolveStoRIbyPFN(pfn, user);
    }

    public StoRI resolveStoRIbyTURL(TTURL turl) throws NamespaceException {
        PFN pfn = turl.tfn().pfn();
        return resolveStoRIbyPFN(pfn);
    }

    public VirtualFSInterface resolveVFSbyTURL(TTURL turl, GridUserInterface user) throws NamespaceException {
        return null;
    }

    public VirtualFSInterface resolveVFSbyTURL(TTURL turl) throws NamespaceException {
        return null;
    }

    public StoRI getDefaultSpaceFileForUser(GridUserInterface user) throws NamespaceException {
        return null;
    }

    public Space retrieveSpaceByToken(TSizeInBytes totSize, TSpaceToken token) {
        return null;
    }

    /***********************************************
     *              UTILITY METHODS
     **********************************************/

    /**
     * The winner rule is selected from the rule with the minimum distance
     * between the StFN-Path and the StFN-Root within the rule specification.
     *
     * @param stfnPath String
     * @return MappingRule
     */
    private MappingRule getWinnerRuleWithoutApproachableRule(String stfnPath) {
        //log.debug("[getWinnerRule] StFN Path string = "+stfnPath);
        Vector rules = new Vector(parser.getMappingRules().values());
        MappingRule winnerRule = null;
        String stfnRule;
        MappingRule rule;
        int distance = Integer.MAX_VALUE;
        for (int i = 0; i < rules.size(); i++) {
            rule = (MappingRule) rules.elementAt(i);
            stfnRule = rule.getStFNRoot();
            int d = NamespaceUtil.computeDistanceFromPath(stfnRule, stfnPath);
            //log.debug("[getWinnerRule] Evaluating with sftnRule = "+stfnRule+" | Distance = "+d);
            if (d < distance) {
                //Check if the rule is compatible
                //  log.debug("[getWinnerRule] Updating the winner Rule with "+rule);
                boolean enclosed = NamespaceUtil.isEnclosed(stfnRule, stfnPath);
                if (enclosed) { //Found a compatible Mapping rule
                    distance = d;
                    winnerRule = rule;
                }
            }
        }
        return winnerRule;
    }

    /**
     * The winner rule is selected from the rule with the minimum distance
     * between the StFN-Path and the StFN-Root within the rule specification.
     *
     * @param stfnPath String
     * @return MappingRule
     */
    public MappingRule getWinnerRule(String stfnPath, HashSet vfsNameApproachable) {
        //log.debug("[getWinnerRule] StFN Path string = "+stfnPath);
        Vector rules = new Vector(parser.getMappingRules().values());
        MappingRule winnerRule = null;
        String stfnRule;
        MappingRule rule;
        int distance = Integer.MAX_VALUE;
        for (int i = 0; i < rules.size(); i++) {
            rule = (MappingRule) rules.elementAt(i);

            //Check if the selected Rule holds a VFS belonging to the set of VFS
            //approachable by the requestor
            String mappedVFS = rule.getMappedFS();
            log.debug("### Rules : '" + mappedVFS + "'  Lenght:" + mappedVFS.length());
            log.debug("VFS Approachable set : " + vfsNameApproachable);
            log.debug("Contained? = " + vfsNameApproachable.contains(mappedVFS));
            if (vfsNameApproachable.contains(mappedVFS)) {
                //VFS compatibile
                stfnRule = rule.getStFNRoot();
                int d = NamespaceUtil.computeDistanceFromPath(stfnRule, stfnPath);
                //log.debug("[getWinnerRule] Evaluating with sftnRule = "+stfnRule+" | Distance = "+d);
                if (d < distance) {
                    //Check if the rule is compatible
                    //  log.debug("[getWinnerRule] Updating the winner Rule with "+rule);
                    boolean enclosed = NamespaceUtil.isEnclosed(stfnRule, stfnPath);
                    if (enclosed) { //Found a compatible Mapping rule
                        distance = d;
                        winnerRule = rule;
                    }
                }
            } else {
                // VFS incompatible
            }
        }
        return winnerRule;
    }

    @SuppressWarnings("unchecked")
    public VirtualFSInterface getWinnerVFS(String absolutePath) throws NamespaceException {
        VirtualFSInterface vfsWinner = null;
        String path = absolutePath;
        Hashtable<String, VirtualFSInterface> table = (Hashtable<String, VirtualFSInterface>) parser.getMapVFS_Root();
        int distance = Integer.MAX_VALUE;
        Enumeration scan = table.keys();
        String vfs_root = null;
        String vfs_root_winner = null;
        boolean found = false;
        String vfsNameWinner = null;
        while (scan.hasMoreElements()) {
            vfs_root = (String) scan.nextElement();
            int d = NamespaceUtil.computeDistanceFromPath(vfs_root, path);
            log.debug("Pondering VFS Root :'" + vfs_root + "' against '" + path + "' DISTANCE = " + d);
            if (d < distance) {
                boolean enclosed = NamespaceUtil.isEnclosed(vfs_root, absolutePath);
                if (enclosed) { //Found a compatible Mapping rule
                    distance = d;
                    vfsWinner = table.get(vfs_root);
                    try {
                        vfsNameWinner = vfsWinner.getAliasName();
                        vfs_root_winner = vfs_root;
                    } catch (NamespaceException ex) {
                        log.error("Unable to retrieve VFS name.");
                    }
                    log.debug("Partial winner is " + vfs_root_winner + " (VFS :'" + vfsNameWinner + "'");
                    found = true;
                }
            }
        }
        if (found) {
            log.debug("VFS winner is " + vfs_root_winner + " (VFS :'" + vfsNameWinner + "'");
        } else {
            log.error("Unable to found a VFS compatible with path :'" + absolutePath + "'");
            throw new NamespaceException("Unable to found a VFS compatible with path :'" + absolutePath + "'");
        }
        return vfsWinner;
    }

    /*****************************************
     * Methods used for manage SPACE
     *****************************************/

    public String makeSpaceFileURI(GridUserInterface user) throws NamespaceException {
        String result = null;
        TreeSet<ApproachableRule> appRules = new TreeSet<ApproachableRule>(getApproachableRules(user));
        log.debug("Compatible Approachable rules : " + appRules);
        if(appRules.isEmpty())
        {
            if(user instanceof VomsGridUser)
            {
                log.error("No approachable rules found for user with DN='" + user.getDn() + "' and VO = '"
                        + ((VomsGridUser) user).getVO() + "'");
                throw new NamespaceException("No approachable rules found for user with DN='" + user.getDn()
                        + "' and VO = '" + ((VomsGridUser) user).getVO() + "'");
            }
            else
            {
                log.error("No approachable rules found for user with DN='" + user.getDn()
                        + "' User certificate has not VOMS extension");
                throw new NamespaceException("No approachable rules found for user with DN='" + user.getDn()
                        + "' User certificate has not VOMS extension");
            }
        }
        ApproachableRule firstAppRule = appRules.first();
        log.debug("Default APP_RULE is the first (in respsect of name): " + firstAppRule);
        //Retrieve the Relative Path for Space Files
        String spacePath = getRelativePathForSpaceFile(firstAppRule);

        //Retrieve default VFS for the first Approachable Rule compatible for the user.
        VirtualFSInterface vfs = getApproachableDefaultVFS(firstAppRule);
        log.debug("Default VFS for Space Files : " + vfs);

        //Build the Space file path
        String rootPath = vfs.getRootPath();
        String spaceFileName = makeSpaceFileNameForUser(user);
        result = rootPath + spacePath + NamingConst.SEPARATOR + spaceFileName;
        return result;
    }

    public String getRelativePathForSpaceFile(ApproachableRule rule) {
        String result = rule.getSpaceRelativePath();
        if (result == null) {
            result = NamingConst.ROOT_PATH;
        }
        return result;
    }

    public String makeSpaceFileNameForUser(GridUserInterface user) {
        /**
         * @todo Instead of Local User name, extract from DN the NAME_SURNAME
         */
        String userName = null;
        try {
            userName = user.getLocalUser().getLocalUserName();
        } catch (CannotMapUserException ex) {
            log.error("Cannot map user.");
        }
        if (userName == null) {
            userName = "unknown";
        }
        GUID guid = new GUID();
        return userName + "_" + guid + ".space";
    }

    /**
     *
     * @param user GridUserInterface
     * @return SortedSet
     */
    public SortedSet<ApproachableRule> getApproachableRules(GridUserInterface user) {
        TreeSet<ApproachableRule> appRules = null;
        Map rules = parser.getApproachableRules();
        Hashtable appRulesUnorderd = null;
        if (rules != null) {
            appRulesUnorderd = new Hashtable(parser.getApproachableRules());
        }
        appRules = new TreeSet();

        //Purging incompatible rules from the results
        if (appRulesUnorderd != null) {
            ApproachableRule appRule = null;
            // List the entries
            for (Iterator it = appRulesUnorderd.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                appRule = (ApproachableRule) appRulesUnorderd.get(key);
                if (matchSubject(appRule, user)) {
                    //Insert into the result (that is an ordered set)
                    appRules.add(appRule);
                }
            }
        }
        return appRules;
    }

    /**
     *
     * @param appRule ApproachableRule
     * @return VirtualFSInterface
     */
    public VirtualFSInterface getApproachableDefaultVFS(ApproachableRule appRule) {
        VirtualFSInterface defaultVFS = null;
        String defaultVFSName = null;

        //Retrieve VFS names list
        List listVFSnames = appRule.getApproachableVFS();
        if (listVFSnames != null) {
            Vector<String> vfsNames = new Vector<String>(listVFSnames);
            log.debug(" VFS NAMES = " + vfsNames);
            //Looking for the default element, signed with a '*' char at the end
            String vfsName = null;
            if (vfsNames.size() > 0) { //Various VFS names exists. The default is '*' tagged or the first.
                boolean found = false;
                for (Object element : vfsNames) {
                    vfsName = (String) element;
                    if (vfsName.endsWith("*")) {
                        found = true;
                        vfsName = vfsName.substring(0, vfsName.length() - 1);
                        break;
                    }
                }
                if (!found) {
                    defaultVFSName = vfsNames.firstElement();
                } else {
                    defaultVFSName = vfsName;
                }
            }
        }
        log.debug(" Default VFS detected : '" + defaultVFSName + "'");
        defaultVFS = parser.getVFS(defaultVFSName);
        log.debug(" VFS Description " + defaultVFS);
        return defaultVFS;
    }

    private static boolean matchSubject(ApproachableRule approachableRule, GridUserInterface user) {
        boolean result = true;
        result = approachableRule.match(user);
        return result;
    }

    /**
     * Retrieve the list of VFS name approachable by the Grid User gUser.
     *
     * @param gUser GridUserInterface
     * @return Set
     */
    public Set getListOfVFSName(GridUserInterface gUser) {
        Hashtable apprules = new Hashtable(parser.getApproachableRules());
        Enumeration enumer = apprules.elements();
        ApproachableRule appRule;

        HashSet approachVFSNames = new HashSet();
        for (; enumer.hasMoreElements();) {
            appRule = (ApproachableRule) enumer.nextElement();
            if (appRule.match(gUser)) {
                approachVFSNames.addAll(appRule.getApproachableVFS());
            }
        }
        return approachVFSNames;
    }

    /******************************************
     *           VERSION 1.4                  *
     *******************************************/
    /**
     *
     * @param spaceToken TSpaceToken
     * @return VirtualFSInterface
     * @throws NamespaceException
     */
    public VirtualFSInterface resolveVFSbySpaceToken(TSpaceToken spaceToken) throws NamespaceException {
        /** @todo IMPLEMENT */
        return null;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.namespace.NamespaceInterface#getFittingRoots(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    //@Override
    public boolean isStfnFittingSomewhere(String surlString, GridUserInterface user) throws NamespaceException {
        boolean result = false;

        // Array of all stfnRoots
        ArrayList<String> stfnRoots = new ArrayList<String>();
        // List of VFS approachable
        HashSet<String> listVFS = (HashSet<String>) getListOfVFSName(user);
        // List of Mapping Rule
        Hashtable<String, MappingRule> rules = new Hashtable<String, MappingRule>(parser.getMappingRules());

        // Retrieve the list of stfnRoot approachable
        String stfnRoot;
        for (Map.Entry<String, MappingRule> rule : rules.entrySet()) {
            String mappedFS = rule.getValue().getMappedFS();
            if (listVFS.contains(mappedFS)) { // retrieve stfnRoot
                stfnRoot = rule.getValue().getStFNRoot();
                stfnRoots.add(stfnRoot);
            }
        }
        log.debug("FITTING: List of StFNRoots approachables = " + stfnRoots);

        //Build SURL and retrieve the StFN part.
        String stfn = SURL.makeSURLfromString(surlString).getStFN();

        // Path elements of stfn
        ArrayList<String> stfnArray = (ArrayList<String>) NamespaceUtil.getPathElement(stfn);

        for (Object element : stfnRoots) {
            stfnRoot = (String) element;
            log.debug("FITTING: considering StFNRoot = " + stfnRoot + " against StFN = " + stfn);
            ArrayList<String> stfnRootArray = (ArrayList<String>) NamespaceUtil.getPathElement(stfnRoot);
            stfnRootArray.retainAll(stfnArray);
            if (!(stfnRootArray.isEmpty())) {
                result = true;
                log.debug("FIT!");
                break;
            }
        }
        return result;
    }

}
