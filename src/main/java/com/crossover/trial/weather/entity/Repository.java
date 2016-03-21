package com.crossover.trial.weather.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.exception.WeatherValidationException;
import com.crossover.trial.weather.utility.DistanceUtility;
import com.crossover.trial.weather.utility.ValidationUtility;

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
	 * All known airports infos. Key corresponds with iata of the related
	 * airport.
	 */
	private Map<String, AirportData> airportData;

	/**
	 * Atmospheric information for each airport. Key corresponds with iata of
	 * the related airport.
	 */
	private Map<String, AtmosphericInformation> atmosphericInformation;

	public static Repository getInstance() {
		if (instance == null) {
			instance = new Repository();
		}
		return instance;
	}

	private Repository() {
		airportData = new TreeMap<>();
		atmosphericInformation = new TreeMap<>();
		clear();
	}

	public synchronized void clear() {
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
	 * @param iataCode
	 *            the 3 letter IATA code
	 * @param pointType
	 *            the point type {@link DataPointType}
	 * @param dp
	 *            a datapoint object holding pointType data
	 *
	 * @throws WeatherException
	 *             if the update can not be completed
	 */
	public synchronized void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
		AtmosphericInformation ai = atmosphericInformation.get(iataCode);
		if (ai == null) {
			throw new WeatherException("Airport not found");
		}
		update(ai, DataPointType.valueOf(pointType.toUpperCase()), dp);
	}

	public synchronized Stream<AirportData> getAirportsFromIataInRadius(String iataCode, double radius)
			throws WeatherValidationException {

		ValidationUtility.checkRadius(radius);

		ValidationUtility.checkIataCode(iataCode);

		AirportData origin = airportData.get(iataCode);

		if (origin == null) {
			return Stream.empty();
		}

		return airportData.values().stream().filter(x -> DistanceUtility.calculateDistance(origin, x) <= radius);

	}

	public synchronized AtmosphericInformation getAtmosphericInformation(String iataCode) {
		return atmosphericInformation.get(iataCode);
	}

	/**
	 * @return Return list of iata code of contained airports
	 */
	public synchronized Set<String> getAirports() {
		return new TreeSet<String>(airportData.keySet());
	}

	/**
	 * Add a new known airport to our list.
	 *
	 * @param iataCode
	 *            3 letter code
	 * @param latitude
	 *            in degrees
	 * @param longitude
	 *            in degrees
	 *
	 * @return the added airport
	 */
	public synchronized AirportData addAirport(String iataCode, double latitude, double longitude) {

		AirportData ad = new AirportData.Builder(iataCode).withLat(latitude).withLon(longitude).build();

		AirportData old = null;

		old = airportData.get(iataCode);
		if (old != null) {
			return old;
		}
		airportData.put(iataCode, ad);

		AtmosphericInformation ai = new AtmosphericInformation();
		atmosphericInformation.put(iataCode, ai);

		return ad;

	}

	/**
	 * Delete airport
	 * 
	 * @return True if airport was deleted
	 */
	public synchronized boolean deleteAirport(String iataCode) {
		boolean ret = airportData.remove(iataCode) != null;
		if (ret == false) {
			return ret;
		}
		atmosphericInformation.remove(iataCode);
		return ret;
	}

	/**
	 * Given an iataCode find the airport data
	 *
	 * @param iataCode
	 *            as a string
	 * @return airport data or null if not found
	 */
	public synchronized AirportData findAirportData(String iataCode) {
		return airportData.get(iataCode);
	}

	public synchronized int getDataSize() {
		int datasize = 0;
		for (AtmosphericInformation ai : atmosphericInformation.values()) {
			// we only count recent readings
			if (ai.hasSomeValue()) {
				// updated in the last day
				if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
					datasize++;
				}
			}
		}
		return datasize;
	}

	/**
	 * update atmospheric information with the given data point for the given
	 * point type
	 *
	 * @param ai
	 *            the atmospheric information object to update
	 * @param pointType
	 *            the data point type as a string
	 * @param dp
	 *            the actual data point
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
			if (dp.getMean() >= 0 && dp.getMean() < 100) {
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
