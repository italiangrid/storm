/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.space;

import java.time.Duration;
import java.time.Instant;

public class DUResult {

  private final long sizeInBytes;
  private final String absRootPath;
  private final Instant start;
  private final Instant end;
  private final ExitStatus status;
  private final String detail;
  

  /**
   * @param size
   * @param absRootPath
   * @param startTime
   * @param durationTime
   * @param cmdResult
   */
  private DUResult(long sizeInBytes, String absRootPath, Instant start, Instant end,
      ExitStatus status, String detail) {

    this.sizeInBytes = sizeInBytes;
    this.absRootPath = absRootPath;
    this.start = start;
    this.end = end;
    this.status = status;
    this.detail = detail;
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
   * @return the start @Instant
   */
  public final Instant getStart() {

    return start;
  }

  /**
   * @return the end @Instant
   */
  public final Instant getEnd() {

    return end;
  }

  /**
   * @return the end @Instant
   */
  public final long getDurationInMillis() {

    return Duration.between(start, end).toMillis();
  }

  public boolean isSuccess() {

    return ExitStatus.SUCCESS.equals(status);
  }

  /**
   * @return the exit status
   */
  public final ExitStatus getStatus() {

    return status;
  }

  /**
   * @return the exit status detailed message
   */
  public final String getDetail() {

    return detail;
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("DUResult [size=");
    builder.append(sizeInBytes);
    builder.append(", absRootPath=");
    builder.append(absRootPath);
    builder.append(", start=");
    builder.append(start);
    builder.append(", end=");
    builder.append(end);
    builder.append(", status=");
    builder.append(status);
    builder.append(", detail=");
    builder.append(detail);
    builder.append("]");
    return builder.toString();
  }

  public static DUResult success(String absRootPath, Instant start, Instant end, long sizeInBytes) {
    return get(absRootPath, start, end, sizeInBytes, ExitStatus.SUCCESS, "");
  }

  public static DUResult failure(String absRootPath, Instant start, Instant end, String errorMessage) {
    return get(absRootPath, start, end, -1, ExitStatus.FAILURE, errorMessage);
  }

  public static DUResult get(String absRootPath, Instant start, Instant end, long sizeInBytes, ExitStatus status, String statusMessage) {
    return new DUResult(sizeInBytes, absRootPath, start, end, status, statusMessage);
  }
}
