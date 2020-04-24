package it.grid.storm.info.du;

import static it.grid.storm.space.ExitCode.EMPTY_OUTPUT;
import static it.grid.storm.space.ExitCode.FAILURE;
import static it.grid.storm.space.ExitCode.IO_ERROR;
import static it.grid.storm.space.ExitCode.SUCCESS;

import java.util.Date;
import java.util.List;

import it.grid.storm.space.DUResult;
import it.grid.storm.space.ExitCode;

public class DiskUsageUtils {

  private DiskUsageUtils() {}

  private static DUResult getResult(ExitCode code, String absPath, long size, long start, long end) {

    return new DUResult(size, absPath, new Date(start), end - start, code);
  }

  public static DUResult getIOErrorResult(String absPath, long start, long end) {

    return getResult(IO_ERROR, absPath, -1, start, end);
  }

  public static DUResult getSuccessResult(String absPath, long size, long start, long end) {

    return getResult(SUCCESS, absPath, size, start, end);
  }

  public static DUResult getEmptyOutputResult(String absPath, long start, long end) {

    return getResult(EMPTY_OUTPUT, absPath, 0, start, end);
  }

  public static DUResult getFailureResult(String absPath, long start, long end) {

    return getResult(FAILURE, absPath, -1, start, end);
  }

  public static long parseSize(List<String> output) {

    if (output.isEmpty()) {
      return -1;
    }
    // get last output line
    String outputLine = output.get(output.size() - 1);
    String sizeStr = outputLine.split("\\s")[0];
    return Long.parseLong(sizeStr);
  }

  public static DUResult getResult(String absPath, long start, long end, List<String> output) {

    if (output.isEmpty()) {
      return DiskUsageUtils.getEmptyOutputResult(absPath, start, end);
    }

    try {

      long size = DiskUsageUtils.parseSize(output);
      return DiskUsageUtils.getSuccessResult(absPath, size, start, end);

    } catch (NumberFormatException e) {

      return DiskUsageUtils.getFailureResult(absPath, start, end);
    }
  }

}
