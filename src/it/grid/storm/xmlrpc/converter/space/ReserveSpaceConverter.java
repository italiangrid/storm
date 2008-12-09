/**
 * This class represents the Type Converter for space Reservation function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the space Reservation Manager
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.xmlrpc.converter.space;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.InvalidReserveSpaceInputDataAttributesException;
import it.grid.storm.synchcall.data.space.ReserveSpaceInputData;
import it.grid.storm.synchcall.data.space.ReserveSpaceOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.griduser.GridUserManager;

public class ReserveSpaceConverter implements Converter {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public ReserveSpaceConverter()
    {};

    /** This method return a SpaceResData created from input Hashtable structure of an xmlrpc spaceReservation v2.1 call.
     *  SpaceResData can be used to invoke SpaceResevation Manager
     */
    public InputData convertToInputData(Map inputParam)    {

        log.debug("reserveSpaceConverter :Call received :Creation of SpaceResData = "+inputParam.size());
        log.debug("reserveSpaceConverter: Input Structure toString: "+inputParam.toString());

        ReserveSpaceInputData inputData = null;

        String memberName = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        /* (2) userSpaceTokenDescription */
        memberName = new String("userSpaceTokenDescription");
        String spaceAlias = (String) inputParam.get(memberName);
        if (spaceAlias == null) spaceAlias = new String("");

        /* (3) retentionPolicyInfo */
        TRetentionPolicyInfo retentionPolicyInfo = TRetentionPolicyInfo.decode(inputParam, TRetentionPolicyInfo.PNAME_retentionPolicyInfo);

        /* (4) desiredSizeOfTotalSpace */
        TSizeInBytes desiredSizeOfTotalSpace = TSizeInBytes.decode(inputParam, TSizeInBytes.PNAME_DESIREDSIZEOFTOTALSPACE);

        /* (5) desiredSizeOfGuaranteedSpace */
        TSizeInBytes desiredSizeOfGuaranteedSpace = TSizeInBytes.decode(inputParam, TSizeInBytes.PNAME_DESIREDSIZEOFGUARANTEEDSPACE);

        /* (6) desiredLifetimeOfReservedSpace */
        TLifeTimeInSeconds desiredLifetimeOfReservedSpace = TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_DESIREDLIFETIMEOFRESERVEDSPACE);

        /* (7) arrayOfExpectedFileSizes */
        ArrayOfTSizeInBytes arrayOfExpectedFileSizes = ArrayOfTSizeInBytes.decode(inputParam, ArrayOfTSizeInBytes.PNAME_arrayOfExpectedFileSizes);

        /* (8) storageSystemInfo */
        ArrayOfTExtraInfo storageSystemInfo;
        try {
            storageSystemInfo = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        }
        catch (InvalidArrayOfTExtraInfoAttributeException e) {
            storageSystemInfo = null;
        }

        /* (9) transferParameters */
        TTransferParameters transferParameters = TTransferParameters.decode(inputParam, TTransferParameters.PNAME_transferParameters);

        /* Creation of SpaceResInputData */
        try {
            inputData = new ReserveSpaceInputData(guser, spaceAlias, retentionPolicyInfo, desiredSizeOfTotalSpace,
                                              desiredSizeOfGuaranteedSpace, desiredLifetimeOfReservedSpace,
                                              arrayOfExpectedFileSizes, storageSystemInfo, transferParameters);
        }
        catch (InvalidReserveSpaceInputDataAttributesException e) {
            log.debug("Error Creating inputData for SpaceReservationManager: " + e);
        }

        // Return Space Reservation Data Created
        return inputData;

    }


    public Map convertFromOutputData(OutputData data)
    {
        log.debug("reserveSpaceConverter :Call received :Creation of XMLRPC Output Structure! ");

        //Creation of new Hashtable to return
        Map outputParam = new HashMap();

        ReserveSpaceOutputData outputData = (ReserveSpaceOutputData) data;

        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getStatus();
        returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        /* (2) requestToken */
        /* Actually we are not planning an asynchronous version of ReserveSpace (in theory not needed for StoRM).
         * Therefor this parameter is not set.
         */

        /* (3) estimatedProcessingTime */
        // TODO: in the future (actually the FE is predisposed to decode this value as an int).

        /* (4) retentionPolocyInfo */
        TRetentionPolicyInfo retentionPolicyInfo = outputData.getRetentionPolicyInfo();
        if (retentionPolicyInfo != null) retentionPolicyInfo.encode(outputParam, TRetentionPolicyInfo.PNAME_retentionPolicyInfo);

        /* (5) sizeOfTotalReservedSpace */
        TSizeInBytes sizeOfTotalReservedSpace = outputData.getTotalSize();
        if (sizeOfTotalReservedSpace != null) {
            if (!(sizeOfTotalReservedSpace.isEmpty())) {
                sizeOfTotalReservedSpace.encode(outputParam, TSizeInBytes.PNAME_SIZEOFTOTALRESERVEDSPACE);
            }
        }

        /* (6) sizeOfGuaranteedReservedSpace */
        TSizeInBytes sizeOfGuaranteedReservedSpace = outputData.getGuaranteedSize();
        if (sizeOfGuaranteedReservedSpace != null) {
            if (!(sizeOfGuaranteedReservedSpace.isEmpty())) {
                sizeOfGuaranteedReservedSpace.encode(outputParam, TSizeInBytes.PNAME_SIZEOFGUARANTEEDRESERVEDSPACE);
            }
        }

        /* (7) lifetimeOfReservedSpace */
        TLifeTimeInSeconds lifetimeOfReservedSpace = outputData.getLifeTimeInSeconds();
        if (lifetimeOfReservedSpace != null) {
            if (!(lifetimeOfReservedSpace.isEmpty())) {
                lifetimeOfReservedSpace.encode(outputParam, TLifeTimeInSeconds.PNAME_LIFETIMEOFRESERVEDSPACE);
            }
        }

        /* (8) spaceToken */
        TSpaceToken spaceToken = outputData.getSpaceToken();
        if (spaceToken != null) spaceToken.encode(outputParam, TSpaceToken.PNAME_SPACETOKEN);

        log.debug(outputParam.toString());

        return outputParam;
    }
}
