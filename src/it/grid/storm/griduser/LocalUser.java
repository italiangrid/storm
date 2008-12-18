/*
 * LocalUser
 *
 * Copyright (c) 2005,2006 Riccardo Murri <riccardo.murri@ictp.it>
 *
 * You may copy, distribute and modify this file under the terms
 * listed in the fikle LICENSE.txt
 *
 * $Id: LocalUser.java,v 1.9 2006/06/27 11:59:07 ecorso Exp $
 *
 */
package it.grid.storm.griduser;


/**
 * Encapsulates a POSIX account numeric data: the UID, the primary GID
 * and the supplementary GIDs (if any).  This object is immutable, to
 * all extents.
 *
 * <p> The <code>uid_t</code> and <code>gid_t</code> types are
 * presently unsigned 32-bit integers on Linux, but they are wrapped
 * in Java as <code>int</code>, as the special value <code>-1</code>
 * used by <code>setuid()</code> is of no concern here...
 *
 * <p>XXX: is this class a misnomer?
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>, EGRID Project, ICTP
 * @version $Revision: 1.9 $
 * @see it.grid.storm.griduser.swig.LocalUserInfo;
 */
public class LocalUser {
	private int   __uid;  /* FIXME: uid_t on Linux is unsigned 32-bit */
	private int[] __gids;


	// --- constructors --- //

    /**
     * Constructor taking UID, and a list of GIDs and the size of this
     * list.  The size of the list needs to be passed as an explicit
     * parameter, to cater for naive arrays created by the SWIG
     * interface wrapper, which have no <code>.length</code>
     * attribute.  The list of GIDs is copied into a proper Java array.
	 *
	 * @todo XXX: use objects from localuser_info instead of copying;
	 * problems with "fake" Java arrays should be gone now... (added
	 * 2006-03-15)
     */
    public LocalUser(int uid, int[] gids, long ngids) {
		__uid = uid;

		__gids = new int[(int)ngids];

		for (int i = 0; i<ngids; i++)
			__gids[i] = gids[i];
    }

    /**
     * Constructor taking UID, GID and list of supplementary GIDs.
     */
    public LocalUser(int uid, int gid, int[] supplementaryGids) {
		__uid = uid;

		__gids = new int[1 + supplementaryGids.length];
		__gids[0] = gid;

		for (int i = 1; i<=supplementaryGids.length; i++)
			__gids[i] = supplementaryGids[i-1];
    }

    /**
     * Constructor taking UID and GID.  The list of supplementary GIDs
     * is initialized to an empty array.
     */
    public LocalUser(int uid, int gid) {
		this(uid, gid, new int[0]);
    }

    /**
     * Constructor that requires the String representation of a LocalUser.
     * The String representation consists of at least two comma separated integers,
     * the first one being the uid, followed by all gids. For example:
     * "501,501"
     * "501,123,332,1223"
     *
     * In case the supplied String is null or invalid, by default a LocalUser with
     * uid=501 and one gid=501 is created.
     */
    public LocalUser(String uidgids) {
        this.__uid=501;
        this.__gids = new int[1]; __gids[0] = 501;
        if (uidgids!=null) {
            String[] aux = uidgids.split(",");
            //try parsing the chunks provided there are at least two!
            if (aux.length>=2) {
                try {
                    int auxuid=Integer.parseInt(aux[0]);
                    int[] auxgid = new int[aux.length-1];
                    for (int i=0; i<aux.length-1; i++) auxgid[i]=Integer.parseInt(aux[i+1]);
                    this.__uid = auxuid;
                    this.__gids = auxgid;
                } catch (NumberFormatException e) {
                    //Do nothing! Defaults are ok!
                }
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(__uid);
        for (int i=0; i<__gids.length; i++) {
            sb.append(",");
            sb.append(__gids[i]);
        }
        return sb.toString();
    }


    /**
     * Return the LocalUser Name String on wich the GridUser is mapped.
     * A string format is needed by current version of native library to enforce ACL.
     */

    public String getLocalUserName() {
      return Integer.toString(getUid());
    }



    // --- public accessor methods --- //

    public int getUid() {
		return __uid;
    }

    /**
     * Return an array holding the GIDs; the primary GID is at index 0.
     * If no supplementary GIDs were provided at construction time, then
     * the array will have length 1, and contain only the primary GID.
     *
     * @return  int[] holding all GIDs, with the primary one at index 0.
     */
    public int[] getGids() {
		return __gids;
    }

    /**
     * Return the primary GID.
	 *
     * @return  the primary GID of this POSIX account.
     */
    public int getPrimaryGid() {
		return __gids[0];
    }
}
