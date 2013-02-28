package it.grid.storm.space;



public enum ExitCode {
    SUCCESS(0), IO_ERROR(1), INTERRUPTED(2), TIMEOUT(3), UNDEFINED(-1), PARTIAL_SUCCESS(4), POISON_PILL(99), EMPTY_OUTPUT(5), FAILURE(6);

    private int code;

    ExitCode(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }

    public static ExitCode getExitCode(int code) {
        ExitCode result = ExitCode.UNDEFINED;
        for (ExitCode ec : ExitCode.values()) {
            if (ec.getCode() == code) {
                result = ec;
                break;
            }
        }
        return result;
    }
}