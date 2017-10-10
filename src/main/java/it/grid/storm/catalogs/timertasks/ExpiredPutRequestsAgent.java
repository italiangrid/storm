package it.grid.storm.catalogs.timertasks;

import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.PtPChunkDAO;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommandException;


public class ExpiredPutRequestsAgent extends TimerTask {

	private static final Logger log = LoggerFactory
		.getLogger(ExpiredPutRequestsAgent.class);
	
	private PtPChunkDAO dao = PtPChunkDAO.getInstance();
	private final String name = ExpiredPutRequestsAgent.class.getName();

	@Override
	public synchronized void run() {

		try {

			transitExpiredLifetimeRequests();
			transitExpiredInProgressRequests();

		} catch (Exception e) {

			log.error("{}: {}", e.getClass(), e.getMessage(), e);

		}
	}

	private void transitExpiredLifetimeRequests() {

		Map<Long,String> expiredRequests = dao.getExpiredSRM_SPACE_AVAILABLE();
		if (expiredRequests.isEmpty()) {
			log.debug("No expired SRM_SPACE_AVAILABLE requests found.");
			return;
		}
		log.info("{}: {} expired requests retrieved from db", name, expiredRequests.size());
		log.debug("{}: Launch srmPutDone on expired requests ...", name);
		expiredRequests.entrySet().forEach(e -> executePutDone(e.getKey(), e.getValue()));
		log.debug("{}: Update db statuses ...", name);
		int numTransited =
			dao.transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(expiredRequests.keySet());
		log.info("{}: {}/{} expired ptp requests moved to "
			+ "SRM_FILE_LIFETIME_EXPIRED", name, numTransited, expiredRequests.size());
	}

	private void executePutDone(Long id, String surl) {

		log.debug("{}: processing request with id = {} and surl = {}", name, id, surl);
		TSURL tSurl = null;
		try {
			tSurl = TSURL.makeFromStringValidate(surl);
			log.debug("{}: computing srmPutDone on SURL {}", name, tSurl);
			PutDoneCommand.executePutDone(tSurl);
		} catch (InvalidTSURLAttributesException | PutDoneCommandException e) {

			log.error("Unable to execute PutDone on request with id {} and surl {}: ", id, surl,
			e.getMessage(), e);
		}
	}

	private void transitExpiredInProgressRequests() {

		Map<Long,String> expiredRequests = dao.getExpiredSRM_REQUEST_INPROGRESS();
		if (expiredRequests.isEmpty()) {
			log.debug("No expired SRM_REQUEST_INPROGRESS requests found.");
			return;
		}
		log.info("{}: {} expired in progress requests retrieved from db", name, expiredRequests.size());
		log.debug("{}: Update db statuses ...", name);
		int numTransited =
			dao.transitExpiredSRM_REQUEST_INPROGRESStoSRM_FAILURE(expiredRequests.keySet());
		log.info("{}: {}/{} expired ptp requests moved to SRM_FAILURE", name, numTransited,
			expiredRequests.size());
	}

}
