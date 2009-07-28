/**
 * 
 */
package it.grid.storm.authz.sa.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zappi
 *
 */
public class SpaceAccessMask {
    
    private List<SpaceOperation> spAccessMask; 

    public SpaceAccessMask() {
        spAccessMask = new ArrayList<SpaceOperation>();
    }

    public void addSpaceOperation(SpaceOperation spOp) {
        spAccessMask.add(spOp);
    }

    public boolean containsSpaceOperation(SpaceOperation spOp) {
        return spAccessMask.contains(spOp);
    }

    public List<SpaceOperation> getSpaceOperations() {
        return spAccessMask;
    }
    
    public String toString() {
        String spacePermissionStr = "";
        for (SpaceOperation spOp : SpaceOperation.values()) {
            if (spAccessMask.contains(spOp)) {
                spacePermissionStr += spOp.getSpaceOperationValue();
            } else {
                spacePermissionStr += "-";
            }
        }
        return spacePermissionStr;
    }
    
}
