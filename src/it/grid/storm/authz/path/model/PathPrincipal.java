/**
 * 
 */
package it.grid.storm.authz.path.model;

/**
 * @author zappi
 *
 */
public class PathPrincipal {

    public static String prefix = "@";
    
    private String localGroupName;
    private boolean principalCategory = false;

    public PathPrincipal(String principal) {
        if (principal.startsWith(prefix)) {
            principalCategory = true;
        }
        localGroupName = principal;
    }

    
    public boolean isLocalGroup() {
        return (!(principalCategory));
    }

    public String getLocalGroupName() {
        return localGroupName;
    }


    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PathPrincipal) {
            PathPrincipal op = (PathPrincipal) o;
            if (op.isLocalGroup() && (isLocalGroup())) {
                result = (op.getLocalGroupName().equals(getLocalGroupName()));
            } else {
                result = false;
            }
        }
        return result;
    }
    
}
