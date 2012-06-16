package it.grid.storm.balancer;

import java.util.EnumSet;

public enum BalancerStrategyType {
	
	RANDOM ("random",Weighted.NOWEIGHT), 
	ROUNDROBIN ("round-robin",Weighted.NOWEIGHT), 
	WEIGHT ("weight",Weighted.WEIGHTED),
	SMART_RR ("smart-rr",Weighted.NOWEIGHT), 
	METRIC1 ("metric-1",Weighted.NOWEIGHT), 
	METRIC2 ("metric-2",Weighted.NOWEIGHT);
	
	private enum Weighted { WEIGHTED, NOWEIGHT };
	
	private String name;
	private boolean withWeight;
	
	BalancerStrategyType(String name, Weighted w){
		this.name = name;
		if (w == Weighted.WEIGHTED) withWeight = true; 
		else withWeight=false;
	}
	
	@Override
    public String toString(){
        return name;
    }
	
	public boolean requireWeight() {
		return this.withWeight;
	}
	
    public static BalancerStrategyType getByValue(String name){
        for (final BalancerStrategyType element : EnumSet.allOf(BalancerStrategyType.class)) {
            if (element.toString().equals(name)) {
                return element;
            }
        }
        return null;
    }
    
}
