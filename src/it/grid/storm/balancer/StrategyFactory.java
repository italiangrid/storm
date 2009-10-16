package it.grid.storm.balancer;

import it.grid.storm.balancer.ftp.GFTPLoadStrategy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StrategyFactory {

    public static <E extends Node> Strategy<E> getStrategy(StrategyType type, List<E> pool) {
        switch(type) {
        case RANDOM: return new RandomStrategy<E>(pool);
        case ROUNDROBIN: return new RoundRobinStrategy<E>(pool);
        case WEIGHT: return new WeightStrategy<E>(pool);
        case GFTPLOAD: return new GFTPLoadStrategy<E>(pool);
        }
        throw new AssertionError("StrategyFactory: Unknown op: ");
     
    }

}
