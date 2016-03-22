package com.crossover.trial.weather.utility;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.DataPointType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Utility to validate properties of the system
 * 
 * @author Michele Mastrogiovanni
 */
public class ValidationUtility {

	/** shared gson json to object factory */
	public final static Gson gson = new Gson();

	/**
	 * Parse and check if normalied value of a property is a DataPointType.
	 * 
	 * @param property Property to parse
	 * @param value Current value of property
	 * @return Parsed DataPointType
	 * @throws WeatherException Error in converting normalized value to enum
	 */
	public static DataPointType isADataPoint(String property, String value) throws WeatherException {
		DataPointType dp = DataPointType.valueOf(StringUtils.stripToEmpty(value).toUpperCase());
		if ( dp == null ) {
			throw new WeatherException("Error in parsing the " + property + ": it is not a valid DataPointType", Status.BAD_REQUEST);
		}
		return dp;
	}
	
	/**
	 * Check if a normalized string can be decoded as JSON object to a given type.
	 * 
	 * @param clazz Class to convert string to 
	 * @param property Property description
	 * @param value String to convert
	 * @return Converted object
	 * @throws WeatherException Impossible to parse/convert JSON object to type requested
	 */
	public static <T> T jsonIsA(Class<T> clazz, String property, String value) throws WeatherException {
		try {
			return gson.fromJson(StringUtils.stripToNull(value), clazz);
		}
		catch ( JsonSyntaxException e ) {
			throw new WeatherException("Error in parsing the " + property + ": input is malformed", Status.BAD_REQUEST);
		}
	}

	/**
	 * Verify that a normalized string (removing blank spaces) is a correct double value
	 * 
	 * @param property Property checking
	 * @param value Value in string (eventually with blank chars)
	 * @return Its double value
	 * @throws WeatherException Normalized string is not a number
	 */
	public static double isADouble(String property, String value) throws WeatherException {
		try {
			return Double.valueOf(StringUtils.stripToEmpty(value));
		}
		catch ( NumberFormatException e ) {
			throw new WeatherException("Error in parsing the " + property + ": is not a number", Status.BAD_REQUEST);
		}
	}

	/**
	 * Verify a radius value: it must be a not negative number
	 * 
	 * @param radius Radius
	 * @throws WeatherException Radius is negative
	 */
	public static void checkRadius(double radius) throws WeatherException {
		if ( radius < 0 ) {
			throw new WeatherException("radius cannot be negative", Status.BAD_REQUEST);
		}
	}

	/**
	 * Verify latitude range [-90, 90]
	 * 
	 * @param longitude Latitude
	 * @throws WeatherException Latitude is out of range
	 */
	public static void checkLatitude(double latitude) throws WeatherException {
		if ( latitude < -90 || latitude > 90 ) {
			throw new WeatherException("the airport's latitude in degrees as a string [-90, 90]", Status.BAD_REQUEST);
		}
	}

	/**
	 * Verify longitude range [-180, 180]
	 * 
	 * @param longitude Longitude
	 * @throws WeatherException Longitude is out of range
	 */
	public static void checkLongitude(double longitude) throws WeatherException {
		if ( longitude < -180 || longitude > 180 ) {
			throw new WeatherException("the airport's longitude in degrees as a string [-180, 180]", Status.BAD_REQUEST);
		}
	}
	
	/**
	 * String blank chartacters from IATA code, normalize it by putting uppercase
	 * and than verify that its length is 3.
	 * 
	 * @param iataCode de-normalized IATA code
	 * @return Normalized IATA code
	 * @throws WeatherException IATA code normalized is not 3 char length
	 */
	public static String checkIataCode(String iataCode) throws WeatherException {
		iataCode = StringUtils.stripToEmpty(iataCode).toUpperCase();
		if ( iataCode.length() != 3 ) {
			throw new WeatherException("the airport's IATA codes is a string of 3 letter", Status.BAD_REQUEST);
		}
		return iataCode;
	}

	/**
	 * Verify if a normalized property is empty.
	 * 
	 * @param property Property to check
	 * @param value Value to check
	 * @throws WeatherException Normalized property is Blank.
	 */
	public static void checkNotNull(String property, String value) throws WeatherException {
		if ( StringUtils.isBlank(value)) {
			throw new WeatherException("the property '" + property + "' cannot be an empty value", Status.BAD_REQUEST);
		}
	}

}
