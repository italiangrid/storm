/* AuthorizationCollector
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 *
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: AuthorizationCollector.java,v 1.4 2006/03/21 20:42:10 rmurri Exp $
 *
 */
package it.grid.storm.authorization;


import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.AuthorizationQueryInterface;
import it.grid.storm.authorization.DecisionCombiningAlgorithm;
import it.grid.storm.authorization.sources.CompositeAuthorizationSource;
import it.grid.storm.config.Configuration;
//import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.griduser.GridUserInterface;


/**
 * Queries a list of <code>AuthorizationSource</code>s and returns a
 * final decision.
 *
 * <p>This is an interface to a tree of
 * <code>AuthorizationSource</code>s, where each leaf of the tree is a
 * proper authorization source returning an
 * <code>AuthorizationDecisio</code> instance for each query, and each
 * node of the tree is a <code>CompositeAuthorizationSource</code>
 * that will combine decisions based on a
 * <code>DecisionCombiningAlgorithm</code>.  The
 * <code>AuthorizationCollector</code> holds the root node of the
 * tree.
 *
 * <p>Is a Singleton, as there need be only one Collector per StoRM
 * instance.
 *
 * <p>The list of authorization sources to query is taken from the
 * {@link it.grid.storm.config.Configuration} instance.
 *
 * @see AuthorizationDecision
 * @see AuthorizationQueryInterface
 * @see it.grid.storm.authorization.combiners
 * @see it.grid.storm.config.Configuration
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */

public class AuthorizationCollector
	implements AuthorizationQueryInterface {

	/** The only instance of this class. */
	private static final AuthorizationCollector instance
		= new AuthorizationCollector();


	/** The root node of the authorization sources tree. */
	private final AuthorizationQueryInterface rootNode;


	/** Default constructor: reads values for the decision combining
	 * algorithm and the list of sources from the Configuration
	 * object. */
	protected AuthorizationCollector() {
		Configuration configuration = Configuration.getInstance();

		rootNode = new CompositeAuthorizationSource(
							configuration.getAuthorizationCombiningAlgorithm(),
							configuration.getAuthorizationSources()
						);
	}


	/** Return the unique instance of this class. */
	public static AuthorizationCollector getInstance() {
		return instance;
	}


	/**
	 * Check if user should be denied access to <em>any</em> of the
	 * StoRM features in the first place. i.e., the StoRM server
	 * should return some unauthorized access error code for
	 * <em>any</em> request.
	 */
	public AuthorizationDecision canUseStormAtAll(final GridUserInterface gridUser) {
		return rootNode.canUseStormAtAll(gridUser);
	}


	/**
	 * Check if user can be allowed read access to the specified file.
	 */
	public AuthorizationDecision
		canReadFile(final GridUserInterface gridUser, final StoRI file) {
		return rootNode.canReadFile(gridUser, file);
	}


	/**
	 * Check if user can be allowed write access to the specified
	 * file.  This request is considered meaningful only for
	 * already-existing files, however, it is up to the actual
	 * implementation to check if the file already exists.
	 */
	public AuthorizationDecision
		canWriteFile(final GridUserInterface gridUser, final StoRI existingFile) {
		return rootNode.canWriteFile(gridUser, existingFile);
	}


	/**
	 * Check if user can create the named file.  All levels of
	 * directories above the file to be created are assumed to exist;
	 * however, implementations of <code>canCreateNewFile</code> may
	 * possibly skip the existence check - it is up to the caller to
	 * ensure that the file does not already exist, or fail
	 * accordingly.
	 *
	 * <p>Involved in: <code>SrmCopy</code>,
	 * <code>srmPrepareToPut</code>
	 */
	public AuthorizationDecision
		canCreateNewFile(final GridUserInterface gridUser, final StoRI targetFile) {
		return rootNode.canCreateNewFile(gridUser, targetFile);
	}


	/**
	 * Check if user can change the (Grid-level) ACLs on the specified
	 * file or directory.  Note that implementations may possibly skip
	 * the existence check on the named entity, it is up to the caller
	 * to ensure that the file or directory being operated upon
	 * actually exists in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmChangePermissions</code>.
	 */
	public AuthorizationDecision
		canChangeAcl(final GridUserInterface gridUser,
					 final StoRI fileOrDirectory) {
		return rootNode.canChangeAcl(gridUser, fileOrDirectory);
	}


	/**
	 * Check if user can re-assign ownernship of the specified file or
	 * directory to another Grid user or group.  Note that
	 * implementations may possibly skip the existence check on the
	 * named entity, it is up to the caller to ensure that the file or
	 * directory being operated upon actually exists in the StoRM
	 * namespace.
	 *
	 * <p>Involved in: <code>srmReassignToUser</code>.
	 */
	public AuthorizationDecision
		canGiveaway(final GridUserInterface gridUser, final StoRI fileOrDirectory) {
		return rootNode.canGiveaway(gridUser, fileOrDirectory);
	}


	/**
	 * Check if user can list a directory contents.  Note that
	 * implementations may possibly skip the existence check on the
	 * named directory, it is up to the caller to ensure that the file
	 * or directory being operated upon actually exists in the StoRM
	 * namespace.
	 *
	 *
	 * <p>Involved in: <code>srmLs</code>,
	 */
	public AuthorizationDecision
		canListDirectory(final GridUserInterface gridUser, final StoRI directory) {
		return rootNode.canListDirectory(gridUser, directory);
	}


	/**
	 * Check if user can descend the specified path.  If
	 * <code>path</code> points to a file, then check if the specified
	 * path can be descended to the directory containing that file.
	 *
	 * <p>Note that implementations may possibly skip the existence
	 * check on the named entity, it is up to the caller to ensure
	 * that the file or directory being operated upon actually exists
	 * in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmCopy</code>, <code>srmLs</code>,
	 * <code>srmPrepareToGet</code>, <code>srmPrepareToPut</code>,
	 * <code>srmMv</code>, <code>srmMkdir</code>, <code>srmRm</code>,
	 * <code>srmRmdir</code>.
	 */
	public AuthorizationDecision
		canTraverseDirectory(final GridUserInterface gridUser,
							 final StoRI path) {
		return rootNode.canTraverseDirectory(gridUser, path);
	}


	/**
	 * Check if user can rename the specified file or directory.
	 * Note that implementations may possibly skip
	 * the existence check on the named entity, it is up to the caller
	 * to ensure that the file or directory being operated upon
	 * actually exists in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmMv</code>.
	 */
	public AuthorizationDecision
		canRename(final GridUserInterface gridUser, final StoRI file) {
		return rootNode.canRename(gridUser, file);
	}


	/**
	 * Check if user can delete the specified file or directory.
	 * Note that implementations may possibly skip
	 * the existence check on the named entity, it is up to the caller
	 * to ensure that the file or directory being operated upon
	 * actually exists in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmRm</code>, <code>srmRmdir</code>.
	 */
	public AuthorizationDecision
		canDelete(final GridUserInterface gridUser, final StoRI file) {
		return rootNode.canDelete(gridUser, file);
	}


	/**
	 * Check if user can create the specified directory.
	 * Note that implementations may possibly skip
	 * the existence check on the named entity, it is up to the caller
	 * to ensure that the file or directory being operated upon
	 * actually exists in the StoRM namespace.
	 *
	 * <p>Involved in: <code>srmMkdir</code>.
	 */
	public AuthorizationDecision
		canMakeDirectory(final GridUserInterface gridUser,
						 final StoRI targetDirectory) {
		return rootNode.canMakeDirectory(gridUser, targetDirectory);
	}

}
