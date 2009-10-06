/**
 * 
 */
package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;

import org.slf4j.Logger;

/**
 * @author zappi
 *
 */
public class PathACE {
    
    private final Logger log = AuthzDirector.getLogger();
    
    public static final String ALL_GROUPS = "*";
    public static final String FIELD_SEP = "\\s"; // * White space character **/

    public static final PathACE PERMIT_ALL = new PathACE(ALL_GROUPS, StFN.makeEmpty(), PathAccessMask.DEFAULT);
    public static final String COMMENT = "#";
    
    private String localGroupName;
    private StFN storageFileName;
    private PathAccessMask pathAccessMask;


    // =========== CONSTRUCTORs ============
    
    public PathACE(String localGroup, StFN stfn, PathAccessMask accessMask) {
        localGroupName = localGroup;
        storageFileName = stfn;
        pathAccessMask = accessMask;
    }

    public PathACE() {
        localGroupName = null;
        storageFileName = StFN.makeEmpty();
        pathAccessMask = PathAccessMask.DEFAULT;
    }

    
    /**
     * @param pathACEString
     * @return
     * @throws AuthzException
     */
    public static PathACE buildFromString(String pathACEString) throws AuthzException {
        PathACE result = new PathACE();
        String[] fields = pathACEString.split(FIELD_SEP, -1);
        if (fields.length != 3) {
            throw new AuthzException("Error while parsing the Path ACE '" + pathACEString + "'");
        } else {
            // Setting the Local Group Name
            result.setLocalGroupName(fields[0]);

            // Setting the StFN
            try {
                StFN stfn = StFN.make(fields[1]);
                result.setStorageFileName(stfn);
            } catch (InvalidStFNAttributeException e) {
                throw new AuthzException("Error while parsing the StFN '" + fields[1] + "' in Path ACE ");
            }

            // Setting the Permission Mask
            PathAccessMask pAccessMask = new PathAccessMask();
            for (int i = 0; i < fields[2].length(); i++) {
                PathOperation pathOper = PathOperation.getSpaceOperation(fields[2].charAt(i));
                pAccessMask.addPathOperation(pathOper);
            }
            result.setPathAccessMask(pAccessMask);
        }
        return result;
    }
    
    
    public void setLocalGroupName(String localGroup) {
        localGroupName = localGroup;
    }

    public void setStorageFileName(StFN stfn) {
        storageFileName = stfn;
    }

    public void setPathAccessMask(PathAccessMask accessMask) {
        pathAccessMask = accessMask;
    }

    public String getLocalGroupName() {
        return localGroupName;
    }

    public StFN getStorageFileName() {
        return storageFileName;
    }

    public PathAccessMask getPathAccessMask() {
        return pathAccessMask;
    }
    

    /**
     * ## BUSINESS Methods
     */
    
    public boolean subjectMatch(String subjectGroup) {
        boolean result = false;
        if (localGroupName != null) {
            if (localGroupName.equals(PathACE.ALL_GROUPS)) {
                result = true;
                // WARNING. Here we don't check if the subjectGroup is a valid group Name.
            } else {
                if (localGroupName.equals(subjectGroup)) {
                    result = true;
                    log.debug("ACE (" + toString() + ") matches with subject '" + subjectGroup + "'");
                }
            }
        }
        return result;
    }

    /**
     * 
     */
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof PathACE) {
            PathACE pathOther = (PathACE) other;
            if (pathOther.getLocalGroupName() == localGroupName) {
                if (pathOther.getStorageFileName().equals(storageFileName)) {
                    if (pathOther.getPathAccessMask().equals(pathAccessMask)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
    
    public String toString() {
        String result = "";
        if (localGroupName == null) {
            result += "NULL";
        } else {
            result += localGroupName;
        }
        result += " ";
        result += storageFileName;
        result += " ";
        result += pathAccessMask;
        return result;
    }
    
}
