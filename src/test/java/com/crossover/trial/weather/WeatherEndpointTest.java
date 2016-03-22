package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.Repository;
import com.crossover.trial.weather.server.WeatherCollectorEndpoint;
import com.crossover.trial.weather.server.WeatherQueryEndpoint;
import com.crossover.trial.weather.server.impl.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.server.impl.RestWeatherQueryEndpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WeatherEndpointTest {

    private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();

    private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();

    private Gson _gson = new Gson();

    private DataPoint _dp;
    
    @Before
    public void setUp() throws Exception {
    	Repository.getInstance().reset();
        _dp = new DataPoint.Builder()
        		.withCount(10)
        		.withFirst(10)
        		.withMedian(20)
        		.withLast(30)
        		.withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
        _query.weather("BOS", "0").getEntity();
    }

    @Test
    public void testQueryPing() throws Exception {
        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testQueryWeather() throws Exception {
    	
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), _dp);
        
        ais = (List<AtmosphericInformation>) _query.weather("XXX", "0").getEntity();
        assertNotNull(ais);
        assertEquals(0, ais.size());
    }
    
    @Test
    public void testGetNearby() throws Exception {
        // check datasize response
        _update.updateWeather("JFK", "wind", _gson.toJson(_dp));
        _dp = new DataPoint.Builder().from(_dp).withMean(40).build();
        _update.updateWeather("EWR", "wind", _gson.toJson(_dp));
        _dp = new DataPoint.Builder().from(_dp).withMean(30).build();
        _update.updateWeather("LGA", "wind", _gson.toJson(_dp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
        assertEquals(3, ais.size());
    }

    @Test
    public void testUpdateWeather() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
                .withCount(10)
                .withFirst(10)
                .withMedian(20)
                .withLast(30)
                .withMean(22)
                .build();
        _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        _query.weather("BOS", "0").getEntity();

        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint cloudCoverDp = new DataPoint.Builder()
                .withCount(4)
                .withFirst(10)
                .withMedian(60)
                .withLast(100)
                .withMean(50)
                .build();
        _update.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
        
        windDp = new DataPoint.Builder()
                .withCount(10)
                .withFirst(10)
                .withMedian(20)
                .withLast(30)
                .withMean(22).build();
        
        Response response = _update.updateWeather("PIPPO", "wind", _gson.toJson(windDp));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        response = _update.updateWeather("pi", "wind", _gson.toJson(windDp));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        response = _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
    }
        
    @Test
    public void testAddAirport() {
    	
    	Response response = null;
    	
    	response = _update.addAirport(null, null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.addAirport("pi", null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.addAirport("pip", null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.addAirport("pip", "23b", null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.addAirport("pip", "23", "wer");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.addAirport("pip", "23,23", "12.45");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.addAirport("pip", "23.23", "12.45");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    }
    
    @Test
    public void testDeleteAirport() {
    	
    	Response response = null;
    	
    	response = _update.deleteAirport("Pippo");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.deleteAirport("PPP");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    	response = _update.deleteAirport("BOS");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    }
    
    @Test
    public void testGetAirport() {
    	
    	Response response = _update.getAirport("BOS");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

    	response = _update.getAirport("XXX");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());

    }
    

}