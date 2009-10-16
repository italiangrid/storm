package it.grid.storm.catalogs;

import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This catalogue holds all info needed to pin files for JiT ACL tracking, and for keeping track of Volatile files.
 * pinLifetime is the time Jit ACLs will be in place: upon expiry ACLs are removed; fileLifetime is the time Volatile
 * files will remain in the system: upon expiry those files are removed. In particular the srmPrepareToPut analyses the
 * request and if the specified file is set to Volatile, then it calls on the catalogue to add the corresponding entry
 * for the given fileLifetime. If StoRM is configured for JiT, another method is invoked to add an entry to keep track
 * of the ACLs for the desired pinLifetime. For srmPrepareToGet, only if StoRM is configured for JiT ACLs then a method
 * is invoked to add the corresponding entry for the given pinLifetime. Repeatedly putting the same Volatile file, will
 * overwrite existing fileLifetime only if the overwrite option allows file overwriting. If JiT is enabled and it is a
 * new user that is putting again the same file in, a new pinLifetime entry is added; but if it is the same user, the
 * pinLifetime WILL be changed provided the new expiry exceeds the current one! Repeatedly invoking PtG on the same file
 * behaves similarly: different users will have their own pinLifetime record, but the same user WILL change the
 * pinLifetime provided the new expiry exceeds the current one! In case the pinLifetime exceeds the fileLifetime, the
 * fileLifetime is used as ceiling. This may occur when a file is Put and defined Volatile, but with a pinLifetime that
 * is longer than that of the pin. Or if _subsequent_ calls to PtG specify a pinLifetime that lasts longer. To be more
 * precise, the pinLifetime gets recorded as requested, but upon expiry of the volatile entry any associated acl will
 * get removed as well, regardless of the acl expiry. When lifetime expires: volatile files get erased from the system
 * and their entries in the catalogue are removed; tracked ACLs get removed from the files WITHOUT erasing the files,
 * and their entries in the cataogue are removed; finally for Volatile files with ACLs set up on them, the ACLs are
 * removed AND the files are erased, also cleaning up the catalogue. As a last note, the catalogue checks periodically
 * its entries for any expired ones, and then proceeds with purging; this frequency of cleaning is specified in a
 * configuration parameter, and the net effect is that the pinning/volatile may actually last longer (but never less)
 * because the self cleaning mechanism is active only at those predetermined times.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date November 2006
 */
public class VolatileAndJiTCatalog {

    private static final Logger log = LoggerFactory.getLogger(VolatileAndJiTCatalog.class);

    /** only instance of Catalog! */
    private static final VolatileAndJiTCatalog cat = new VolatileAndJiTCatalog();
    /** only instance of DAO object! */
    private static final VolatileAndJiTDAO dao = VolatileAndJiTDAO.getInstance();
    /** Timer object in charge of cleaning periodically the Catalog! */
    private final Timer cleaner = new Timer();
    /** Delay time before starting cleaning thread! Set to 1 minute */
    private final long delay = Configuration.getInstance().getCleaningInitialDelay() * 1000;
    /** Period of execution of cleaning! Set to 1 hour */
    private final long period = Configuration.getInstance().getCleaningTimeInterval() * 1000;
    /** fileLifetime to use if user specified a non-positive value */
    private final long defaultFileLifetime = Configuration.getInstance().getFileLifetimeDefault();
    /** Number of seconds to use as default if the supplied lifetime is zero! */
    private final long floor = Configuration.getInstance().getPinLifetimeMinimum();
    /**
     * Maximum number of seconds that an ACL can live: the life time requested by the user cannot be greater than this
     * value! This ceiling is needed because of the cron job that removes pool account mappings: when the mapping is
     * removed, there must NOT be ANY ACL for that pool-user left!
     */
    private final long ceiling = Configuration.getInstance().getPinLifetimeMaximum();

    /**
     * Private costructor that starts the cleaning timer.
     */
    private VolatileAndJiTCatalog() {
        TimerTask cleaningTask = new TimerTask() {
            @Override
            public void run() {
                purge();
            }
        };
        cleaner.scheduleAtFixedRate(cleaningTask, delay, period);
    }

    /**
     * Method that returns the only instance of PinnedFilesCatalog.
     */
    public static VolatileAndJiTCatalog getInstance() {
        return cat;
    }

    /**
     * Checks whether the given file exists in the volatile table or not.
     * 
     * @param filename
     * @return <code>true</code> if there is antry for the given file in the volatilte table, <code>false</code>
     *         otherwise.
     */
    synchronized public boolean exists(PFN pfn) {
        return dao.exists(pfn.getValue());
    }

    /**
     * Method used to expire _all_ related entries in the JiT catalogue, that were setup during a PtG operation. The
     * method is intended to be used by code handling srmAbort command. Notice that the Traverse on the parents is NOT
     * removed! This is to accomodate for the use case of a user that has run many PtG on different SURLs but all
     * contained in the same directory tree! In practice this method removes the R permission. If any entry does not
     * exist, then nothing happens and a warning gets written in the logs; otherwise entries get their start time set to
     * now, and the lifetime set to zero; in case more than one matching entry is found, a message gets written to the
     * logs, and the updating continues anyway as explained. At this point, when the garbage collector wakes up the
     * entries get cleanly handled (physical ACL is removed, catalog entry removed, etc.); or an earlier cleaning can be
     * forced by invoking directly the purge mehod. The method returns FALSE in case an entry was not found or the
     * supplied parameters were null, and TRUE otherwise. Yet keep in mind that it says nothing of whether the DB
     * operation was successful or not.
     */
    synchronized public boolean expireGetJiTs(PFN pfn, LocalUser localUser) {
        boolean ok = pfn != null && localUser != null;
        if (!ok) {
            log.error("VolatileAndJiT CATALOG: programming bug! expireGetJiTs invoked on null attributes; pfn="
                    + pfn + " localUser=" + localUser);
            return false;
        } else {
            boolean result = true;
            result = result && expireJiT(pfn, localUser, FilesystemPermission.Read);
            return result;
        }
    }

    /**
     * Method used to expire an entry in the JiT catalogue. The method is intended to be used by code handling srmAbort
     * command. If the entry does not exist, then nothing happens and a warning gets written in the logs; otherwise the
     * entry gets its start time set to now, and its lifetime set to zero; in case more than one matching entry is
     * found, a message gets written to the logs, and the updating continues anyway as explained. At this point, when
     * the garbage collector wakes up the entry is cleanly handled (physical ACL is removed, catalog entry removed,
     * etc.); or an earlier cleaning can be forced by invoking directly the purge method. The method returns FALSE in
     * case no entry was found or the supplied parameters were null, and TRUE otherwise. Yet keep in mind that is says
     * nothing of whether the DB operation was successful or not.
     */
    synchronized public boolean expireJiT(PFN pfn, LocalUser localUser, FilesystemPermission acl) {
        boolean ok = pfn != null && localUser != null && acl != null;
        if (!ok) {
            log.error("VolatileAndJiT CATALOG: programming bug! expireJiT invoked on null attributes; pfn="
                    + pfn + " localUser=" + localUser + " acl=" + acl);
            return false;
        } else {
            String fileName = pfn.getValue();
            int uid = localUser.getUid();
            int intacl = acl.getInt();
            // from the current time we remove 10 seconds because it was observed
            // that when executing purge right after invoking this method, less
            // than 1 second elapses, so no purging takes place at all since expiry
            // is not yet reached!
            // Seconds needed and not milliseconds!
            long pinStart = (Calendar.getInstance().getTimeInMillis() / 1000) - 10;
            long pinTime = 0; // set to zero the lifetime!
            int n = dao.numberJiT(fileName, uid, intacl);
            if (n == 0) {
                log.warn("VolatileAndJiT CATALOG: expireJiT found no entry for (" + fileName + "," + uid
                        + "," + intacl + ")!");
                return false;
            } else {
                dao.forceUpdateJiT(fileName, uid, intacl, pinStart, pinTime);
                if (n > 1) {
                    log.warn("VolatileAndJiT CATALOG: expireJiT found more than one entry for (" + fileName
                            + "," + uid + "," + intacl + "); the catalogue could be corrupt!");
                }
                return true;
            }
        }
    }

    /**
     * Method used to expire _all_ related entries in the JiT catalogue, that were setup during a PtP operation. The
     * method is intended to be used by code handling srmAbort command, and by srmPutDone. Notice that the Traverse on
     * the parents is NOT removed! This is to accomodate for the use case of a user that has run many PtP on different
     * SURLs but that are all contained in the same directory tree! In practice, this method removes R and W
     * permissions. If any entry does not exist, then nothing happens and a warning gets written in the logs; otherwise
     * entries get their start time set to now, and the lifetime set to zero; in case more than one matching entry is
     * found, a message gets written to the logs, and the updating continues anyway as explained. At this point, when
     * the garbage collector wakes up the entries get cleanly handled (physical ACL is removed, catalog entry removed,
     * etc.); or an earlier cleaning can be forced by invoking directly the purge mehod. The method returns FALSE in
     * case an entry was not found or the supplied parameters were null, and TRUE otherwise. Yet keep in mind that is
     * says nothing of whether the DB operation was successful or not.
     */
    synchronized public boolean expirePutJiTs(PFN pfn, LocalUser localUser) {
        boolean ok = pfn != null && localUser != null;
        if (!ok) {
            log.error("VolatileAndJiT CATALOG: programming bug! expirePutJiTs invoked on null attributes; pfn="
                    + pfn + " localUser=" + localUser);
            return false;
        } else {
            boolean result = true;
            result = result && expireJiT(pfn, localUser, FilesystemPermission.Read);
            result = result && expireJiT(pfn, localUser, FilesystemPermission.Write);
            return result;
        }
    }

    /**
     * Method that purges the catalog, removing expired ACLs and deleting expired Volatile files. When Volatile entries
     * expire, any realted JiT will automatically expire too, regardless of the specified pinLifetime: that is,
     * fileLifetime wins over pinLifetime. WARNING! Notice that the catalogue DOES get cleaned up even if the physical
     * removal of the ACL or erasing of the file fails.
     */
    synchronized public void purge() {
        log.debug("VolatileAndJiT CATALOG! Executing purge!");
        Calendar rightNow = Calendar.getInstance();
        /**
         * removes all expired entries from storm_pin and storm_track, returning two Collections: one with the PFN of
         * Volatile files, and the other with PFN + GridUser couple of the entries that were just being tracked for the
         * ACLs set up on them.
         */
        Collection[] expired = dao.removeExpired(rightNow.getTimeInMillis() / 1000);
        Collection expiredVolatile = expired[0]; // collection of expired Volatile entries
        Collection expiredJiT = expired[1]; // collection of expired JiTs
        if (expiredVolatile.size() == 0) {
            log.debug("VolatileAndJiT CATALOG! No expired Volatile entries found.");
        } else {
            log.info("VolatileAndJiT CATALOG! Found and purged the following expired Volatile entries:\n "
                    + volatileString(expired[0]));
        }
        if (expiredJiT.size() == 0) {
            log.debug("VolatileAndJiT CATALOG! No JiT entries found.");
        } else {
            log.info("VolatileAndJiT CATALOG! Found and purged the following expired JiT ACLs entries:\n "
                    + jitString(expired[1]));
        }
        // Remove ACLs
        JiTData aux = null;
        for (Iterator i = expiredJiT.iterator(); i.hasNext();) {
            aux = (JiTData) i.next();
            int jitacl = aux.acl();
            String jitfile = aux.pfn();
            int jituid = aux.uid();
            int jitgid = aux.gid();
            try {
                log.info("VolatileAndJiT CATALOG. Removing ACL " + jitacl + " on file " + jitfile
                        + " for user " + jituid + "," + jitgid);
                LocalFile auxFile = NamespaceDirector.getNamespace()
                                                     .resolveStoRIbyPFN(PFN.make(jitfile))
                                                     .getLocalFile();
                LocalUser auxUser = new LocalUser(jituid, jitgid);
                FilesystemPermission auxACL = new FilesystemPermission(jitacl);
                auxFile.revokeUserPermission(auxUser, auxACL);
            } catch (Exception e) {
                // log exceptions
                log.error("VolatileAndJiT CATALOG! Entry removed from Catalog, but physical ACL " + jitacl
                        + " for user " + jituid + "," + jitgid + " could NOT be removed from " + jitfile);
                log.error("VolatileAndJiT CATALOG! " + e);
            }
        }
        // Delete files
        String auxPFN = null;
        for (Iterator i = expiredVolatile.iterator(); i.hasNext();) {
            auxPFN = (String) i.next();
            try {
                log.info("VolatileAndJiT CATALOG. Deleting file " + auxPFN);
                LocalFile auxFile = NamespaceDirector.getNamespace()
                                                     .resolveStoRIbyPFN(PFN.make(auxPFN))
                                                     .getLocalFile();
                boolean ok = auxFile.delete();
                if (!ok) {
                    throw new Exception("Java File deletion failed!");
                }
            } catch (Exception e) {
                // log exceptions
                log.error("VolatileAndJiT CATALOG! Entry removed from Catalog, but physical file " + auxPFN
                        + " could NOT be deleted!");
                log.error("VolatileAndJiT CATALOG! " + e);
            }
        }
    }

    /**
     * Method used upon expiry of SRM_SPACE_AVAILABLE to remove all JiT entries in the DB table, related to the given
     * PFN; Notice that _no_ distinction is made aboutthe specific user! This is because upon expiry of
     * SRM_SPACE_AVAILABLE the file gets erased, so all JiTs on that file are automatically erased. This implies that
     * all catalogue entries get removed. If no entries are present nothing happens.
     */
    synchronized public void removeAllJiTsOn(PFN pfn) {
        boolean ok = pfn != null;
        if (!ok) {
            log.error("VolatileAndJiT CATALOG: programming bug! removeAllJiTsOn invoked on null pfn!");
        } else {
            String fileName = pfn.getValue();
            dao.removeAllJiTsOn(fileName);
        }
    }

    /**
     * Method used to remove a Volatile entry that matches the supplied pfn, from the DB. If null is supplied, an error
     * message gets logged and nothing happens. If PFN is not found, nothing happens and _no_ message gets logged.
     */
    synchronized public void removeVolatile(PFN pfn) {
        boolean ok = pfn != null;
        if (!ok) {
            log.warn("VolatileAndJiT CATALOG: programming bug! removeVolatile invoked on null pfn!");
        } else {
            dao.removeVolatile(pfn.getValue());
        }
    }

    /**
     * Method used to keep track of an ACL set up on a PFN; it needs the PFN, the LocalUser, the ACL and the desired
     * pinLifeTime. If the 3-ple (PFN, ACL, LocalUser) is not present, it gets added; if it is already present, provided
     * the new desired expiry occurs after the present one, it gets changed. If the supplied lifetime is zero, then a
     * default value is used instead. If it is larger than a ceiling, that ceiling is used instead. The floor value in
     * seconds can be set from the configuration file, with the property: pinLifetime.minimum While the ceiling value in
     * seconds is set with: pinLifetime.maximum BEWARE: The intended use case is in both srmPrepareToGet and
     * srmPrepareToPut, for the case of the _JiT_ security mechanism. The maximum is necessary because JiT ACLs cannot
     * last longer than the amount of time the pool account is leased. Notice that for Volatile entries, a pinLifetime
     * larger than the fileLifetime can be specified. However, when Volatile files expire any related JiTs automatically
     * expire in anticipation!
     */
    synchronized public void trackJiT(PFN pfn, LocalUser localUser, FilesystemPermission acl, Calendar start,
            TLifeTimeInSeconds pinLifetime) {
        boolean ok = pfn != null && localUser != null && acl != null && start != null && pinLifetime != null;
        if (!ok) {
            log.error("VolatileAndJiT CATALOG: programming bug! TrackACL invoked on null attributes; pfn="
                    + pfn + " localUser=" + localUser + " acl=" + acl + " start=" + start + " pinLifetime="
                    + pinLifetime);
        } else {
            String fileName = pfn.getValue();
            int uid = localUser.getUid();
            int gid = localUser.getPrimaryGid();
            int intacl = acl.getInt();
            long pinStart = start.getTimeInMillis() / 1000; // seconds needed and not milliseconds!
            long pinTime = validatePinLifetime(pinLifetime.value());
            int n = dao.numberJiT(fileName, uid, intacl);
            if (n == 0) {
                dao.addJiT(fileName, uid, gid, intacl, pinStart, pinTime);
            } else {
                dao.updateJiT(fileName, uid, intacl, pinStart, pinTime);
                if (n > 1) {
                    log.warn("VolatileAndJiT CATALOG: More than one entry found for (" + fileName + "," + uid
                            + "," + intacl + "); the catalogue could be corrupt!");
                }
            }
        }
    }

    /**
     * Method that adds an entry to the catalogue that keeps track of Volatile files. The PFN and the fileLifetime are
     * needed. If no entry corresponding to the given PFN is found, a new one gets recorded. If the PFN is already
     * present, then provided the new expiry (obtained by adding together current-time and requested-lifetime) exceeds
     * the expiry in the catalogue, the entry is updated. Otherwise nothing takes place. If the supplied fileLifetime is
     * zero, then a default value is used instead. This floor default value in seconds can be set from the configuration
     * file, with the property: fileLifetime.default BEWARE: The intended use case for this method is during
     * srmPrepareToPut. When files are uploaded into StoRM, they get specified as Volatile or Permanent. The PtP logic
     * determines if the request is for a Volatile file and in that case it adds a new entry in the catalog. That is the
     * purpose of this method. Any subsequent PtP call will just result in a modification of the expiry, provided the
     * newer one lasts longer than the original one. Yet bear in mind that two or more PtP on the same file makes NO
     * SENSE AT ALL! If any DB error occurs, then nothing gets added/updated and an error messagge gets logged.
     */
    synchronized public void trackVolatile(PFN pfn, Calendar start, TLifeTimeInSeconds fileLifetime) {
        boolean ok = pfn != null && fileLifetime != null && start != null;
        if (!ok) {
            log.warn("VolatileAndJiT CATALOG: programming bug! volatileEntry invoked on null attributes; pfn="
                    + pfn + " start=" + start + " fileLifetime=" + fileLifetime);
        } else {
            String fileName = pfn.getValue();
            long fileTime = fileLifetime.value();
            if (fileTime <= 0) {
                fileTime = defaultFileLifetime;
            }
            long fileStart = start.getTimeInMillis() / 1000; // seconds needed and not milliseconds!
            int n = dao.numberVolatile(fileName);
            if (n == -1) {
                log.error("VolatileAndJiT CATALOG! DB problem does not allow to count number of Volatile entries for "
                        + pfn + "! Volatile entry NOT processed!");
            } else if (n == 0) {
                dao.addVolatile(fileName, fileStart, fileTime);
            } else {
                dao.updateVolatile(fileName, fileStart, fileTime);
                if (n > 1) {
                    log.warn("VolatileAndJiT CATALOG: More than one entry found for " + fileName
                            + "; the catalogue could be corrupt!");
                }
            }
        }
    }

    /**
     * Method that returns a List whose first element is a Calendar with the starting date and time of the lifetime of
     * the supplied PFN, and whose second element is the TLifeTime the system is keeping the PFN. If no entry is found
     * for the given PFN, an empty List is returned. Likewise if any DB error occurs. In any case, proper error
     * messagges get logged. Moreover notice that if for any reason the value for the Lifetime read from the DB does not
     * allow creation of a valid TLifeTimeInSeconds, an Empty one is returned. Error messages in logs warn of the
     * situation.
     */
    synchronized public List volatileInfoOn(PFN pfn) {
        boolean ok = pfn != null;
        ArrayList aux = new ArrayList();
        if (!ok) {
            log.error("VolatileAndJiT CATALOG: programming bug! volatileInfoOn invoked on null PFN!");
            return aux;
        } else {
            Collection<Long> c = dao.volatileInfoOn(pfn.getValue());
            if (c.size() == 2) {
                Iterator<Long> i = c.iterator();
                // start time
                long startInMillis = i.next().longValue() * 1000;
                Calendar auxcal = Calendar.getInstance();
                auxcal.setTimeInMillis(startInMillis);
                aux.add(auxcal);
                // lifeTime
                long lifetimeInSeconds = ((Long) i.next()).longValue();
                TLifeTimeInSeconds auxLifeTime = TLifeTimeInSeconds.makeEmpty();
                try {
                    auxLifeTime = TLifeTimeInSeconds.make(lifetimeInSeconds, TimeUnit.SECONDS);
                } catch (InvalidTLifeTimeAttributeException e) {
                    log.error("VolatileAndJiT CATALOG: programming bug! Retrieved long does not allow TLifeTimeCreation! long is: "
                            + lifetimeInSeconds + "; exception is: " + e);
                }
                aux.add(auxLifeTime);
            }
            return aux;
        }
    }

    /**
     * Private method used to return a String representation of the expired entries Collection of JiTData.
     */
    private String jitString(Collection<JiTData> c) {
        if (c == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("file,acl,uid,gid\n");
        JiTData aux = null;
        for (Iterator<JiTData> i = c.iterator(); i.hasNext();) {
            aux = i.next();
            sb.append(aux.pfn());
            sb.append(",");
            sb.append(aux.acl());
            sb.append(",");
            sb.append(aux.uid());
            sb.append(",");
            sb.append(aux.gid());
            if (i.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Private method that makes sure that the lifeTime of the request: (1) It is not less than a predetermined value:
     * this check is needed because clients may omit to supply a value and some default one must be used; moreover, it
     * is feared that if the requested lifetime is very low, such as 0 or a few seconds, there could be strange problems
     * in having a file written and erased immediately. (2) It is not larger than a given ceiling; this is necessary
     * because in the JiT model, the underlying system may decide to remove the pool account mappings; it is paramount
     * that no ACLs remain set up for the now un-associated pool account.
     */
    private long validatePinLifetime(long lifetime) {
        long duration = lifetime < floor ? floor : lifetime; // adjust for lifetime set to zero!
        duration = duration <= ceiling ? duration : ceiling; // make sure lifetime is not longer than the maximum set!
        return duration;
    }

    /**
     * Private method used to return a String representation of the expired entries Collection of pfn Strings.
     */
    private String volatileString(Collection<String> c) {
        if (c == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> i = c.iterator(); i.hasNext();) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
