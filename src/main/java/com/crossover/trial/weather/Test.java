package com.crossover.trial.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import com.csvreader.CsvReader;

public class Test {

	public static void main(String[] args) throws IOException {
		
		InputStream in = Test.class.getResourceAsStream("/airports.dat");

		CsvReader reader = new CsvReader(in, Charset.forName("UTF-8"));
		reader.setDelimiter(',');
		reader.setRecordDelimiter('\n');
		
		while ( reader.readRecord() ) {
			
			String[] values = reader.getValues();
			
			// Main city served by airport. May be spelled differently from name.
			String city = get(values, 0);
			
			// Country or territory where airport is located.
			String country = get(values, 1);
			
			// 3-letter FAA code or IATA code (blank or "" if not assigned)
			String iata = StringUtils.stripToEmpty(get(values, 2));
			
			// 4-letter ICAO code (blank or "" if not assigned)
			String icao = StringUtils.stripToEmpty(get(values, 3));
			
			// Decimal degrees, up to 6 significant digits. Negative is South, positive is North.
			String latitude = get(values, 4);
			
			// Decimal degrees, up to 6 significant digits. Negative is West, positive is East.
			String longitude = get(values, 5);
			
			// In feet
			String altitude = get(values, 6);
			
			// Hours offset from UTC. Fractional hours are expressed as decimals. (e.g. India is 5.5)
			String timezone = get(values, 7);
			
			// One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown) 
			String dst = get(values, 8);

		}
		
		
	}
	
	private static String get(String[] list, int index) {
		if ( index >= list.length ) {
			return null;
		}
		return StringUtils.stripToNull(list[index]);
	}

}
