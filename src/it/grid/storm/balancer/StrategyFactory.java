/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
