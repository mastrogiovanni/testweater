package com.crossover.trial.weather.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {

    /** the three letter IATA code */
    private String iata;

    /** latitude value in degrees */
    private double latitude;

    /** longitude value in degrees */
    private double longitude;
    
    private AirportData() {}

    public String getIata() {
        return iata;
    }

    private void setIata(String iata) {
        this.iata = iata;
    }

    public double getLatitude() {
        return latitude;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
    
    public static class Builder {
    	
    	double lat;
        double lon;
        String iata;

        public Builder(String iata) {
        	this.iata = iata;
        }
        
        public Builder withLat(double lat) {
        	this.lat = lat;
            return this;
        }

        public Builder withLon(double lon) {
        	this.lon = lon;
            return this;
        }

        public AirportData build() {
        	AirportData ad = new AirportData();
        	ad.setIata(this.iata);
        	ad.setLatitude(this.lat);
        	ad.setLongitude(this.lon);
        	return ad;
        }
        
    }
}
