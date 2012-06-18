package it.grid.storm.balancer.ftp;


public enum FtpReplyCode {

	GENERIC_PRELIMINARY(199),
    COMMAND_OK(200),
    SYSTEM_STATUS(211),
    SERVICE_READY(220),
    SERVICE_CLOSING(221),
    GENERIC_POSITIVE(299),
    GENERIC_POSITIVE_INTERMEDIATE(399),
    SERVICE_NOT_AVAILABLE(421),
    GENERIC_TRANSIENT_NEGATIVE(499),
    NOT_LOGGED_IN(530),
    GENERIC_PERMANENT_NEGATIVE(599),
    GENERIC_PROTECTED(699),
    UNKNOWN_ERROR(999);
    
    private final int code;

    FtpReplyCode(int code) {
        this.code = code;
    }

  
    public int getValue() {
        return code;
    }

    public static boolean isPositivePreliminary(int code) {
        return code >= 100 && code < 200;
    }

    public static boolean isPositiveCompletion(int code) {
        return code >= 200 && code < 300;
    }

    public static boolean isPositiveIntermediate(int code) {
        return code >= 300 && code < 400;
    }


    public static boolean isTransientNegative(int code) {
        return code >= 400 && code < 500;
    }

    public static boolean isPermanentNegative(int code) {
        return code >= 500 && code < 600;
    }

    public static boolean isProtectedReply(int code) {
        return code >= 600 && code < 700;
    }

 
    /**
     * Static utility method to convert a value into a FtpReplyCode.
     *
     * @param v the value to convert
     * @return the <code>FtpReplyCode</code> associated with the value.
     */
    public static FtpReplyCode find(int v) {
        for (FtpReplyCode code : FtpReplyCode.values()) {
            if (code.getValue() == v) {
                return code;
            }
        }
        if (isPositivePreliminary(v)) return GENERIC_PRELIMINARY;
        if (isPositiveCompletion(v)) return GENERIC_POSITIVE;
        if (isPositiveIntermediate(v)) return GENERIC_POSITIVE_INTERMEDIATE;
        if (isTransientNegative(v)) return GENERIC_TRANSIENT_NEGATIVE;
        if (isPermanentNegative(v)) return GENERIC_PERMANENT_NEGATIVE;
        return UNKNOWN_ERROR;
    }


	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.name()+" ("+this.code+")";
	}

    

}
