package it.grid.storm.synchcall.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.datatransfer.RequestUnknownException;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

public class SurlStatusCommandHelper {

	private static final Logger log = LoggerFactory
		.getLogger(SurlStatusCommandHelper.class);

	public static Map<TSURL, TReturnStatus> getSurlsStatus(InputData inputData,
		TRequestType requestType) throws IllegalArgumentException,
		RequestUnknownException, UnknownTokenException, ExpiredTokenException,
		IllegalStateException {

		if (inputData instanceof ManageFileTransferRequestFilesInputData) {
			return getSurlsStatus(
				((ManageFileTransferRequestFilesInputData) inputData).getRequestToken(),
				((ManageFileTransferRequestFilesInputData) inputData).getArrayOfSURLs(),
				requestType);
		} else {
			if (inputData instanceof ManageFileTransferFilesInputData) {
				if (inputData instanceof IdentityInputData) {
					return getSurlsStatus(
						((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs(),
						((IdentityInputData) inputData).getUser(), true, requestType, true);
				} else {
					return getSurlsStatus(
						((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs(),
						null, false, requestType, true);
				}
			} else {
				if (inputData instanceof ManageFileTransferRequestInputData) {
					return getSurlsStatus(
						((ManageFileTransferRequestInputData) inputData).getRequestToken(),
						requestType);
				} else {
					throw new IllegalStateException("Unknown iput data type "
						+ inputData.getClass());
				}
			}
		}
	}

	public static Map<TSURL, TReturnStatus> getSurlsStatus(InputData inputData)
		throws IllegalArgumentException, RequestUnknownException,
		UnknownTokenException, ExpiredTokenException, IllegalStateException {

		if (inputData instanceof ManageFileTransferRequestFilesInputData) {
			return getSurlsStatus(
				((ManageFileTransferRequestFilesInputData) inputData).getRequestToken(),
				((ManageFileTransferRequestFilesInputData) inputData).getArrayOfSURLs());
		} else {
			if (inputData instanceof ManageFileTransferFilesInputData) {
				if (inputData instanceof IdentityInputData) {
					return getSurlsStatus(
						((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs(),
						((IdentityInputData) inputData).getUser(), true, null, false);
				} else {
					return getSurlsStatus(
						((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs(),
						null, false, null, false);
				}
			} else {
				if (inputData instanceof ManageFileTransferRequestInputData) {
					return getSurlsStatus(((ManageFileTransferRequestInputData) inputData)
						.getRequestToken());
				} else {
					throw new IllegalStateException("Unknown iput data type "
						+ inputData.getClass());
				}
			}
		}
	}

	public static Map<TSURL, TReturnStatus> getSurlsStatus(
		TRequestToken requestToken, TRequestType requestType)
		throws RequestUnknownException, IllegalArgumentException,
		UnknownTokenException, ExpiredTokenException {

		if (requestToken == null) {
			throw new IllegalArgumentException(
				"unable to get the statuses, null arguments: requestToken="
					+ requestToken);
		}
		Map<TSURL, TReturnStatus> surlsStatuses = SurlStatusManager.getSurlsStatus(
			requestToken, requestType);
		if (surlsStatuses.isEmpty()) {
			log.info("No one of the requested surls found for the provided token");
			throw new RequestUnknownException(
				"No one of the requested surls found for the provided token");
		}
		return surlsStatuses;
	}

	public static Map<TSURL, TReturnStatus> getSurlsStatus(
		TRequestToken requestToken) throws RequestUnknownException,
		IllegalArgumentException, UnknownTokenException, ExpiredTokenException {

		if (requestToken == null) {
			throw new IllegalArgumentException(
				"unable to get the statuses, null arguments: requestToken="
					+ requestToken);
		}
		Map<TSURL, TReturnStatus> surlsStatuses = SurlStatusManager
			.getSurlsStatus(requestToken);
		if (surlsStatuses.isEmpty()) {
			log.info("No one of the requested surls found for the provided token");
			throw new RequestUnknownException(
				"No one of the requested surls found for the provided token");
		}
		return surlsStatuses;
	}

	private static Map<TSURL, TReturnStatus> getSurlsStatus(
		ArrayOfSURLs arrayOfSURLs, GridUserInterface user, boolean withUser,
		TRequestType requestType, boolean withRequestType)
		throws RequestUnknownException, IllegalArgumentException {

		if (arrayOfSURLs == null || (withUser && user == null)
			|| (withRequestType && requestType == null)) {
			throw new IllegalArgumentException(
				"unable to get the statuses, invalid arguments: arrayOfSURLs="
					+ arrayOfSURLs + " hasUser=" + withUser + " user=" + user
					+ " withRequestType=" + withRequestType + " requestType="
					+ requestType);
		}
		Map<TSURL, TReturnStatus> surlsStatuses = new HashMap<TSURL, TReturnStatus>();
		for (TSURL surl : arrayOfSURLs.getArrayList()) {
			try {
				if (withUser) {
					if (withRequestType) {
						surlsStatuses.put(surl,
							SurlStatusManager.getSurlStatus(surl, user, requestType));
					} else {
						surlsStatuses.put(surl,
							SurlStatusManager.getSurlsStatus(surl, user));
					}

				} else {
					if (withRequestType) {
						surlsStatuses.put(surl,
							SurlStatusManager.getSurlsStatus(surl, requestType));
					} else {
						surlsStatuses.put(surl, SurlStatusManager.getSurlStatus(surl));
					}
				}

			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(
					"Unexpected IllegalArgumentException in getSurlsStatus: " + e);
			} catch (UnknownSurlException e) {
				log.info("Requested surl {} is unknown", surl);
			}
		}
		if (surlsStatuses.isEmpty()) {
			log.info("No one of the requested surls found for the provided token");
			throw new RequestUnknownException(
				"No one of the requested surls found for the provided token");
		}
		return surlsStatuses;
	}

	public static Map<TSURL, TReturnStatus> getSurlsStatus(
		TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs,
		TRequestType requestType) throws RequestUnknownException,
		IllegalArgumentException, UnknownTokenException, ExpiredTokenException {

		if (requestToken == null || arrayOfSURLs == null) {
			throw new IllegalArgumentException(
				"unable to get the statuses, null arguments: requestToken="
					+ requestToken + " arrayOfSURLs=" + arrayOfSURLs);
		}
		Map<TSURL, TReturnStatus> surlsStatuses = SurlStatusManager.getSurlsStatus(
			requestToken, arrayOfSURLs.getArrayList(), requestType);
		if (surlsStatuses.isEmpty()) {
			log.info("No one of the requested surls found for the provided token");
			throw new RequestUnknownException(
				"No one of the requested surls found for the provided token");
		}
		return surlsStatuses;
	}

	public static Map<TSURL, TReturnStatus> getSurlsStatus(
		TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs)
		throws RequestUnknownException, IllegalArgumentException,
		UnknownTokenException, ExpiredTokenException {

		if (requestToken == null || arrayOfSURLs == null) {
			throw new IllegalArgumentException(
				"unable to get the statuses, null arguments: requestToken="
					+ requestToken + " arrayOfSURLs=" + arrayOfSURLs);
		}
		Map<TSURL, TReturnStatus> surlsStatuses = SurlStatusManager.getSurlsStatus(
			requestToken, arrayOfSURLs.getArrayList());
		if (surlsStatuses.isEmpty()) {
			log.info("No one of the requested surls found for the provided token");
			throw new RequestUnknownException(
				"No one of the requested surls found for the provided token");
		}
		return surlsStatuses;
	}

	public static ArrayOfTSURLReturnStatus prepareSurlsReturnStatus(
		Map<TSURL, TReturnStatus> surlStastuses) {

		ArrayOfTSURLReturnStatus surlReturnStatuses = new ArrayOfTSURLReturnStatus(
			surlStastuses.size());
		for (Entry<TSURL, TReturnStatus> surlStatus : surlStastuses.entrySet()) {
			surlReturnStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(
				surlStatus.getKey(), surlStatus.getValue()));
		}
		return surlReturnStatuses;
	}

	public static ArrayOfTSURLReturnStatus prepareSurlsReturnStatus(
		Map<TSURL, TReturnStatus> surlStastuses, ArrayOfSURLs arrayOfUserSURLs) {

		ArrayOfTSURLReturnStatus surlReturnStatuses = new ArrayOfTSURLReturnStatus(
			surlStastuses.size());
		for (TSURL surl : arrayOfUserSURLs.getArrayList()) {
			TReturnStatus status = surlStastuses.get(surl);
			if (status == null) {
				status = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
					"Invalid SURL");
			}
			surlReturnStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(surl,
				status));
		}
		return surlReturnStatuses;
	}

	public static void printRequestOutcome(TReturnStatus status,
		InputData inputData, String srmCommand) {

		if (inputData != null) {
			if (inputData instanceof ManageFileTransferRequestFilesInputData) {
				CommandHelper.printRequestOutcome(srmCommand, log, status, inputData,
					((ManageFileTransferRequestFilesInputData) inputData)
						.getRequestToken(),
					((ManageFileTransferRequestFilesInputData) inputData)
						.getArrayOfSURLs().asStringList());
			} else {
				if (inputData instanceof ManageFileTransferFilesInputData) {
					CommandHelper.printRequestOutcome(srmCommand, log, status, inputData,
						((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs()
							.asStringList());
				} else {
					if (inputData instanceof ManageFileTransferRequestInputData) {
						CommandHelper.printRequestOutcome(srmCommand, log, status,
							inputData, ((ManageFileTransferRequestInputData) inputData)
								.getRequestToken());
					} else {
						CommandHelper.printRequestOutcome(srmCommand, log, status,
							inputData);
					}
				}

			}
		} else {
			CommandHelper.printRequestOutcome(srmCommand, log, status);
		}
	}

}
