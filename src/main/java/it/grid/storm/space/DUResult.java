package it.grid.storm.space;

import java.util.Date;

public class DUResult {

    private long sizeInBytes;
    private String absRootPath;
    private long durationTimeInMillisec;
    private Date startTime;
    private ExitCode cmdResult;

    /**
     * @param size
     * @param absRootPath
     * @param startTime
     * @param durationTime
     * @param cmdResult
     */
    public DUResult(long sizeInBytes, String absRootPath, Date startTime, long durationTimeInMillisec,
            ExitCode cmdResult) {

        super();
        this.sizeInBytes = sizeInBytes;
        this.absRootPath = absRootPath;
        this.durationTimeInMillisec = durationTimeInMillisec;
        this.startTime = startTime;
        this.cmdResult = cmdResult;
    }

    public DUResult(DUResult duResult) {
        this(duResult.getSizeInBytes(), duResult.getAbsRootPath(), duResult.getStartTime(),
                duResult.getDurationTimeInMillisec(), duResult.getCmdResult());
    }

    /**
     * @return the size
     */
    public final long getSizeInBytes() {

        return sizeInBytes;
    }

    /**
     * @return the absRootPath
     */
    public final String getAbsRootPath() {

        return absRootPath;
    }

    /**
     * @return the durationTime
     */
    public final long getDurationTimeInMillisec() {

        return durationTimeInMillisec;
    }

    /**
     * @return the startTime
     */
    public final Date getStartTime() {

        return startTime;
    }

    public boolean isSuccess() {

        return (this.cmdResult.equals(ExitCode.SUCCESS));
    }

    public boolean isPoisoned() {

        return (this.cmdResult.equals(ExitCode.POISON_PILL));
    }

    /**
     * @return the cmdResult
     */
    public final ExitCode getCmdResult() {

        return cmdResult;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("DUResult [size=");
        builder.append(sizeInBytes);
        builder.append(", absRootPath=");
        builder.append(absRootPath);
        builder.append(", durationTime=");
        builder.append(durationTimeInMillisec);
        builder.append(", startTime=");
        builder.append(startTime);
        builder.append(", cmdResult=");
        builder.append(cmdResult);
        builder.append("]");
        return builder.toString();
    }

}
