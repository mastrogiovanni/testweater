package com.crossover.trial.weather.entity;

import java.util.HashMap;
import java.util.Map;

public class Statistics {
	
	private static Statistics instance;
	
	/**
	 * Internal performance counter to better understand most requested
	 * information, this map can be improved but for now provides the basis for
	 * future performance optimizations. Due to the stateless deployment
	 * architecture we don't want to write this to disk, but will pull it off
	 * using a REST request and aggregate with other performance metrics
	 * {@link #ping()}
	 */
	private Map<String, Integer> requestFrequency = new HashMap<String, Integer>();
	private Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

	public static Statistics instance() {
		if ( instance == null ) {
			instance = new Statistics();
		}
		return instance;
	}
	
	private Statistics() {
		requestFrequency.clear();
		radiusFreq.clear();
	}
	
    /**
     * Records information about how often requests are made
     *
     * @param iata an iata code
     * @param radius query radius
     */
    public void updateRequestFrequency(String iata, Double radius) {
        requestFrequency.put(iata, requestFrequency.getOrDefault(iata, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0) + 1);
    }

	public Map<String, Double> getIataFreq() {
		
		Map<String, Double> freq = new HashMap<>();
		
		// fraction of queries
		for (String iata : requestFrequency.keySet()) {
			double frac = (double) requestFrequency.getOrDefault(iata, 0) / requestFrequency.size();
			freq.put(iata, frac);
		}
		
		return freq;
	}
	
	public int[] getRadiusFreqHistogram() {
		
		int m = radiusFreq.keySet().stream().max(Double::compare).orElse(1000.0).intValue() + 1;

		int[] hist = new int[m];
		for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
			int i = e.getKey().intValue() % 10;
			hist[i] += e.getValue();
		}
		
		return hist;
		
	}

}
