package com.crossover.trial.weather.utility;

import org.apache.commons.lang3.StringUtils;

import com.crossover.trial.weather.exception.WeatherValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ValidationUtility {
	
	/** shared gson json to object factory */
	public final static Gson gson = new Gson();

	public static <T> T isA(Class<T> clazz, String property, String value) throws WeatherValidationException {
		try {
			return gson.fromJson(StringUtils.stripToNull(value), clazz);
		}
		catch ( JsonSyntaxException e ) {
			throw new WeatherValidationException("Error in parsing the " + property + ": input is malformed");
		}
	}

	public static double isADouble(String property, String value) throws WeatherValidationException {
		try {
			return Double.valueOf(StringUtils.stripToEmpty(value));
		}
		catch ( NumberFormatException e ) {
			throw new WeatherValidationException("Error in parsing the " + property + ": is not a number");
		}
	}

	public static void checkRadius(double radius) throws WeatherValidationException {
		if ( radius < 0 ) {
			throw new WeatherValidationException("radius cannot be negative");
		}
	}

	public static void checkLatitude(double latitude) throws WeatherValidationException {
		if ( latitude < -90 || latitude > 90 ) {
			throw new WeatherValidationException("the airport's latitude in degrees as a string [-90, 90]");
		}
	}

	public static void checkLongitude(double longitude) throws WeatherValidationException {
		if ( longitude < -180 || longitude > 180 ) {
			throw new WeatherValidationException("the airport's longitude in degrees as a string [-180, 180]");
		}
	}
	
	public static void checkIataCode(String iataCode) throws WeatherValidationException {
		iataCode = StringUtils.stripToEmpty(iataCode);
		if ( iataCode.length() != 3 ) {
			throw new WeatherValidationException("the airport's IATA codes is a string of 3 letter");
		}
	}

	public static void checkNotNull(String property, String value) throws WeatherValidationException {
		if ( StringUtils.isBlank(value)) {
			throw new WeatherValidationException("the property '" + property + "' cannot be an emtpy value");
		}
	}

}
