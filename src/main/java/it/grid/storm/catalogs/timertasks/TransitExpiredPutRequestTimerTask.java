package it.grid.storm.catalogs.timertasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.PtPChunkDAO;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommandException;


public class TransitExpiredPutRequestTimerTask extends TimerTask {

	private static final Logger log = LoggerFactory
		.getLogger(TransitExpiredPutRequestTimerTask.class);
	
	private final PtPChunkDAO dao = PtPChunkDAO.getInstance();
	private final String name = TransitExpiredPutRequestTimerTask.class.getName();

	private Map<Long,String> getExpiredRequests() {
		
		return dao.getExpiredSRM_SPACE_AVAILABLE();
	}
	
	private int expireRequests(List<Long> ids) {

		return dao.transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(
			new ArrayList<Long>(ids));
	}
	
	private void executePutDone(Map<Long,String> ids) {
		
		for (Entry<Long, String> entry : ids.entrySet()) {
			
			log.debug("{}: processing request with id = {} and surl = {}", name,
				entry.getKey(), entry.getValue());
			
			TSURL surl = null;
			try {
				
				surl = TSURL.makeFromStringValidate(entry.getValue());
				log.debug("{}: computing srmPutDone on SURL {}", name, surl);
				PutDoneCommand.executePutDone(surl, null);
				
			} catch (InvalidTSURLAttributesException e1) {
				
				log.error("Unexpected SURL conversion error: unable to convert "
					+ "the value read from database '{}' to a valid TSURL object",
					surl);
				continue;
				
			} catch (PutDoneCommandException e) {
				
				log.error(e.getMessage(), e);
				continue;
			
			}
		}
	}
	
	@Override
	public synchronized void run() {
		
		try {
			
			Map<Long,String> ids = getExpiredRequests();
			int numExpired = ids.size();
			
			if (numExpired == 0) {
				log.info("{}: Nothing to do.", name);
				return;
			}
			log.info("{}: {} expired requests retrieved from db", name, numExpired);
			
			log.debug("{}: Launch srmPutDone on expired requests ...", name);
			executePutDone(ids);

			log.debug("{}: Update db statuses ...", name);
			int numTransited = expireRequests(new ArrayList<Long>(ids.keySet()));
			log.info("{} - {}/{} expired ptp requests moved to "
				+ "SRM_FILE_LIFETIME_EXPIRED", name, numTransited, numExpired);

		} catch (Throwable e) {

			log.error("{}: {}", e.getClass(), e.getMessage(), e);

		} finally {
			
			
		}
	}

}
