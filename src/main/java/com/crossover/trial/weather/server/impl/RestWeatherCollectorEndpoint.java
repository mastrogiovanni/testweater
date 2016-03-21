package com.crossover.trial.weather.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.WeatherException;
import com.crossover.trial.weather.entity.AirportData;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.entity.Repository;
import com.crossover.trial.weather.server.WeatherCollectorEndpoint;
import com.google.gson.Gson;
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

	/** shared gson json to object factory */
	public final static Gson gson = new Gson();
	
	@Override
	public Response ping() {
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@Override
	public Response updateWeather(String iataCode, String pointType, String datapointJson) {
		try {
			Repository.getInstance().addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
		} catch (JsonSyntaxException e) {
			LOGGER.log(Level.SEVERE, "Cannot add datapoint: bad request", e);
			Response.status(Response.Status.BAD_REQUEST).entity("Cannot add datapoint: bad request").build();
		} catch (WeatherException e) {
			LOGGER.log(Level.SEVERE, "Cannot add datapoint: " + e.getMessage(), e);
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Cannot add datapoint: " + e.getMessage())
					.build();
		}
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response getAirports() {
		return Response.status(Response.Status.OK).entity(Repository.getInstance().getAirports()).build();
	}

	@Override
	public Response getAirport(@PathParam("iata") String iata) {
		AirportData ad = Repository.getInstance().findAirportData(iata);
		return Response.status(Response.Status.OK).entity(ad).build();
	}

	@Override
	public Response addAirport(String iata, String latString, String longString) {
		try {
			Repository.getInstance().addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Latitude and Longitude must be valid numbers").build();
		}
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response deleteAirport(@PathParam("iata") String iata) {
		if (Repository.getInstance().deleteAirport(iata)) {
			return Response.status(Response.Status.OK).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@Override
	public Response exit() {
		System.exit(0);
		return Response.noContent().build();
	}

}
