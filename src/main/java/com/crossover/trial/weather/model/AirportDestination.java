package com.crossover.trial.weather.model;

public enum AirportDestination {

	EUROPE("E"),
	US_CANADA("A"),
	SOUTH_AMERICA("S"),
	AUSTRALIA("O"),
	NEW_ZEALAND("Z"),
	NONE("N"),
	UNKNOWN("U");
		
	private String code;

	private AirportDestination(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static AirportDestination findByCode(String code) {
		for ( AirportDestination destination : AirportDestination.values() ) {
			if ( destination.getCode().equals(code)) {
				return destination;
			}
		}
		return null;
	}

}
