package it.grid.storm.info.du;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.Test;

import it.grid.storm.space.DUResult;
import it.grid.storm.space.ExitStatus;
import jersey.repackaged.com.google.common.collect.Lists;

public class DiskUsageUtilsTest {

  @Test
  public void testEmptyOutput() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);
    List<String> output = Lists.newArrayList();

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertThat(result.getAbsRootPath(), equalTo(ABS_PATH));
    assertThat(result.getStatus(), equalTo(ExitStatus.FAILURE));
    assertThat(result.getDetail(), equalTo("empty output"));
    assertThat(result.getDurationInMillis(), equalTo(fiveMinutes.toMillis()));
    assertThat(result.getSizeInBytes(), equalTo(-1L));

  }

  @Test
  public void testSuccessfulOutput() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);
    List<String> output = Lists.newArrayList("474839632 /storage/sa");

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertThat(result.getAbsRootPath(), equalTo(ABS_PATH));
    assertThat(result.getStatus(), equalTo(ExitStatus.SUCCESS));
    assertThat(result.getDetail(), equalTo(""));
    assertThat(result.getDurationInMillis(), equalTo(fiveMinutes.toMillis()));
    assertThat(result.getSizeInBytes(), equalTo(474839632L));

  }

  @Test
  public void testParseLongErrorOutput() {

    final String ABS_PATH = "/storage/sa";
    Instant start = Instant.now();
    Duration fiveMinutes = Duration.ofMinutes(5L);
    Instant end = start.plus(fiveMinutes);
    List<String> output = Lists.newArrayList("some error output message");

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertThat(result.getAbsRootPath(), equalTo(ABS_PATH));
    assertThat(result.getStatus(), equalTo(ExitStatus.FAILURE));
    assertThat(result.getDetail(), equalTo("NumberFormatException on parsing du output"));
    assertThat(result.getDurationInMillis(), equalTo(fiveMinutes.toMillis()));
    assertThat(result.getSizeInBytes(), equalTo(-1L));

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
