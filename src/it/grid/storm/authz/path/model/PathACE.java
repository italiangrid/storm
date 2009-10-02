/**
 * 
 */
package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;

/**
 * @author zappi
 *
 */
public class PathACE {
    
    public static final String ALL_GROUPS = "*";
    public static final String FIELD_SEP = "\\s"; // * White space character **/

    public static final PathACE PERMIT_ALL = new PathACE(ALL_GROUPS, StFN.makeEmpty(), PathAccessMask.DEFAULT);
    
    
    private String localGroupName;
    private StFN storageFileName;
    private PathAccessMask pathAccessMask;

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
    
}
