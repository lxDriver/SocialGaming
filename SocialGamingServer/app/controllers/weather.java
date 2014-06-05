package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import models.User;
import play.api.libs.json.*;

import org.jongo.MongoCollection;

import play.libs.Json;


import play.Logger;
import uk.co.panaxiom.playjongo.PlayJongo;
import webservices.HttpGet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;


/*
 * stores a string with the received data for handling on client
 * together with location and last update
 */
public class weather {

	@JsonProperty("_id")
    public String id;
	
	
	public Double[] loc = new Double[]{11.5833, 48.15};
	public Date date = new Date();
	public long timestamp;
	public String ergebnis;
	public static Double entfernung=1000.0;
	
	public static MongoCollection Weather() {
        MongoCollection userCollection =  PlayJongo.getCollection("weather");
        
        // make sure we use 2d indices on a sphere to use geospatial queries
        userCollection.ensureIndex("{loc: '2dsphere'}");
        return userCollection;
    }   
	
	public weather(String condition, long l, Double[] loc) {
		ergebnis = condition;
		this.timestamp = l;
		this.loc = loc;
	}
	
	public weather() {
		
	}
	public static void updateweather(Double[]loc) {
		Logger.info("updating weather");
		//java.lang.Math.abs(this.firstUserInteractionTimeStamp - this.secondUserInteractionTimeStamp) < 60000
		List<weather> results = Lists.newArrayList(Weather().find("{loc: {$geoNear : {$geometry : {type: 'Point', " +
				"coordinates: # }, $maxDistance: # }}}", loc, entfernung).limit(100).as(weather.class));
	//	List<weather> results = Lists.newArrayList(Weather().find().as(weather.class));
		Logger.info("found "+results.size()+ "entrys");
		
			HttpGet weather = new HttpGet();
				
				String result;
				try {
					Logger.info("requesting weather data");
					result = weather.getweather(String.valueOf(loc[0]), String.valueOf(loc[1]));
					Logger.info("got weather data"+result);
					Logger.info("trying to save");
					Weather().save(new weather(result, System.currentTimeMillis(), loc));
				} catch (MalformedURLException e) {
					
					Logger.info("malformdedurl");
					e.printStackTrace();
				} catch (IOException e) {
					Logger.info("io exception");
					
					e.printStackTrace();
				}
				//ObjectNode ende = result;
				Logger.info("updated");
				
		
	}
	
	public static weather getweather(Double[]loc) {
		 //return Weather().findOne("{latitude: #, longitude#}", latitude, longitude).as(weather.class);
			Logger.info("asking for weather in db");
		 //List<weather> results = Lists.newArrayList(Weather().find("{loc: {$geoNear : {$geometry : {type: 'Point', " +
	      //          "coordinates: # }, $maxDistance: # }}}", loc, entfernung).limit(100).as(weather.class));
		 List<weather> results = Lists.newArrayList(Weather().find().as(weather.class));
			Logger.info("got "+results.size()+ "entrys");
		 if(results.size()<1||java.lang.Math.abs(results.get(0).timestamp - System.currentTimeMillis()) < 600000) {
			 updateweather(loc);
			 return getweather(loc);
		 } else {
			 return results.get(0);
		 }

		 
		 
		
		 
	}

	
	
}
