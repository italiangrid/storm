package it.grid.storm.info.du;

import static it.grid.storm.space.ExitStatus.FAILURE;
import static it.grid.storm.space.ExitStatus.SUCCESS;
import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import it.grid.storm.space.DUResult;

public class DiskUsageUtilsTest {

  @Test
  public void testEmptyOutput() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);
    List<String> output = Lists.newArrayList();

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertEquals(result.getAbsRootPath(), ABS_PATH);
    assertEquals(result.getStatus(), FAILURE);
    assertEquals(result.getDetail(), "empty output");
    assertEquals(result.getDurationInMillis(), fiveMinutes.toMillis());
    assertEquals(result.getSizeInBytes(), -1L);

  }

  @Test
  public void testSuccessfulOutput() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);
    List<String> output = Lists.newArrayList("474839632 /storage/sa");

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertEquals(result.getAbsRootPath(), ABS_PATH);
    assertEquals(result.getStatus(), SUCCESS);
    assertEquals(result.getDetail(), "");
    assertEquals(result.getDurationInMillis(), fiveMinutes.toMillis());
    assertEquals(result.getSizeInBytes(), 474839632L);

  }

  @Test
  public void testParseLongErrorOutput() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);
    List<String> output = Lists.newArrayList("some error output message");

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertEquals(result.getAbsRootPath(), ABS_PATH);
    assertEquals(result.getStatus(), FAILURE);
    assertEquals(result.getDetail(), "NumberFormatException on parsing du output");
    assertEquals(result.getDurationInMillis(), fiveMinutes.toMillis());
    assertEquals(result.getSizeInBytes(), -1L);

  }

  @Test(expected = NullPointerException.class)
  public void testParseNullOutputError() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);

    DiskUsageUtils.getResult(ABS_PATH, start, end, null);
  }

}
