package com.crossover.trial.weather.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.exception.WeatherValidationException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.Repository;
import com.crossover.trial.weather.server.WeatherCollectorEndpoint;
import com.crossover.trial.weather.utility.ValidationUtility;
import com.google.gson.JsonSyntaxException;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {

	public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());
	
	@Override
	public Response ping() {
		LOGGER.log(Level.FINE, "Ping received");
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@Override
	public Response updateWeather(String iataCode, String pointType, String datapointJson) {

		LOGGER.log(Level.FINE, "Requesting update weater information");

		try {
			
			iataCode = ValidationUtility.checkIataCode(iataCode);
			
			ValidationUtility.checkNotNull("point type", pointType);
			
			DataPoint dataPoint = ValidationUtility.jsonIsA(DataPoint.class, "request body", datapointJson);
			
			Repository.getInstance().addDataPoint(iataCode, pointType, dataPoint);
			
		} catch (WeatherValidationException e) {
			LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (WeatherException e) {
			LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		
		return Response.status(Response.Status.OK).build();
		
	}

	@Override
	public Response getAirports() {
		return Response
				.status(Response.Status.OK)
				.entity(Repository.getInstance().getAirports())
				.build();
	}

	@Override
	public Response getAirport(@PathParam("iata") String iataCode) {
		
		try {
			
			iataCode = ValidationUtility.checkIataCode(iataCode);

			AirportData ad = Repository.getInstance().findAirportData(iataCode);
			if ( ad == null ) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			else {
				return Response.status(Response.Status.OK).entity(ad).build();
			}

		} catch (WeatherValidationException e) {
			LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		
	}

	@Override
	public Response addAirport(String iataCode, String latString, String longString) {
		
		try {
			
			ValidationUtility.checkNotNull("latitude", latString);
			ValidationUtility.checkNotNull("longitude", longString);
			
			double latitude = ValidationUtility.isADouble("latitude", latString);
			double longitude = ValidationUtility.isADouble("longitude", longString);
			
			iataCode = ValidationUtility.checkIataCode(iataCode);
			ValidationUtility.checkLatitude(latitude);
			ValidationUtility.checkLongitude(longitude);
			
			Repository.getInstance().addAirport(iataCode, latitude, longitude);
			
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Latitude and Longitude must be valid numbers").build();
		} catch (WeatherValidationException e) {
			LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response deleteAirport(@PathParam("iata") String iataCode) {
		
		try {
			
			iataCode = ValidationUtility.checkIataCode(iataCode);
			
			if (Repository.getInstance().deleteAirport(iataCode)) {
				return Response.status(Response.Status.OK).build();
			}
			
			return Response.status(Response.Status.NOT_FOUND).build();
			
		} catch (WeatherValidationException e) {
			LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		
	}

	@Override
	public Response exit() {
		System.exit(0);
		return Response.noContent().build();
	}

}
