package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.entity.AtmosphericInformation;
import com.crossover.trial.weather.entity.DataPoint;
import com.crossover.trial.weather.entity.Repository;
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
    	Repository.getInstance().clear();
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
    public void testPing() throws Exception {
        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), _dp);
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
    public void testUpdate() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
                .withCount(10)
                .withFirst(10)
                .withMedian(20)
                .withLast(30)
                .withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        _query.weather("BOS", "0").getEntity();

        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint cloudCoverDp = new DataPoint.Builder()
                .withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        _update.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }
    
    @Test
    public void testBadUpdate() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
                .withCount(10)
                .withFirst(10)
                .withMedian(20)
                .withLast(30)
                .withMean(22).build();
        
        Response response = _update.updateWeather("PIPPO", "wind", _gson.toJson(windDp));
        Assert.assertNotEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = _update.updateWeather("pi", "wind", _gson.toJson(windDp));
        Assert.assertNotEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    }
    
    @Test
    public void testBadAirportInsert() {
    	
    	Response response = null;
    	
    	response = _update.addAirport(null, null, null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    	
    }
    

}