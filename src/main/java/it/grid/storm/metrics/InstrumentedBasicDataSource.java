package it.grid.storm.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class InstrumentedBasicDataSource extends BasicDataSource {

  private Timer getConnectionTimer = null;
  private JmxReporter reporter = null;

  public InstrumentedBasicDataSource(String prefix, MetricRegistry registry) {
    instrument(prefix, registry, this);
    getConnectionTimer = registry.timer(name(prefix, "getconnection"));
    reporter = JmxReporter.forRegistry(registry).build();
    reporter.start();
  }

  /**
   * Instrument the given BasicDataSource instance with a series of timers and gauges.
   * 
   */
  public static void instrument(String prefix, MetricRegistry registry, final BasicDataSource datasource) {

    registry.register(name(prefix, "initialsize"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getInitialSize();
      }
    });
    registry.register(name(prefix, "maxidle"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMaxIdle();
      }
    });
    registry.register(name(prefix, "maxopenpreparedstatements"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMaxOpenPreparedStatements();
      }
    });
    registry.register(name(prefix, "maxwaitmillis"), new Gauge<Long>() {
      public Long getValue() {
        return datasource.getMaxWaitMillis();
      }
    });
    registry.register(name(prefix, "minevictableidletimemillis"), new Gauge<Long>() {
      public Long getValue() {
        return datasource.getMinEvictableIdleTimeMillis();
      }
    });
    registry.register(name(prefix, "minidle"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getMinIdle();
      }
    });
    registry.register(name(prefix, "numactive"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getNumActive();
      }
    });
    registry.register(name(prefix, "numidle"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getNumIdle();
      }
    });
    registry.register(name(prefix, "numtestsperevictionrun"), new Gauge<Integer>() {
      public Integer getValue() {
        return datasource.getNumTestsPerEvictionRun();
      }
    });
    registry.register(name(prefix, "timebetweenevictionrunsmillis"), new Gauge<Long>() {
      public Long getValue() {
        return datasource.getTimeBetweenEvictionRunsMillis();
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
