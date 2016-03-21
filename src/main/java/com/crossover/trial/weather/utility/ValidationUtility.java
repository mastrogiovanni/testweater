package com.crossover.trial.weather.utility;

import org.apache.commons.lang3.StringUtils;

import com.crossover.trial.weather.exception.WeatherValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * All validation operations are here
 * 
 * @author Michele Mastrogiovanni
 */
public class ValidationUtility {
	
	/** shared gson json to object factory */
	public final static Gson gson = new Gson();

	/**
	 * Check if a normalized string can be decoded as JSON object to a given type.
	 * 
	 * @param clazz Class to convert string to 
	 * @param property Property description
	 * @param value String to convert
	 * @return Converted object
	 * @throws WeatherValidationException Impossible to parse/convert JSON object to type requested
	 */
	public static <T> T jsonIsA(Class<T> clazz, String property, String value) throws WeatherValidationException {
		try {
			return gson.fromJson(StringUtils.stripToNull(value), clazz);
		}
		catch ( JsonSyntaxException e ) {
			throw new WeatherValidationException("Error in parsing the " + property + ": input is malformed");
		}
	}

	/**
	 * Verify that a normalized string (removing blank spaces) is a correct double value
	 * 
	 * @param property Property checking
	 * @param value Value in string (eventually with blank chars)
	 * @return Its double value
	 * @throws WeatherValidationException Normalized string is not a number
	 */
	public static double isADouble(String property, String value) throws WeatherValidationException {
		try {
			return Double.valueOf(StringUtils.stripToEmpty(value));
		}
		catch ( NumberFormatException e ) {
			throw new WeatherValidationException("Error in parsing the " + property + ": is not a number");
		}
	}

	/**
	 * Verify a radius value: it must be a not negative number
	 * 
	 * @param radius Radius
	 * @throws WeatherValidationException Radius is negative
	 */
	public static void checkRadius(double radius) throws WeatherValidationException {
		if ( radius < 0 ) {
			throw new WeatherValidationException("radius cannot be negative");
		}
	}

	/**
	 * Verify latitude range [-90, 90]
	 * 
	 * @param longitude Latitude
	 * @throws WeatherValidationException Latitude is out of range
	 */
	public static void checkLatitude(double latitude) throws WeatherValidationException {
		if ( latitude < -90 || latitude > 90 ) {
			throw new WeatherValidationException("the airport's latitude in degrees as a string [-90, 90]");
		}
	}

	/**
	 * Verify longitude range [-180, 180]
	 * 
	 * @param longitude Longitude
	 * @throws WeatherValidationException Longitude is out of range
	 */
	public static void checkLongitude(double longitude) throws WeatherValidationException {
		if ( longitude < -180 || longitude > 180 ) {
			throw new WeatherValidationException("the airport's longitude in degrees as a string [-180, 180]");
		}
	}
	
	/**
	 * String blank chartacters from IATA code, normalize it by putting uppercase
	 * and than verify that its length is 3.
	 * 
	 * @param iataCode de-normalized IATA code
	 * @return Normalized IATA code
	 * @throws WeatherValidationException IATA code normalized is not 3 char length
	 */
	public static String checkIataCode(String iataCode) throws WeatherValidationException {
		iataCode = StringUtils.stripToEmpty(iataCode).toUpperCase();
		if ( iataCode.length() != 3 ) {
			throw new WeatherValidationException("the airport's IATA codes is a string of 3 letter");
		}
		return iataCode;
	}

	/**
	 * Verify if a normalized property is empty.
	 * 
	 * @param property Property to check
	 * @param value Value to check
	 * @throws WeatherValidationException Normalized property is Blank.
	 */
	public static void checkNotNull(String property, String value) throws WeatherValidationException {
		if ( StringUtils.isBlank(value)) {
			throw new WeatherValidationException("the property '" + property + "' cannot be an empty value");
		}
	}

}
