/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs.timertasks;

import it.grid.storm.catalogs.PtPChunkDAO;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;


public class ExpiredPutRequestsAgent extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(ExpiredPutRequestsAgent.class);

    private static final String NAME = "Expired-PutRequests-Agent";

    private long inProgressRequestsExpirationTime;

    public ExpiredPutRequestsAgent(long inProgressRequestsExpirationTime) {

        this.inProgressRequestsExpirationTime = inProgressRequestsExpirationTime;
        log.info("{} created.", NAME);
    }

    @Override
    public synchronized void run() {

        log.debug("{} run.", NAME);
        try {

            transitExpiredLifetimeRequests();
            transitExpiredInProgressRequests();

        } catch (Exception e) {

            log.error("{}: {}", e.getClass(), e.getMessage(), e);

        }
    }

    private void transitExpiredLifetimeRequests() {

        PtPChunkDAO dao = PtPChunkDAO.getInstance();
        Map<Long, String> expiredRequests = dao.getExpiredSRM_SPACE_AVAILABLE();
        log.debug("{} lifetime-expired requests found ... ", NAME, expiredRequests.size());

        if (expiredRequests.isEmpty()) {
            return;
        }

        expiredRequests.entrySet().forEach(e -> executePutDone(e.getKey(), e.getValue()));

        int count = dao.transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(
                expiredRequests.keySet());
        log.info("{} updated expired put requests - {} db rows affected", NAME, count);
    }

    private void executePutDone(Long id, String surl) {

        try {

            if (PutDoneCommand.executePutDone(TSURL.makeFromStringValidate(surl))) {
                log.info("{} successfully executed a srmPutDone on surl {}", NAME, surl);
            }

        } catch (InvalidTSURLAttributesException | PutDoneCommandException e) {

            log.error("{}. Unable to execute PutDone on request with id {} and surl {}: ", NAME, id,
                    surl, e.getMessage(), e);
        }
    }

    private void transitExpiredInProgressRequests() {

        PtPChunkDAO dao = PtPChunkDAO.getInstance();
        List<Long> expiredRequestsIds =
                dao.getExpiredSRM_REQUEST_INPROGRESS(inProgressRequestsExpirationTime);
        log.debug("{} expired in-progress requests found.", expiredRequestsIds.size());

        if (expiredRequestsIds.isEmpty()) {
            return;
        }

        int count = dao.transitExpiredSRM_REQUEST_INPROGRESStoSRM_FAILURE(expiredRequestsIds);
        log.info("{} moved in-progress put requests to failure - {} db rows affected", NAME, count);
    }
}
