package it.grid.storm.synchcall.directory;

import it.grid.storm.srm.types.TReturnStatus;

public class InvalidMvOutputAttributeException extends Exception {
    private boolean nullStat = true;

    public InvalidMvOutputAttributeException(TReturnStatus stat) {
        nullStat = (stat == null);
    }

    public String toString() {
        return "nullStatus = " + nullStat;
    }

}
