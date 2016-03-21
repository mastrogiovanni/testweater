package com.crossover.trial.weather.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.Repository;
import com.crossover.trial.weather.entity.Statistics;
import com.crossover.trial.weather.exception.WeatherValidationException;
import com.crossover.trial.weather.server.WeatherQueryEndpoint;
import com.crossover.trial.weather.utility.ValidationUtility;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check
 * health stats. Currently, all data is held in memory. The end point deploys to
 * a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

	public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

	/** shared gson json to object factory */
	public static final Gson gson = new Gson();

	static {
		Repository.getInstance().clear();
	}

	/**
	 * Retrieve service health including total size of valid data points and
	 * request frequency information.
	 *
	 * @return health stats for the service as a string
	 */
	@Override
	public String ping() {

		Map<String, Object> healthStatus = new HashMap<>();

		healthStatus.put("datasize", Repository.getInstance().getDataSize());
		healthStatus.put("iata_freq", Statistics.getInstance().getIataFreq());
		healthStatus.put("radius_freq", Statistics.getInstance().getRadiusFreqHistogram());

		return gson.toJson(healthStatus);

	}

	/**
	 * Given a query in json format {'iata': CODE, 'radius': km} extracts the
	 * requested airport information and return a list of matching atmosphere
	 * information.
	 *
	 * @param iata
	 *            the iataCode
	 * @param radiusString
	 *            the radius in km
	 *
	 * @return a list of atmospheric information
	 */
	@Override
	public Response weather(String iataCode, String radiusString) {

		try {

			iataCode = ValidationUtility.checkIataCode(iataCode);

			double radius = ValidationUtility.isADouble("radius", radiusString);

			ValidationUtility.checkRadius(radius);

			// Update statistics on data
			Statistics.getInstance().updateRequestFrequency(iataCode, radius);

			// Result of atmosferical conditions
			List<AtmosphericInformation> result = new ArrayList<>();

			Repository.getInstance().getAirportsFromIataInRadius(iataCode, radius)
					.map(airport -> Repository.getInstance().getAtmosphericInformation(airport.getIata()))
					.filter(ai -> ai.hasSomeValue())
					.forEach(result::add);

			return Response.status(Response.Status.OK).entity(result).build();

		} catch (WeatherValidationException e) {
			LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

}
