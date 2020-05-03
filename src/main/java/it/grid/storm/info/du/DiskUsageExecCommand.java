package it.grid.storm.info.du;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.space.DUResult;

public class DiskUsageExecCommand {

  private static final Logger log = LoggerFactory.getLogger(DiskUsageExecCommand.class);

  private String absPath;

  public DiskUsageExecCommand(String absPath) {
    this.absPath = absPath;
  }

  public DUResult execute() throws IOException {

    long start = ZonedDateTime.now().toInstant().toEpochMilli();

    List<String> command = Lists.newArrayList("du", "--bytes", "--summarize", absPath);

    if (log.isDebugEnabled()) {
      log.debug("Starting {} ...", String.join(" ", command));
    }

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);

    Process process = builder.start();

    List<String> output = getCommandOutput(process.getInputStream());

    if (log.isDebugEnabled()) {
      log.debug("DU output:\n{}", String.join("\n", output));
    }

    long end = ZonedDateTime.now().toInstant().toEpochMilli();

    return DiskUsageUtils.getResult(absPath, start, end, output);
  }

  private List<String> getCommandOutput(InputStream is) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    List<String> output = Lists.newArrayList();
    String line = null;
    while ((line = reader.readLine()) != null) {
      output.add(line);
    }

    IOUtils.closeQuietly(reader);
    return output;
  }
}
