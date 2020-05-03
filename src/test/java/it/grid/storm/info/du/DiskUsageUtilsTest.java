package it.grid.storm.info.du;

import static it.grid.storm.space.ExitCode.EMPTY_OUTPUT;
import static it.grid.storm.space.ExitCode.FAILURE;
import static it.grid.storm.space.ExitCode.SUCCESS;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import it.grid.storm.space.DUResult;
import jersey.repackaged.com.google.common.collect.Lists;

public class DiskUsageUtilsTest {

  @Test
  public void testEmptyOutput() {

    final String ABS_PATH = "/storage/sa";
    long start = System.nanoTime();
    long end = start + 1000;
    List<String> output = Lists.newArrayList();

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertThat(result.getAbsRootPath(), equalTo(ABS_PATH));
    assertThat(result.getCmdResult().getCode(), equalTo(EMPTY_OUTPUT.getCode()));
    assertThat(result.getDurationTimeInMillisec(), equalTo(end - start));
    assertThat(result.getSizeInBytes(), equalTo(0L));

  }

  @Test
  public void testSuccessfulOutput() {

    final String ABS_PATH = "/storage/sa";
    long start = System.nanoTime();
    long end = start + 1000;
    List<String> output = Lists.newArrayList("474839632 /storage/sa");

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertThat(result.getAbsRootPath(), equalTo(ABS_PATH));
    assertThat(result.getCmdResult().getCode(), equalTo(SUCCESS.getCode()));
    assertThat(result.getDurationTimeInMillisec(), equalTo(end - start));
    assertThat(result.getSizeInBytes(), equalTo(474839632L));

  }

  @Test
  public void testParseLongErrorOutput() {

    final String ABS_PATH = "/storage/sa";
    long start = System.nanoTime();
    long end = start + 1000;
    List<String> output = Lists.newArrayList("some error output message");

    DUResult result = DiskUsageUtils.getResult(ABS_PATH, start, end, output);
    assertThat(result.getAbsRootPath(), equalTo(ABS_PATH));
    assertThat(result.getCmdResult().getCode(), equalTo(FAILURE.getCode()));
    assertThat(result.getDurationTimeInMillisec(), equalTo(end - start));

  }

}
