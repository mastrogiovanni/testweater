package com.crossover.trial.weather;

import java.io.IOException;

import com.crossover.trial.weather.utility.AirportLoader;

public class Pippo {

	public static void main(String[] args) {
		
		WeatherServer.Handler handler = new WeatherServer.Handler() {

			@Override
			public void launched() {

				WeatherClient client = new WeatherClient();
				
				try {
					AirportLoader.main(new String[]{ "/home/michele/Documents/Progetti/weather-dist/src/main/resources/airports.dat" });
				} catch (IOException e) {
					e.printStackTrace();
				}

				client.pingCollect();
				client.populate("wind", 0, 10, 6, 4, 20);

				client.query("BOS");
				client.query("JFK");
				client.query("EWR");
				client.query("LGA");
				client.query("MMU");
		        
				client.query("BOS", "pippo");
				client.query("BOS", "-23");

				client.pingQuery();

// 		        client.populate("wind", 0, 10, 6, 4, 20);
//				
//				client.pingCollect();
//				
//				client.pingQuery();
		        
		        client.exit();
				
			}
			
		};

		WeatherServer.start(handler);

	}

}
