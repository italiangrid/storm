/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;

public class InstrumentedBasicDataSource extends BasicDataSource {

  private final Timer getConnectionTimer;
  private final JmxReporter reporter;

  public InstrumentedBasicDataSource(String prefix, MetricRegistry registry) {
    instrument(prefix, registry, this);
    getConnectionTimer = registry.timer(name(prefix, "get-connection"));
    reporter = JmxReporter.forRegistry(registry).build();
    reporter.start();
  }

  /**
   * Instrument the given BasicDataSource instance with a series of timers and gauges.
   * 
   */
  public static void instrument(String prefix, MetricRegistry registry,
      final BasicDataSource datasource) {

    registry.register(name(prefix, "initial-size"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getInitialSize();
      }
    });
    registry.register(name(prefix, "max-idle"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMaxIdle();
      }
    });
    registry.register(name(prefix, "max-open-prepared-statements"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMaxOpenPreparedStatements();
      }
    });
    registry.register(name(prefix, "max-wait-millis"), new Gauge<Long>() {
      public Long getValue() {
        return datasource.getMaxWaitMillis();
      }
    });
    registry.register(name(prefix, "min-evictable-idle-time-millis"), new Gauge<Long>() {
      public Long getValue() {
        return datasource.getMinEvictableIdleTimeMillis();
      }
    });
    registry.register(name(prefix, "min-idle"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMinIdle();
      }
    });
    registry.register(name(prefix, "num-active"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getNumActive();
      }
    });
    registry.register(name(prefix, "max-total"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMaxTotal();
      }
    });
    registry.register(name(prefix, "num-idle"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getNumIdle();
      }
    });
    registry.register(name(prefix, "num-tests-per-eviction-run"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getNumTestsPerEvictionRun();
      }
    });
    registry.register(name(prefix, "time-between-eviction-runs-millis"), new Gauge<Long>() {
      public Long getValue() {
        return datasource.getTimeBetweenEvictionRunsMillis();
      }
    });
    registry.register(name(prefix, "percent-idle"), new RatioGauge() {
      @Override
      protected Ratio getRatio() {
        return Ratio.of(datasource.getNumIdle(), datasource.getMaxIdle());
      }
    });
    registry.register(name(prefix, "percent-active"), new RatioGauge() {
      @Override
      protected Ratio getRatio() {
        return Ratio.of(datasource.getNumActive(), datasource.getMaxTotal());
      }
    });
  }

  @Override
  public Connection getConnection() throws SQLException {
    final Timer.Context ctx = getConnectionTimer.time();
    try {
      return super.getConnection();
    } finally {
      ctx.stop();
    }
  }

  @Override
  public synchronized void close() throws SQLException {
    super.close();
    reporter.stop();
  }
}
