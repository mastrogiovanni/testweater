package com.crossover.trial.weather.exception;

import javax.ws.rs.core.Response;

public class WeatherException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private Response.Status status;

	public WeatherException(String message, Response.Status status) {
		super(message);
		this.status = status;
	}
	
	public Response.Status getStatus() {
		return status;
	}

}
