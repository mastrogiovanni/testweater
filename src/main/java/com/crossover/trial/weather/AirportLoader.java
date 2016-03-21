package com.crossover.trial.weather;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.csvreader.CsvReader;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * @author code test administrator
 */
public class AirportLoader {

    /** end point to supply updates */
    private WebTarget collect;

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        collect = client.target("http://localhost:9090/collect");
    }

    public void upload(InputStream airportDataStream) throws IOException {
    	
		CsvReader reader = new CsvReader(airportDataStream, Charset.forName("UTF-8"));
		reader.setDelimiter(',');
		reader.setRecordDelimiter('\n');
		
		while ( reader.readRecord() ) {
			
			String[] values = reader.getValues();
			
			// String index = get(values, 0);

			// Main city served by airport. May be spelled differently from name.
			// String description = get(values, 1);

			// Main city served by airport. May be spelled differently from name.
			// String city = get(values, 2);
			
			// Country or territory where airport is located.
			// String country = get(values, 3);
			
			// 3-letter FAA code or IATA code (blank or "" if not assigned)
			String iata = StringUtils.stripToEmpty(get(values, 4));
			
			// 4-letter ICAO code (blank or "" if not assigned)
			// String icao = StringUtils.stripToEmpty(get(values, 5));
			
			// Decimal degrees, up to 6 significant digits. Negative is South, positive is North.
			String latitude = get(values, 6);
			
			// Decimal degrees, up to 6 significant digits. Negative is West, positive is East.
			String longitude = get(values, 7);
			
			// In feet
			// String altitude = get(values, 8);
			
			// Hours offset from UTC. Fractional hours are expressed as decimals. (e.g. India is 5.5)
			// String timezone = get(values, 9);
			
			// One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown) 
			// String dst = get(values, 10);

	        WebTarget path = collect.path("/airport/" + iata + "/" + latitude + "/" + longitude);
	        Response post = path.request().post(Entity.entity("", "application/json"));
	        if ( post.getStatus() != Response.Status.OK.getStatusCode() ) {
	        	System.out.println("Error in saving airport: " + post.getEntity().toString());
	        	break;
	        }
	        // dump(post, path);

		}
		
    }

//    private void dump(Response response, WebTarget path) {
//        System.out.println(path.getUri() + ": (" + response.getStatusInfo() + ") - " + response.readEntity(String.class));
//    }

	private String get(String[] list, int index) {
		if ( index >= list.length ) {
			return null;
		}
		return StringUtils.stripToNull(list[index]);
	}

    public static void main(String args[]) throws IOException {
        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }
        AirportLoader al = new AirportLoader();
        al.upload(new FileInputStream(airportDataFile));
        System.exit(0);
    }
}
