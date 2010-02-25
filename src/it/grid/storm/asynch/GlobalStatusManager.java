package it.grid.storm.asynch;

import it.grid.storm.catalogs.ChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to keep track of the global state of a request consisting of the supplied ChunkData: it encloses the logic
 * for computing such global state. It must be intialised with the TRequestToken of the request; any ChunkData that must
 * be considered in the computation must be added with the addChunk method; when all ChunkData have been added,
 * finishedAdding method must be called. Chunks then must invoke either successfulChunk or failedChunk passing their
 * ChunkData as arguments, in order for the OverallRequestStatus to be updated accordingly. When all chunks complete,
 * the state is updated into persistence automatically. Finally, chunks may also invoke expiredSpaceLifetimeChunk to
 * signal that the associated space token has its lifetime expired. This is a special state: it is not possible to have
 * a request with some chunks in this situation while others are not. All chunks of the same request should invoke this
 * method: it is an anomaly if it does not occur; it gets signalled properly.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2006
 */
public class GlobalStatusManager {

    private static Logger log = LoggerFactory.getLogger(GlobalStatusManager.class);
    private TRequestToken rt = null;
    private Map<Long, Object> chunks = new HashMap<Long, Object>(); // HashMap containing handles to all chunks!
    private boolean finished = false; // boolean true if all chunks of the request have been added to the map
    private InternalState internal = InternalState.IN_PROGRESS;
    private Object mock = new Object(); // private mock object that will be put repeatedly in Map!

    public GlobalStatusManager(TRequestToken rt) throws InvalidOverallRequestAttributeException {
        if (rt == null) {
            throw new InvalidOverallRequestAttributeException();
        }
        this.rt = rt;
    }

    /**
     * Method that tells this OverallRequest to consider the state of the supplied ChunkData, when computing the global
     * state. If finishedAdding method has already been invoked, this method has no effect.
     */
    synchronized public void addChunk(ChunkData c) {
        log.debug("GlobalStatusManager: asked to add chunkData " + c.primaryKey());
        if ((!finished) && (c != null)) {
            chunks.put(new Long(c.primaryKey()), mock);
            log.debug("GlobalStatusManager: chunkData added.");
        } else {
            log.debug("GlobalStatusManager: chunkData NOT added because either it is null or finishedAdding has already been invoked!");
        }
    }

    /**
     * Method used to indicate that no other ChunkDAta will be considered for the computation of the global state. After
     * invoking this method, all subsequent calls to addChunk will be ignored.
     */
    synchronized public void finishedAdding() {
        log.debug("GlobalStatusManager: received finishedAdding signal.");
        this.finished = true;
        if (chunks.isEmpty()) {
            // finishedAdding invoked after _all_ ChunkData were processed!
            // make final state transition
            if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                internal = InternalState.SUCCESS;
            } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                internal = InternalState.FAIL;
            }
            // save state in persistence
            log.debug("GlobalStatusManager: since all chunks have been processed, saving final state "
                    + internal);
            saveRequestState();
        }
    }

    /**
     * Method used to communicate that the given ChunkData c has transited to a final successful state. If it is null,
     * there are no Chunks being kept track of, or that specific ChunkData is not being tracked, an error message gets
     * written to the logs and the global state transits to ERROR.
     */
    synchronized public void successfulChunk(ChunkData c) {
        
        log.debug("GlobalStatusManager: received successfulChunk signal for " + c);
        
        if ((c != null) && (!chunks.isEmpty()) && (chunks.remove(new Long(c.primaryKey())) != null)) {
            
            // manage state transition: c was indeed there
            if (finished && (chunks.isEmpty())) {
                // no other chunk will be added to request, and none is left: this was the last one to be processed!
                log.debug("GlobalStatusManager: finished and no more chunks left... ");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.SUCCESS;
                } else if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                    internal = InternalState.SUCCESS;
                } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                    internal = InternalState.PARTIAL;
                } else if (internal == InternalState.PARTIAL) {
                    internal = InternalState.PARTIAL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                
                log.debug("GlobalStatusManager: saving final global state " + internal);
                saveRequestState();
                
            } else if (finished && (!chunks.isEmpty())) {
                
                // no chunk will be added to request, but there are _more_ left to be processed!
                log.debug("GlobalStatusManager: finished but there are more chunks to be processed... ");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.INTERMEDIATE_SUCCESS;
                } else if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                    internal = InternalState.INTERMEDIATE_SUCCESS; // stays the same!
                } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                    internal = InternalState.PARTIAL;
                } else if (internal == InternalState.PARTIAL) {
                    internal = InternalState.PARTIAL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: transited to " + internal);
                
            } else if (!finished) {
                
                // more chunks may be added to request, and it is all the same if there are or aren't any left to be
                // processed!
                log.debug("GlobalStatusManager: still not finished adding chunks for consideration, but it is the same whether there are more to be processed or not...");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.INTERMEDIATE_SUCCESS;
                } else if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                    internal = InternalState.INTERMEDIATE_SUCCESS; // stays the same!
                } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                    internal = InternalState.PARTIAL;
                } else if (internal == InternalState.PARTIAL) {
                    internal = InternalState.PARTIAL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: transited to " + internal);
                
            } else {
                // This cannot possibly occur by logic! But there could be multithreading issues in case of bugs!
                log.error("ERROR IN GLOBAL STATUS EVALUATION! An impossible logic codition has materialised: it may be due to multithreading issues! Request is "
                        + rt);
                internal = InternalState.ERROR;
            }
            
        } else {
            // error situation!
            log.error("ERROR IN GLOBAL STATUS EVALUATION! There was an attempt to signal a successful Chunk, but it is either null, or there are actually no Chunks left to be considered, or it was not originally asked to be considered in the evaluation!");
            log.error("Request: " + rt);
            log.error("Chunk: " + c);
            internal = InternalState.ERROR;
        }
    }

    /**
     * Method used to communicate that the given Chunk c has transited to a final failed state. If it is null, there are
     * no Chunks being kept track of, or that specific Chunk is not being tracked, an error message gets written to the
     * logs and the global state transits to ERROR.
     */
    synchronized public void failedChunk(ChunkData c) {
        log.debug("GlobalStatusManager: received failedChunk signal for " + c);
        if ((c != null) && (!chunks.isEmpty()) && (chunks.remove(new Long(c.primaryKey())) != null)) {
            // manage state transition: c was indeed there
            if (finished && (chunks.isEmpty())) {
                // no other chunk will be added to request, and none is left: this was the last one to be processed!
                log.debug("GlobalStatusManager: finished and no more chunks left... ");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.FAIL;
                } else if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                    internal = InternalState.PARTIAL;
                } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                    internal = InternalState.FAIL;
                } else if (internal == InternalState.PARTIAL) {
                    internal = InternalState.PARTIAL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: saving final global state " + internal);
                saveRequestState();
            } else if (finished && (!chunks.isEmpty())) {
                // no chunk will be added to request, but there are _more_ left to be processed!
                log.debug("GlobalStatusManager: finished but there are more chunks to be processed... ");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.INTERMEDIATE_FAIL;
                } else if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                    internal = InternalState.PARTIAL;
                } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                    internal = InternalState.INTERMEDIATE_FAIL; // stays the same!
                } else if (internal == InternalState.PARTIAL) {
                    internal = InternalState.PARTIAL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: transited to " + internal);
            } else if (!finished) {
                // more chunks may be added to request, and it is all the same if there are or arent any left to be
                // processed!
                log.debug("GlobalStatusManager: still not finished adding chunks for consideration, but it is the same whether there are more to be processed or not...");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.INTERMEDIATE_FAIL;
                } else if (internal == InternalState.INTERMEDIATE_SUCCESS) {
                    internal = InternalState.PARTIAL;
                } else if (internal == InternalState.INTERMEDIATE_FAIL) {
                    internal = InternalState.INTERMEDIATE_FAIL; // stays the same!
                } else if (internal == InternalState.PARTIAL) {
                    internal = InternalState.PARTIAL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: transited to " + internal);
            } else {
                // This cannot possibly occur by logic! But there could be multithreading issues in case of bugs!
                log.error("ERROR IN GLOBAL STATUS EVALUATION! An impossible logic codition has materialised: it may be due to multithreading issues! Request is "
                        + rt);
                internal = InternalState.ERROR;
            }
        } else {
            // error situation!
            log.error("ERROR IN GLOBAL STATUS EVALUATION! There was an attempt to signal a failed Chunk, but it is either null, or there are actually no Chunks left to be considered, or it was not originally asked to be considered in the evaluation!");
            log.error("Request: " + rt);
            log.error("ChunkData: " + c);
            internal = InternalState.ERROR;
        }
    }

    /**
     * Method used to communicate that the given Chunk c has transited to a final failed state specifically due to an
     * expired SpaceLifetime. If it is null, there are no Chunks being kept track of, or that specific Chunk is not
     * being tracked, an error message gets written to the logs and the global state transits to ERROR.
     */
    synchronized public void expiredSpaceLifetimeChunk(ChunkData c) {
        log.debug("GlobalStatusManager: received expiredSpaceLifetimeChunk signal for " + c);
        if ((c != null) && (!chunks.isEmpty()) && (chunks.remove(new Long(c.primaryKey())) != null)) {
            // manage state transition: c was indeed there
            if (finished && (chunks.isEmpty())) {
                // no other chunk will be added to request, and none is left: this was the last one to be processed!
                log.debug("GlobalStatusManager: finished and no more chunks left... ");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.SPACEFAIL;
                } else if (internal == InternalState.SPACEFAIL) {
                    internal = InternalState.SPACEFAIL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: saving final global state " + internal);
                saveRequestState();
            } else if (finished && (!chunks.isEmpty())) {
                // no chunk will be added to request, but there are _more_ left to be processed!
                log.debug("GlobalStatusManager: finished but there are more chunks to be processed... ");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.SPACEFAIL;
                } else if (internal == InternalState.SPACEFAIL) {
                    internal = InternalState.SPACEFAIL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: transited to " + internal);
            } else if (!finished) {
                // more chunks may be added to request, and it is all the same if there are or arent any left to be
                // processed!
                log.debug("GlobalStatusManager: still not finished adding chunks for consideration, but it is the same whether there are more to be processed or not...");
                if (internal == InternalState.IN_PROGRESS) {
                    internal = InternalState.SPACEFAIL;
                } else if (internal == InternalState.SPACEFAIL) {
                    internal = InternalState.SPACEFAIL; // stays the same!
                } else if (internal == InternalState.ERROR) {
                    internal = InternalState.ERROR; // stays the same!
                } else {
                    log.error("ERROR in GlobalStatusManager: programming bug! Unexpected InternalState: "
                            + internal);
                }
                log.debug("GlobalStatusManager: transited to " + internal);
            } else {
                // This cannot possibly occur by logic! But there could be multithreading issues in case of bugs!
                log.error("ERROR IN GLOBAL STATUS EVALUATION! An impossible logic codition has materialised: it may be due to multithreading issues! Request is "
                        + rt);
                internal = InternalState.ERROR;
            }
        } else {
            // error situation!
            log.error("ERROR IN GLOBAL STATUS EVALUATION! There was an attempt to signal a space failed Chunk, but it is either null, or there are actually no Chunks left to be considered, or it was not originally asked to be considered in the evaluation!");
            log.error("Request: " + rt);
            log.error("ChunkData: " + c);
            internal = InternalState.ERROR;
        }
    }

    /**
     * Method used to update the state in persistence.
     */
    private void saveRequestState() {
        log.debug("GlobalStatusManager: invoked saveRequestState.");
        
        try {
            
            boolean updatePinFileLifetime;
            
            TReturnStatus retstat = null;
            
            if (internal == InternalState.ERROR) {
                
                updatePinFileLifetime = true;
                retstat = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                            "Global status cannot be evaluated: single file status must be checked.");
                
            } else if (internal == InternalState.FAIL) {
                
                updatePinFileLifetime = false;
                retstat = new TReturnStatus(TStatusCode.SRM_FAILURE, "All chunks failed!");
                
            } else if (internal == InternalState.SUCCESS) {
                
                updatePinFileLifetime = true;
                retstat = new TReturnStatus(TStatusCode.SRM_SUCCESS, "All chunks successfully handled!");
                
            } else if (internal == InternalState.PARTIAL) {
                
                updatePinFileLifetime = true;
                retstat = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                            "Some chunks were successful while others failed!");
                
            } else if (internal == InternalState.SPACEFAIL) {
                
                updatePinFileLifetime = false;
                retstat = new TReturnStatus(TStatusCode.SRM_SPACE_LIFETIME_EXPIRED,
                                            "Supplied SpaceToken has expired lifetime!");
            } else {
                
                updatePinFileLifetime = true;
                retstat = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                            "Global status cannot be evaluated: single file status must be checked.");
                log.error("ERROR IN GLOBAL STATUS EVALUATION! " + internal
                        + " was attempted to be written into persistence, but it is not a final state!");
                log.error("Request: " + rt);
            }
            
            log.debug("GlobalStatusManager: saving into persistence " + retstat);
            
            if (updatePinFileLifetime) {
                RequestSummaryCatalog.getInstance().updateGlobalStatusPinFileLifetime(rt, retstat);
                
            } else {
                RequestSummaryCatalog.getInstance().updateGlobalStatus(rt, retstat);
            }
            
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("ERROR IN GLOBAL STATUS EVALUATION! Could not create a valid TReturnStatus: this is a programming bug! "
                    + e);
        }
    }

    /**
     * Auxiliary private class that keeps track of internal state of request, with respect to all the chunks.
     * 
     * @author EGRID - ICTP Trieste
     * @version 2.0
     * @date September, 2006
     */
    private static class InternalState {
        public static InternalState IN_PROGRESS = new InternalState() {
            @Override
            public String toString() {
                return "InternalState IN_PROGRESS";
            }
        };

        public static InternalState INTERMEDIATE_SUCCESS = new InternalState() {
            @Override
            public String toString() {
                return "InternalState INTERMEDIATE_SUCCESS";
            }
        };

        public static InternalState INTERMEDIATE_FAIL = new InternalState() {
            @Override
            public String toString() {
                return "InternalState INTERMEDIATE_FAIL";
            }
        };

        public static InternalState ERROR = new InternalState() {
            @Override
            public String toString() {
                return "InternalState ERROR";
            }
        };

        public static InternalState FAIL = new InternalState() {
            @Override
            public String toString() {
                return "InternalState FAIL";
            }
        };

        public static InternalState SPACEFAIL = new InternalState() {
            @Override
            public String toString() {
                return "InternalState SPACEFAIL";
            }
        };

        public static InternalState SUCCESS = new InternalState() {
            @Override
            public String toString() {
                return "InternalState SUCCESS";
            }
        };

        public static InternalState PARTIAL = new InternalState() {
            @Override
            public String toString() {
                return "InternalState PARTIAL";
            }
        };

        private InternalState() {
        }
    }

}
