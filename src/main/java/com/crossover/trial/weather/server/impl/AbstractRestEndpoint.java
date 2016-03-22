package com.crossover.trial.weather.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.Repository;
import com.crossover.trial.weather.model.Statistics;

/**
 * This is a base class for all Rest Endpoint
 * 
 * @author Michele Mastrogiovanni
 */
public class AbstractRestEndpoint {

	// Logger user in the Endpoints
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());

	/**
	 * Initialization of the system
	 */
	static {
		Repository.getInstance().reset();
		Statistics.getInstance().reset();
	}
	
	/**
	 * Manage internal error
	 * 
	 * @param e Exception raised
	 * @return Response with the right error code
	 */
	protected Response getResponseByException(WeatherException e) {
		LOGGER.log(Level.INFO, "Validation error: " + e.getMessage());
		return Response.status(e.getStatus()).entity(e.getMessage()).build();
	}

}
