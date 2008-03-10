/*
 * AuthorizationQueryInterface
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: AuthorizationQueryInterface.java,v 1.7 2006/03/21 16:56:35 rmurri Exp $
 *
 */
package it.grid.storm.authorization;

import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.StoRI;


/**
 * The interface for querying the Authorization component.  Defines
 * methods which are actually implemented both in
 * *<code>AuthorizationSource</code> (component) classes, and in the
 * <code>AuthorizationCollector</code> (composite) object.  This
 * interface is meant as a programmers' device to ensure that the
 * component and composite classes actually implement the same
 * interface.
 *
 * <p>Note that methods in the authorization package are not concerned
 * with existence of files and directories, only with users rights to
 * operate on them.  So, a call to delete a non-existing file may
 * legally return a "permit" result - it is up to the caller to check
 * for existence with the filesystem, and eventually return a failure
 * code to the invoking party.
 *
 * <p>All methods described here are meant to return an
 * <code>AuthorizationDecision</code> object.  They should return
 * <code>AuthorizationDecision.Deny</code> (or another
 * <code>AuthorizationDecision</code> object whose
 * <code>isDeny()</code> method returns <code>true</code>) if user
 * should be denied operation on to the specified resource; return
 * instead * <code>AuthorizationDecision.Permit</code> (or another
 * <code>AuthorizationDecision</code> object whose
 * <code>isPermit()</code> method returns <code>true</code>) if user
 * can be allowed operation on the specified resource.
 *
 * @see     AuthorizationDecision
 * @see     AuthorizationCollector
 * @see     it.grid.storm.authorization.sources
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */

public interface AuthorizationQueryInterface {
	/**
	 * Check if user should be denied access to <em>any</em> of the
	 * StoRM features in the first place. i.e., the StoRM server
	 * should return some unauthorized access error code for
	 * <em>any</em> request.
	 */
	public AuthorizationDecision canUseStormAtAll(final VomsGridUser gridUser);

	/**
	 * Check if user can be allowed read access to the specified file.
	 */
	public AuthorizationDecision 
		canReadFile(final VomsGridUser gridUser, final StoRI file);

	/**
	 * Check if user can be allowed write access to the specified
	 * file.  This request is considered meaningful only for
	 * already-existing files, however, it is up to the actual
	 * implementation to check if the file already exists.
	 */
	public AuthorizationDecision 
		canWriteFile(final VomsGridUser gridUser, final StoRI existingFile);

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
		canCreateNewFile(final VomsGridUser gridUser, final StoRI targetFile);

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
		canChangeAcl(final VomsGridUser gridUser, 
					 final StoRI fileOrDirectory);

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
		canGiveaway(final VomsGridUser gridUser, final StoRI fileOrDirectory);

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
		canListDirectory(final VomsGridUser gridUser, final StoRI directory);

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
		canTraverseDirectory(final VomsGridUser gridUser, 
							 final StoRI path);

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
		canRename(final VomsGridUser gridUser, final StoRI file);

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
		canDelete(final VomsGridUser gridUser, final StoRI file);

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
		canMakeDirectory(final VomsGridUser gridUser, 
						 final StoRI targetDirectory);
}
