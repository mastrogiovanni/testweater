package com.crossover.trial.weather.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.crossover.trial.weather.WeatherException;
import com.crossover.trial.weather.utility.DistanceUtility;

/**
 * Singleton class used to maintain all data
 * 
 * @author Michele Mastrogiovanni
 */
public class Repository {

	/**
	 * Singleton instance of repository
	 */
	private static Repository instance;
	
	/** 
	 * All known airports infos.
	 * Key corresponds with iata of the related airport.
	 */
	private Map<String, AirportData> airportData;

	/**
	 * Atmospheric information for each airport.
	 * Key corresponds with iata of the related airport.
	 */
	private Map<String, AtmosphericInformation> atmosphericInformation;

	public static Repository getInstance() {
		if ( instance == null ) {
			instance = new Repository();
		}
		return instance;
	}
	
	private Repository() {
		airportData = new TreeMap<>();
		atmosphericInformation = new TreeMap<>();
		clear();
	}
	
	public void clear() {
		
		airportData.clear();
		atmosphericInformation.clear();
		
		// Initializing data
    	addAirport("BOS", 42.364347, -71.005181);
    	addAirport("EWR", 40.6925, -74.168667);
    	addAirport("JFK", 40.639751, -73.778925);
    	addAirport("LGA", 40.777245, -73.872608);
    	addAirport("MMU", 40.79935, -74.4148747);
		
	}

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
    	synchronized (atmosphericInformation) {
            AtmosphericInformation ai = atmosphericInformation.get(iataCode);
            if (ai == null) {
            	throw new WeatherException("Airport not found");
            }
            update(ai, DataPointType.valueOf(pointType.toUpperCase()), dp);
		}
    }
    
    public List<AirportData> getAirportsFromIataInRadius(String iataCode, double radius) throws WeatherException {
   		if ( radius < 0 ) {
			throw new WeatherException("Radius cannot be negative");
		}
   		synchronized (airportData) {
    		List<AirportData> result = new ArrayList<AirportData>(airportData.size());
    		AirportData origin = airportData.get(iataCode);
    		for ( AirportData airport : airportData.values() ) {
    			if ( DistanceUtility.calculateDistance(origin, airport) <= radius ) {
    				result.add(airport);
    			}
    		}
    		return result;
    	}
    }
    
    public AtmosphericInformation getAtmosphericInformation(String iataCode) {
    	synchronized (atmosphericInformation) {
        	return atmosphericInformation.get(iataCode);
		}
    }

    /**
     * @return Return list of iata code of contained airports
     */
    public Set<String> getAirports() {
    	synchronized (airportData) {
        	return Collections.unmodifiableSet(airportData.keySet());
		}
    }
    
    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     * @throws WeatherException 
     */
    public AirportData addAirport(String iataCode, double latitude, double longitude) {
    	
        AirportData ad = new AirportData.Builder(iataCode)
        		.withLat(latitude)
        		.withLon(longitude)
        		.build();

        AirportData old = null;
        
    	synchronized (airportData) {
    		old = airportData.get(iataCode);
    		if ( old != null ) {
    			return old;
    		}
            airportData.put(iataCode, ad);
		}

    	synchronized (atmosphericInformation) {
            AtmosphericInformation ai = new AtmosphericInformation();
            atmosphericInformation.put(iataCode, ai);
		}
    	
        return ad;
    	
    }
    
    /**
     * Delete airport
     * @return True if airport was deleted
     */
    public boolean deleteAirport(String iataCode) {
    	boolean ret = false;
    	synchronized (airportData) {
    		ret = airportData.remove(iataCode) != null;
		}
    	if ( ret == false ) {
    		return ret;
    	}
    	synchronized (atmosphericInformation) {
        	atmosphericInformation.remove(iataCode);
		}
    	return ret;
    }

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    public AirportData findAirportData(String iataCode) {
    	return airportData.get(iataCode);
    }
    
    public int getDataSize() {
		int datasize = 0;
    	synchronized (atmosphericInformation) {
    		for (AtmosphericInformation ai : atmosphericInformation.values()) {
    			// we only count recent readings
    			if (ai.hasSomeValue()) {
    				// updated in the last day
    				if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
    					datasize++;
    				}
    			}
    		}
    	}
		return datasize;
    }

	/**
	 * update atmospheric information with the given data point for the given point type
	 *
	 * @param ai the atmospheric information object to update
	 * @param pointType the data point type as a string
	 * @param dp the actual data point
	 */
	private void update(AtmosphericInformation ai, DataPointType dptype, DataPoint dp) throws WeatherException {

		switch (dptype) {
		case WIND:
			if (dp.getMean() >= 0) {
				ai.setWind(dp);
				ai.setLastUpdateTime(System.currentTimeMillis());
				return;
			}
			break;

		case CLOUDCOVER:
			if (dp.getMean() >= 0 && dp.getMean() < 100) {
				ai.setCloudCover(dp);
				ai.setLastUpdateTime(System.currentTimeMillis());
				return;
			}
			break;
			
		case HUMIDTY:
			if (dp.getMean() >= 0 && dp.getMean() < 100) {
				ai.setHumidity(dp);
				ai.setLastUpdateTime(System.currentTimeMillis());
				return;
			}
			break;
			
		case PRECIPITATION:
			if (dp.getMean() >=0 && dp.getMean() < 100) {
				ai.setPrecipitation(dp);
				ai.setLastUpdateTime(System.currentTimeMillis());
				return;
			}
			break;
			
		case PRESSURE:
			if (dp.getMean() >= 650 && dp.getMean() < 800) {
				ai.setPressure(dp);
				ai.setLastUpdateTime(System.currentTimeMillis());
				return;
			}
			break;
			
		case TEMPERATURE:
			if (dp.getMean() >= -50 && dp.getMean() < 100) {
				ai.setTemperature(dp);
				ai.setLastUpdateTime(System.currentTimeMillis());
				return;
			}
			break;
			
		}

		throw new WeatherException("couldn't update atmospheric data");

	}

}
