/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.du;

import java.time.Instant;
import java.util.List;

import com.google.common.base.Preconditions;

import it.grid.storm.space.DUResult;

public class DiskUsageUtils {

  private DiskUsageUtils() {}

  public static long parseSize(List<String> output) {

    Preconditions.checkNotNull(output, "Null output received");
    Preconditions.checkArgument(!output.isEmpty(), "Empty output provided as input");

    // get last output line
    String outputLine = output.get(output.size() - 1);
    String sizeStr = outputLine.split("\\s")[0];
    return Long.parseLong(sizeStr);
  }

  public static DUResult getResult(String absPath, Instant start, Instant end,
      List<String> output) {

    Preconditions.checkNotNull(output, "Null output received");

    if (output.isEmpty()) {
      return DUResult.failure(absPath, start, end, "empty output");
    }

    try {

      long size = DiskUsageUtils.parseSize(output);
      return DUResult.success(absPath, start, end, size);

    } catch (NumberFormatException e) {

      return DUResult.failure(absPath, start, end, "NumberFormatException on parsing du output");
    }
  }

}
