package com.crossover.trial.weather.server.impl;

import static com.crossover.trial.weather.utility.ValidationUtility.checkIataCode;
import static com.crossover.trial.weather.utility.ValidationUtility.checkLatitude;
import static com.crossover.trial.weather.utility.ValidationUtility.checkLongitude;
import static com.crossover.trial.weather.utility.ValidationUtility.checkNotNull;
import static com.crossover.trial.weather.utility.ValidationUtility.isADataPoint;
import static com.crossover.trial.weather.utility.ValidationUtility.isADouble;
import static com.crossover.trial.weather.utility.ValidationUtility.jsonIsA;

import java.util.logging.Level;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.model.Repository;
import com.crossover.trial.weather.server.WeatherCollectorEndpoint;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint extends AbstractRestEndpoint implements WeatherCollectorEndpoint {

	@Override
	public Response ping() {
		LOGGER.log(Level.FINE, "Ping received");
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@Override
	public Response updateWeather(String iataCode, String pointType, String datapointJson) {

		LOGGER.log(Level.FINE, "Requesting update weater information");

		try {
			
			iataCode = checkIataCode(iataCode);
			
			checkNotNull("point type", pointType);
			
			DataPoint dataPoint = jsonIsA(DataPoint.class, "request body", datapointJson);
			
			DataPointType dp = isADataPoint("point type", pointType);
			
			Repository.getInstance().addDataPoint(iataCode, dp, dataPoint);
			
		} catch (WeatherException e) {
			return getResponseByException(e);
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
			
			iataCode = checkIataCode(iataCode);

			AirportData ad = Repository.getInstance().findAirportData(iataCode);
			if ( ad != null ) {
				return Response.status(Response.Status.OK).entity(ad).build();
			}

			return Response.status(Response.Status.OK).build();

		} catch (WeatherException e) {
			return getResponseByException(e);
		}
		
	}

	@Override
	public Response addAirport(String iataCode, String latString, String longString) {
		
		try {
			
			checkNotNull("latitude", latString);
			checkNotNull("longitude", longString);
			
			double latitude = isADouble("latitude", latString);
			double longitude = isADouble("longitude", longString);
			
			iataCode = checkIataCode(iataCode);
			
			checkLatitude(latitude);
			
			checkLongitude(longitude);
			
			Repository.getInstance().addAirport(iataCode, latitude, longitude);
			
		} catch (WeatherException e) {
			return getResponseByException(e);
		}
		
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response deleteAirport(@PathParam("iata") String iataCode) {
		
		try {
			
			iataCode = checkIataCode(iataCode);
			
			if (Repository.getInstance().deleteAirport(iataCode)) {
				return Response.status(Response.Status.OK).build();
			}
			
			return Response.status(Response.Status.BAD_REQUEST).build();
			
		} catch (WeatherException e) {
			return getResponseByException(e);
		}
		
	}

	@Override
	public Response exit() {
		System.exit(0);
		return Response.noContent().build();
	}

}
