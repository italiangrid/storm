/*
 * LcmapsMapper
 *
 * Copyright (c) 2005,2006 Riccardo Murri <riccardo.murri@ictp.it>
 *
 * You may copy, distribute and modify this file under the terms
 * listed in the fikle LICENSE.txt
 *
 * $Id: LcmapsMapper.java,v 1.6 2007/05/16 09:46:20 lmagnoni Exp $
 *
 */
package it.grid.storm.griduser;


import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.MapperInterface;
import it.grid.storm.griduser.swig.localuser_info;

import org.apache.log4j.Logger;



/** Map Grid credentials to local account credentials. Implements a
 * factory method for constructing a {@link
 * it.grid.storm.griduser.swig.localuser_info} object with the given
 * Grid user credentials; the localuser_info object will actually
 * invoke LCMAPS to do the actual mapping.
 */
public class LcmapsMapper implements MapperInterface {
    /** Factory method, taking user certificate subject DN and an
     * array of VOMS FQANs.  Returns a new LocalUser object holding
     * the local credentials (UID, GIDs) of the POSIX account the
     * given Grid user is mapped to.
     *
     * <p>LCMAPS, as of version 1.2.8 (current, 2006-03-15) is
     * _not_ thread-safe:
     *
     * <pre>
     *   From: Martijn Steenbakkers <martijn@nikhef.nl>
     *   Subject: Re: Is LCMAPS thread-safe?
     *   Date: Wed, 15 Mar 2006 15:17:15 +0100
     *   To: Riccardo Murri <riccardo.murri@ictp.it>
     *   Cc: Gerben Venekamp <venekamp@nikhef.nl>,
     *       Oscar Koeroo <okoeroo@nikhef.nl>
     *
     *   Riccardo Murri wrote:
     *   >one more question on LCMAPS: is LCMAPS 1.2.8 thread-safe?
     *   Unfortunately, no...
     *
     *   >need to serialize calls to
     *   >lcmaps_return_poolindex_without_gsi()?
     *   So, yes.
     *
     *   Cheers,
     *   Martijn
     * </pre>
     *
     * <p> So, we synchronize the calls to localuser_info constructor
     * using a class static lock.
     *
     * @param dn Grid user certificate subject DN
     * @param fqans array of VOMS FQANs
     *
     * @return a new LocalUser object holding the local credentials
     * (UID, GIDs) of the POSIX account the given Grid user is mapped
     * to.
     *
     * @throws {@link it.grid.storm.griduser.CannotMapUserExcpetion} if
     * some error with LCMAPS occurs.
     */
    public LocalUser  map(final String dn, final String[] fqans)
        throws CannotMapUserException
    {
    	log.debug("Asking LCMAPS for mapping : "+dn+"-"+fqans.toString());

        localuser_info lui = null;
        try {
            synchronized(lock) {
                lui = new localuser_info(dn, fqans);
//                log.debug("lcmaps plugin now works3...");
//                log.debug("guid:..."+lui.getUid());
//                log.debug("gids:..."+lui.getGids());
//                log.debug("gids[0]:..."+lui.getGids()[0]);
//                log.debug("Ngids:..."+lui.getNgids());
//                log.debug("Mapper returns");

            }
            // the following code is effectively unreachable,
            // as localuser_info will _never_ return null, but
            // is here to appease "smart" Java compilers that
            // consider it an _error_ to have an unreachable "catch"
            // block.
            //
            // 'localuser_info' will throw a 'CannotMapUserExcpetion'
            // instead, but gLite's swig-1.3.21 lacks the
            // %javaexception declaration, so the SWIG-generated code
            // does not have the correct throw clause.  But an
            // exception _is_ thrown, so the following is never
            // reached, in case of error.
            //
            if(null == lui)
                throw new CannotMapUserException("BUG in LcmapsMapper.java, line 86");
        }
        catch (CannotMapUserException x) {
            String lcmapsLogFile = System.getenv("LCMAPS_LOG_FILE"); // un-deprecated in Java 5

            if (null == lcmapsLogFile)
                lcmapsLogFile = "";
            String errorMessage = "LCMAPS failure: "
                + x.getMessage()
                + " -- see LCMAPS log file "
                + lcmapsLogFile
                + " for details.";
            log.error(errorMessage);
            throw new CannotMapUserException(errorMessage);
        }
        //Syncronization on this added by Luca
        //to create the LocalUser lcmaps must be queried.
        //Without synchrnonization may be this is the reasons for the JVM crash?
        //due to lcmaps non thread safet
//        log.debug("lcmaps plugin now works1...");
//        log.debug("guid:..."+lui.getUid());
//        log.debug("gids:..."+lui.getGids());
//        log.debug("Ngids:..."+lui.getNgids());
//        log.debug("Mapper returns");
        return new LocalUser(lui.getUid(), lui.getGids(), lui.getNgids());

    }

    public LcmapsMapper() {
        // nothing to do;
    }

    /** To synchronize on LCMAPS invocation. */
    private static Object lock = new Object();

    /** To log LCMAPS failures. */
    private static final Logger log = Logger.getLogger(LcmapsMapper.class);
}
