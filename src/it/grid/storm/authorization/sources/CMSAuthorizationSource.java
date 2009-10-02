/**
 * 
 */
package it.grid.storm.authorization.sources;

import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;

/**
 * @author zappi
 *
 */
public class CMSAuthorizationSource extends AbstractAuthorizationSource {

    /**
     * Check if user can be allowed read access to the specified file.
     */
    public AuthorizationDecision canReadFile(GridUserInterface gridUser, StoRI file) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can be allowed write access to the specified file. This request is considered meaningful only for
     * already-existing files, however, it is up to the actual implementation to check if the file already exists.
     */
    public AuthorizationDecision canWriteFile(GridUserInterface gridUser, StoRI existingFile) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can create the named file. All levels of directories above the file to be created are assumed to
     * exist; however, implementations of <code>canCreateNewFile</code> may possibly skip the existence check - it is up
     * to the caller to ensure that the file does not already exist, or fail accordingly.
     * <p>
     * Involved in: <code>SrmCopy</code>, <code>srmPrepareToPut</code>
     */
    public AuthorizationDecision canCreateNewFile(GridUserInterface gridUser, StoRI targetFile) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can list a directory contents. Note that implementations may possibly skip the existence check on
     * the named directory, it is up to the caller to ensure that the file or directory being operated upon actually
     * exists in the StoRM namespace.
     * <p>
     * Involved in: <code>srmLs</code>,
     */
    public AuthorizationDecision canListDirectory(GridUserInterface gridUser, StoRI directory) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can descend the specified path. If <code>path</code> points to a file, then check if the specified
     * path can be descended to the directory containing that file.
     * <p>
     * Note that implementations may possibly skip the existence check on the named entity, it is up to the caller to
     * ensure that the file or directory being operated upon actually exists in the StoRM namespace.
     * <p>
     * Involved in: <code>srmCopy</code>, <code>srmLs</code>, <code>srmPrepareToGet</code>, <code>srmPrepareToPut</code>, <code>srmMv</code>, <code>srmMkdir</code>, <code>srmRm</code>, <code>srmRmdir</code>.
     */
    public AuthorizationDecision canTraverseDirectory(GridUserInterface gridUser, StoRI path) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can rename the specified file or directory. Note that implementations may possibly skip the
     * existence check on the named entity, it is up to the caller to ensure that the file or directory being operated
     * upon actually exists in the StoRM namespace.
     * <p>
     * Involved in: <code>srmMv</code>.
     */
    public AuthorizationDecision canRename(final GridUserInterface gridUser, final StoRI file) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can delete the specified file or directory. Note that implementations may possibly skip the
     * existence check on the named entity, it is up to the caller to ensure that the file or directory being operated
     * upon actually exists in the StoRM namespace.
     * <p>
     * Involved in: <code>srmRm</code>, <code>srmRmdir</code>.
     */
    public AuthorizationDecision canDelete(GridUserInterface gridUser, StoRI file) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }

    /**
     * Check if user can create the specified directory. Note that implementations may possibly skip the existence check
     * on the named entity, it is up to the caller to ensure that the file or directory being operated upon actually
     * exists in the StoRM namespace.
     * <p>
     * Involved in: <code>srmMkdir</code>.
     */
    public AuthorizationDecision canMakeDirectory(GridUserInterface gridUser, StoRI targetDirectory) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        return result;
    }
    
}
