package com.crossover.trial.weather.model;

/**
 * encapsulates sensor information for a particular location
 */
public class AtmosphericInformation {

	/** temperature in degrees celsius */
	private DataPoint temperature;

	/** wind speed in km/h */
	private DataPoint wind;

	/** humidity in percent */
	private DataPoint humidity;

	/** precipitation in cm */
	private DataPoint precipitation;

	/** pressure in mmHg */
	private DataPoint pressure;

	/** cloud cover percent from 0 - 100 (integer) */
	private DataPoint cloudCover;

	/** the last time this data was updated, in milliseconds since UTC epoch */
	private long lastUpdateTime;

	public DataPoint getTemperature() {
		return temperature;
	}

	void setTemperature(DataPoint temperature) {
		this.temperature = temperature;
	}

	public DataPoint getWind() {
		return wind;
	}

	void setWind(DataPoint wind) {
		this.wind = wind;
	}

	public DataPoint getHumidity() {
		return humidity;
	}

	void setHumidity(DataPoint humidity) {
		this.humidity = humidity;
	}

	public DataPoint getPrecipitation() {
		return precipitation;
	}

	void setPrecipitation(DataPoint precipitation) {
		this.precipitation = precipitation;
	}

	public DataPoint getPressure() {
		return pressure;
	}

	void setPressure(DataPoint pressure) {
		this.pressure = pressure;
	}

	public DataPoint getCloudCover() {
		return cloudCover;
	}

	void setCloudCover(DataPoint cloudCover) {
		this.cloudCover = cloudCover;
	}

	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public boolean hasSomeValue() {
		return getCloudCover() != null || getHumidity() != null || getPrecipitation() != null || getPressure() != null
				|| getTemperature() != null || getWind() != null;
	}

}
