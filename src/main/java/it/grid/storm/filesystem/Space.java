/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * @file Space.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * 
 *         Definition of the Space interface
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it> for the EGRID/INFN
 * joint project StoRM.
 * 
 * You may copy, modify and distribute this file under the same terms as StoRM
 * itself.
 */

package it.grid.storm.filesystem;

import java.io.IOException;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.common.types.SizeUnit;

/**
 * Provides an interface for SRM-style advance space reservation, and adapts it
 * to filesystem-level actual space reservation methods.
 * <em>This is a rough draft</em>, and should be furhter discussed.
 * 
 * <p>
 * <strong>This interface is a draft!</strong> Due to the unsettled state of the
 * SRM spec regarding to reserved space semantics, and the differences between
 * SRM space reservation and filesystem-level preallocation, this interface will
 * probably change in the near future.
 * 
 * At present this interface represents functionality that is only a subset of
 * the full SRM 2.1.1 specification. This is so because StoRM was originally
 * designed to leverage GPFS native space preallocation, which satisfies the SRM
 * specifications only in restricted use cases.
 * 
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @author EGRID - ICTP Trieste (further development and modifications)
 * @version $Revision: 1.9 $
 */
public class Space {

	private SpaceSystem ss = null; // spacesystem
	private TSizeInBytes guaranteedSize = TSizeInBytes.makeEmpty(); // guaranteed
																																	// size
	private TSizeInBytes totalSize = TSizeInBytes.makeEmpty(); // total reserved
																															// size
	private TSpaceToken spaceToken = TSpaceToken.makeEmpty(); // TSpaceToken
																														// associated with
																														// Space request
	private LocalFile spaceFile = null; // space file initialized by constructor!

	/**
	 * Constructor that requires the guaranteedSize, the totalSize, the spaceFile
	 * and the SpaceSystem; if any is null, or totalSize is Empty, or
	 * guaranteedSize is greater than totalSize, then an
	 * InvalidSpaceAttributesException is thrown.
	 */
	public Space(TSizeInBytes guaranteedSize, TSizeInBytes totalSize,
		LocalFile spaceFile, SpaceSystem ss) throws InvalidSpaceAttributesException {

		boolean ok1 = (guaranteedSize != null) && (totalSize != null)
			&& (spaceFile != null) && (ss != null) && (!totalSize.isEmpty());
		boolean ok2 = guaranteedSize.isEmpty();
		boolean ok3 = (!guaranteedSize.isEmpty())
			&& (guaranteedSize.getSizeIn(SizeUnit.BYTES) <= totalSize
				.getSizeIn(SizeUnit.BYTES));
		if (ok1 && (ok2 || ok3)) {
			this.guaranteedSize = guaranteedSize;
			this.totalSize = totalSize;
			this.spaceFile = spaceFile;
			this.ss = ss;
		} else
			throw new InvalidSpaceAttributesException(guaranteedSize, totalSize,
				spaceFile, ss);
	}

	/**
	 * Method used to set the TSpaceToken of This Space. If it is null, nothing
	 * gets set!
	 */
	public void setSpaceToken(TSpaceToken spaceToken) {

		if (spaceToken != null)
			this.spaceToken = spaceToken;
	}

	/**
	 * Method that physically carries out the actual space reservation. In case of
	 * any problem, a ReservationException is thrown
	 */
	public void allot() throws ReservationException {

		ss.reserveSpace(spaceFile.getPath(), guaranteedSize.value());
	}

	/**
	 * Method that just creates the space file but not allocates memory. In case
	 * of any problem, a ReservationException is thrown
	 */
	public void fakeAllot() throws ReservationException {

		java.io.File localFile = new java.io.File(spaceFile.getPath());
		try {
			localFile.createNewFile();
		} catch (IOException e) {
			throw new ReservationException(
				"IO exception while creating local File named : " + spaceFile.getPath()
					+ " : " + e.getMessage());
		} catch (SecurityException e) {
			throw new ReservationException(
				"Security exception while creating local File named : "
					+ spaceFile.getPath() + " : " + e.getMessage());
		}
	}

	/**
	 * Method that just removes the space file but not deallocates memory. In case
	 * of any problem, a ReservationException is thrown
	 */
	public void fakeRelease() throws ReservationException {

		java.io.File localFile = new java.io.File(spaceFile.getPath());
		try {
			localFile.delete();
		} catch (SecurityException e) {
			throw new ReservationException(
				"Security exception while deleteing local File named : "
					+ spaceFile.getPath() + " : " + e.getMessage());
		}
	}

	/**
	 * Method that gives back unused blocks to the filesystems' general available
	 * space. It returns a long representing the size (in bytes) of space that was
	 * freed.
	 * 
	 * If anything goes wrong, a ReservationException is thrown.
	 */
	public long compact() throws ReservationException {

		return ss.compactSpace(spaceFile.getPath());
	}

	/**
	 * Method that returns the TSpaceToken associated to This reserved space: if
	 * none is associated, then an Empty one is returned.
	 */
	public TSpaceToken getSpaceToken() {

		return spaceToken;
	}

	/**
	 * Method that returns the corresponding SpaceFile.
	 * 
	 * BEWARE! All space reservation implementation are assumed to create a
	 * physical file that takes up room in the underlying filesystem! It is this
	 * mock file that gets returned!
	 */
	public LocalFile getSpaceFile() {

		return spaceFile;
	}

	/**
	 * Method that returns a TSizeInBytes representing the size in bytes of
	 * guaranteed reserved space. The guaranteed reserved space can only be used
	 * by the Grid entity (user, VO, ...) that reserved it, and cannot be used up
	 * by entities outside StoRM.
	 */
	public TSizeInBytes getGuaranteedSpaceSize() {

		return guaranteedSize;
	}

	/**
	 * Method that returns a TSizeInBytes representing the total size in bytes of
	 * reserved space: best-effort + guaranteed. The "best-effort" reserved space
	 * may be used by entities outside StoRM so it may actually be no longer
	 * available at the time its use is requested.
	 */
	public TSizeInBytes getTotalReservedSize() {

		return totalSize;
	}

	/**
	 * Method that returns a long representing the size of space that has been
	 * reserved, but that so far has not been used. At the moment this method is
	 * not implemented and always returns 0.
	 */
	public long getUnusedSpace() {

		return 0;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("Space: guaranteedSize=");
		sb.append(guaranteedSize.toString());
		sb.append("; totalSize=");
		sb.append(totalSize.toString());
		sb.append("; spaceFile=");
		sb.append(spaceFile.toString());
		sb.append("; SpaceSystem=");
		sb.append(ss.toString());
		sb.append("; TSpaceToken=");
		sb.append(spaceToken.toString());
		return sb.toString();
	}

	/**
	 * Return the remaining unused space, that is, the size of the space that is
	 * still available to allot for individual files with the
	 * {@link #allotForFile} and {@link #addFile} methods.
	 * 
	 * @return remaining size (in bytes) to be used in this space.
	 */
	// public long getAvailableSize();

	/**
	 * Use a part of the reserved space for a file. Restrictions may be put by the
	 * underlying implementation on the filename, e.g., the file must be created
	 * under a certain directory.
	 * 
	 * @param file
	 *          a {@link File} to assign part of the reserved space to.
	 * @param size
	 *          size (in bytes) of the part of reserved space to use for
	 *          <i>file</i>.
	 * 
	 * @return size (in bytes) of the reserved space actually alloted for the
	 *         file.
	 */
	// public long allotForFile(File file, long size);

	/**
	 * Tell system that <i>file</i> will grow at this reserved space expenses.
	 * Restrictions may be put by the underlying implementation on the filename,
	 * e.g., the file must be created under a certain directory, or, worse, this
	 * call mey not be supported on all implementations.
	 * 
	 * @param file
	 *          a {@link File} to assign part of the reserved space to.
	 * 
	 * @return <code>false</code> if this feature is not supported by the
	 *         underlying implementation.
	 */
	// public boolean addFile(File file);

	/**
	 * Reserve <i>size</i> bytes on filesystem; return actual size (in bytes) of
	 * reserved space. If any space is actually reserved by this function, then it
	 * is considered a <em>guaranteed</em> reservation, that is, only user
	 * <i>u</i> can operate on it and eventually dispose the space.
	 * 
	 * @todo <em>FIXME:</em> This should ideally be a constructor, but we cannot
	 *       specify constructors in interfaces...
	 * 
	 * @param u
	 *          Grid user to reserve the space to.
	 * @param guaranteedSize
	 *          size (in bytes) of space to reserve.
	 * @param bestEffortSize
	 *          size (in bytes) of space to reserve.
	 * 
	 * @return size (in bytes) of <em>guaranteed</em> space actually reserved.
	 */
	// public long reserveGuaranteedSpace(VomsGridUser u,
	// long guaranteedSize,
	// long bestEffortSize);

	/**
	 * Release any reserved space, possibly deleting files and directories still
	 * existing within the reserved space area. The parameter
	 * <i>deleteLeftoverFiles</i> controls whether files or directories existing
	 * within the space should be deleted, or an exception should be thrown.
	 * 
	 * @param deleteLeftoverFiles
	 *          if <code>true</code>, then delete any files and directories that
	 *          still exist within the reserved space; if <code>false</code>, then
	 *          throw a <code>SpaceNotEmpty</code> exception if any such files
	 *          exist.
	 * 
	 * @return size (in bytes) of space that was freed and returned to filesystem
	 *         for general usage.
	 * 
	 * @throws SpaceNotEmpty
	 *           if files or directories exist within the space, and the parameter
	 *           <i>deleteLeftoverFiles</i> was set to <code>false</code>.
	 */
	// public long release(boolean deleteLeftoverFiles);
}
