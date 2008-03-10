package it.grid.storm.asynch;

import it.grid.storm.srm.types.TReturnStatus;

/**
 * Class that represents the reply received from issuing an srmPutDone command.
 *
 * @author  EGRID ICTP Trieste
 * @version 1.0
 * @date    August 2006
 */
public class SRMPutDoneReply {

    private TReturnStatus overallRetStat = null; //overall request return status

    /**
     * Constructor that requires the overall TReturnStatus of the reply.
     */
    public SRMPutDoneReply(TReturnStatus overallRetStat) throws InvalidPutDoneReplyAttributeException {
        if (overallRetStat==null) throw new InvalidPutDoneReplyAttributeException();
        this.overallRetStat = overallRetStat;
    }

    /**
     * Method that returns the overll status of the request.
     */
    public TReturnStatus overallRetStat() {
        return overallRetStat;
    }

    public String toString() {
        return "SRMPutDoneReply: overall TReturnStatus is "+overallRetStat.toString();
    }
}
