/**
 * 
 */
package it.grid.storm.authz.path.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zappi
 *
 */
public class PathAccessMask {
    
    private List<PathOperation> pathAccessMask;
    
    private static List<PathOperation> operations = new ArrayList<PathOperation>() {
        {
            add(PathOperation.READ_FILE);
            add(PathOperation.LIST_DIRECTORY);
            add(PathOperation.TRAVERSE_DIRECTORY);
        }
    };

    public static PathAccessMask DEFAULT = new PathAccessMask(operations);
    
    
    
    public PathAccessMask() {
        pathAccessMask = new ArrayList<PathOperation>();
    }

    public PathAccessMask(List<PathOperation> operations) {
        pathAccessMask = operations;
    }
    
    public void addPathOperation(PathOperation pathOp) {
        pathAccessMask.add(pathOp);
    }

    public boolean containsPathOperation(PathOperation pathOp) {
        return pathAccessMask.contains(pathOp);
    }

    public List<PathOperation> getPathOperations() {
        return pathAccessMask;
    }

    public String toString() {
        String pathPermissionStr = "";
        for (PathOperation pathOp : PathOperation.values()) {
            if (pathAccessMask.contains(pathOp)) {
                pathPermissionStr += pathOp.getSpaceOperationValue();
            } else {
                pathPermissionStr += "-";
            }
        }
        return pathPermissionStr;
    }
    
    public int getSize() {
        return pathAccessMask != null ? pathAccessMask.size() : 0;
    }
    
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof PathAccessMask) {
            PathAccessMask pOther = (PathAccessMask)other;
            if (pathAccessMask.size() == pOther.getSize()) {
                // Check all the bit
            }
        }
        return result;
    }
    
}
