/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.du;

import com.google.common.collect.Lists;
import it.grid.storm.space.DUResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskUsageExecCommand {

  private static final Logger LOG = LoggerFactory.getLogger(DiskUsageExecCommand.class);

  private final String absPath;

  public DiskUsageExecCommand(String absPath) {
    this.absPath = absPath;
  }

  public DUResult execute() throws IOException {

    Instant start = Instant.now();

    List<String> command = Lists.newArrayList("du", "--bytes", "--summarize", absPath);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Starting {} ...", String.join(" ", command));
    }

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);

    Process process = builder.start();

    List<String> output = getCommandOutput(process.getInputStream());

    if (LOG.isDebugEnabled()) {
      LOG.debug("DU output:\n{}", String.join("\n", output));
    }

    Instant end = Instant.now();

    return DiskUsageUtils.getResult(absPath, start, end, output);
  }

  private List<String> getCommandOutput(InputStream is) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    List<String> output = Lists.newArrayList();
    String line = null;
    while ((line = reader.readLine()) != null) {
      output.add(line);
    }

    reader.close();
    return output;
  }
}
