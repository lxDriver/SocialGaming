package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jongo.MongoCollection;

import play.*;
import play.libs.Json;
import play.mvc.*;
import uk.co.panaxiom.playjongo.PlayJongo;
import util.GameConfiguration;
import views.html.*;
import views.html.defaultpages.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import play.mvc.BodyParser;
import models.*;

/**
 * This class contains all of the logic for the REST API used by the clients (webservice).
 * 
 * See conf/routes for the url and parameter encoding scheme.
 * 
 * @author Niklas Klügel
 *
 */
public class Application extends Controller {
	

	/**
	 * Index route: generates a HTNL site with the game statistics.
	 * This will be also shown in a widget in the Android application.
	 * 
	 * @return
	 */
    public static Result index() {
        return ok(index.render("SocialMon"));
    }
    
    
    static Random r = new Random();
     
    /**
     * On login, this will either update or insert a new user in the database
     * @param facebookAuthToken - Facebook auth token to request the user information from facebook on behalf of user
     * @param googleCDSToken - Google Cloud Messaging device token used to communicate via push service to the device
     * @param longitude - user position
     * @param latitude - user position
     * @return
     * @throws IOException 
     */
    public static Result loginUser(String facebookAuthToken, String googleCDSToken, Double longitude, Double latitude) throws IOException {
    	Result ret = null;
    	
    	Logger.info("looking up user...");
    	
    	User user = User.updateUserProfileFromLoginCredentials(facebookAuthToken, googleCDSToken, longitude, latitude);
    	
    	if(user != null) {
    		ObjectNode json = Json.newObject();
    		json.put("type", "server");
    		json.put("subtype", "login");
    		
    		try {
				Logger.info("try to send succes message to user");
    			user.sendMessage(json);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		ret = ok("logged in user "+user.name);
    	} else {
    		ret = badRequest("Error logging user in!");
    	}
    	
    	Logger.info("User is:\n "+user.name + " " +user.loc + " " + user.lastLogin);
    	
    	return ret;
    }
    
    /**
     * Returns nearby users of a user, given his/her Faceook ID. 
     * The maximum distance for this search is set in GameConfiguration.
     * 
     * @param facebookID
     * @return
     */
    public static Result getNearbyUsers(String facebookID) {
    	// sanity check: only allow a participant user to do a request
    	// limit number of returned results
    	
    	Result ret;
    	
    	Logger.info("Requesting users...");
    	
    	User user = User.findByFacebookID(facebookID);
    	
    	if(user != null && user.participatesInGame) {
    		Iterable<User> users = User.findUsersNearby(user.loc, GameConfiguration.MaxDistanceOfUserForNearbyUsers, GameConfiguration.MaxNumberOfReturnedUsers);
    		
    		if(users != null) {
    			ObjectNode searchResult = Json.newObject();
    			ArrayNode userArray = searchResult.arrayNode();
    			
    	    	for(User u: users) {
    	    		if(!u.facebookID.equals(user.facebookID) ){
    	    			ObjectNode userNode = Json.newObject();
    	    			
    	    			userNode.put("user",u.name);
    	    			userNode.put("facebookID", u.facebookID);
    	    			userNode.put("longitude", u.loc[0]);
    	    			userNode.put("latitude", u.loc[1]);
    	    			
    	    			userArray.add(userNode);
    	    		}
    	    	}
    	    	
    	    	// just add an array, if we actually have found users, otherwise just send back an empty json
    	    	if(userArray.size() > 0){
    	    		searchResult.put("users", userArray);
    	    	}
    	    	
    	    	ret = ok(searchResult);    			
    		} else {
    			
    			
    			ret = badRequest("Could not find other users");
    		}
    		
    	} else {
    		ret = badRequest("User not recognized");
    	}
    	
    	return ret;
    }

    /**
     * Updates the database entry of that user with the new location.
     * 
     * @param facebookID
     * @param longitude
     * @param latitude
     * @return
     * @throws IOException 
     * @throws MalformedURLException 
     */
    public static Result updateUserLocation(String facebookID, Double longitude, Double latitude) {
    	Result ret;    	    

    	User user = User.findByFacebookID(facebookID);
    	if(user != null) {
    		user.updateLocation(longitude, latitude);
    		Logger.info("Updated user position: "+facebookID+" > "+longitude+" , "+latitude);
    		
    		
    		ret = ok();
    	} else {
    		ret = badRequest("User does not exist!");
    	}
    	
    	return ret;
    }
    
    /**
     * This is called once a user requests a new game and, after game object creation, messages both users
     * to accept or abort a game.
     * 
     * Game objects also reside in the database, we are using the mongo-db object ID as simple
     * ID for a game directly = gameID.
     * 
     * The message sending logic is part of the game object.
     * 
     * @param facebookID
     * @return
     * @throws IOException 
     */
    
    public static Result requestNewGame(String facebookID) throws IOException{
    	Result ret;
    	Logger.info("requesting game: "+ facebookID);
    	User user = User.findByFacebookID(facebookID);
    	
		
    	
		User opponent = MonsterGame.findOpponent(user);
		
		
		if(opponent != null) {
			// this will not only add a new Game object to the database but also communicate
			// the request via Google Cloud Messaging to the opponents
			
			try {
				
				MonsterGame.createAndStartNewGame(user.facebookID, opponent.facebookID);
				Logger.info("succes");
				ret = ok();
				
			} catch (IOException e) {
				ret = badRequest("Some error occured");
				e.printStackTrace();
				
			}
			
			
			
		} else {
			ret = badRequest("No opponent found!");
			
		}
    	
    	return ret;
    }
    
    /**
     * This is called once a user decides NOT to play upon request.
     * 
     * This action sends messages.
     * 
     * @param gameID
     * @param facebookID
     * @return
     */
    public static Result abortGame(String gameID, String facebookID){
    	Result ret;
    	
    	MonsterGame game = MonsterGame.findByID(gameID);
    	
    	if(game != null){
    		
    		if(!game.isAborted()){
    			try {
					game.abort(facebookID);
					ret = ok();
					
				} catch (IOException e) {
					ret = badRequest("Some error occured");
					e.printStackTrace();
				}
    		}
    		
    		ret = ok();
    	} else {
    		ret = badRequest("Game does not exist!");
    	}
    	
    	return ret;
    }
    
    /**
     * Called to accept a game, the game object changes state to "established".
     * 
     * This action sends messages.
     * 
     * @param gameID
     * @param facebookID
     * @return
     */
    public static Result acceptGame(String gameID, String facebookID) {
    	Result ret;
    	//TODO facebook id + facebookid
    	MonsterGame game = MonsterGame.findByID(gameID);
    	
    	if(game != null){
    		
    		if(!game.isAborted()){
    			try {
					game.accept(facebookID);
					ret = ok();
					
				} catch (IOException e) {
					ret = badRequest("Some error occured");
					e.printStackTrace();
				}
    		}
    		
    		ret = ok();
    	} else {
    		ret = badRequest("Game does not exist!");
    	}
    	
    	return ret;
    }
    
    /**
     * Called when a user did the short time social interaction. 
     * If both users did this for the same game (-ID), then the 
     * winner will be determined and the game is finished.
     * 
     * This action sends messages.
     * 
     * @param gameID
     * @param facebookID
     * @return
     * @throws IOException 
     */
    
    public static Result interactionInGame(String gameID, String facebookID, String my_health, String enemy_health) throws IOException{
    	Result ret;
    	//TODO get health data
    	//int myhealth = 100;
    	//int enemyhealth = 100;
    	MonsterGame game = MonsterGame.findByID(gameID);
    	
    	
    	
    	if(game != null){
    		
    		if(!game.isAborted()){
    			//neue gesundheitswerte einsetzen
    	    	game.fight(facebookID, my_health, enemy_health);
    		}
    		
    		ret = ok();
    	} else {
    		ret = badRequest("Game does not exist!");
    	}
    	
    	return ret;
    }
    
    /**
     * This is used as an axaple to send messages from client to client using
     * the server. 
     * 
     * This action sends messages.
     * 
     * @param senderFacebookID
     * @param recipentFacebookID
     * @return
     */
    
    
    public static Result poke(String senderFacebookID, String recipentFacebookID){
    	Result ret;
    	
    	User sender = User.findByFacebookID(senderFacebookID);
    	User recipent= User.findByFacebookID(recipentFacebookID);
    	
    	if(sender != null && recipent != null && recipent.participatesInGame){
    		// we can do some other logic here, for now, we just send a 
    		// poke message without further testing (test if nearby, is friend, etc)
    		// the message will be kept alive for one hour in the GCM System
    		// and therefore delivered as soon as the recipent is online (within this hour)
    		
    		Logger.info("poke > " + sender.name + " -> " + recipent.name);
    		
    		try {
				recipent.sendMessageCached(PushMessages.createPokeMessage(sender), 3600);
				
				ret = ok();
				
			} catch (IOException e) {
				e.printStackTrace();
				
				ret = badRequest(); 
				
			}
    		
    	} else {
    		ret = badRequest("User does not exist!");
    	}
    	
    	return ret;
    }
    
    
    
    
    
    /*
     * create a monster in the database
     * if it already exists update the monster with name and level
     */
    public static Result AddMonster(String MonsterID, String Name, String Level, String FacebookID) {
    	Result ret;
    	Logger.info("trying to addmonster");
    	
    	try {
			
    		Monster.createMonster(MonsterID, Name, Level, FacebookID);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
    		Logger.info("adding monster");
    		
    	
    	ret = ok();
    
    return ret;
    }

    /*
     * returns one Monster given by the ID
     * may be useless but good for learning ;)
     */
    public static Result getMonster(String MonsterID, String facebookid) {

    	Result ret;
    	
    	
    	Logger.info("Requesting Monsters...");
    	ObjectNode searchResult = Json.newObject();
    	Monster result = Monster.findbyid(MonsterID, facebookid);
    	
    	
    	//nur etwas zurückgeben wenn wir etwas gefunden haben
    	if(result != null) {
    	searchResult.put("Monster", models.Monster.findbyid(MonsterID,facebookid).getName());
    	searchResult.put("off", result.getOffensive());
    	searchResult.put("off", result.getDefence());
    	
    	ret = ok(searchResult);
    	} else {
    		ret = badRequest("could not find Monster");
    	}
    		
    	
    	return ret;
    }
    
    /*
     * returns all monsters from one user
     */
    public static Result getmyMonsters(String FacebookID) throws IOException {
    	
    	Result ret;
    
    	
    	
    	Logger.info("Requesting Monsters for player: "+ FacebookID);
    	
    	
    		Iterable<Monster> Monsters = Monster.findmyMonsters(FacebookID);
    		Logger.info("Monsters found "+ Monsters.toString());
    		if(Monsters != null&&!Monsters.toString().equals("[]")) {
    			Logger.info("Monsters found");
    			ObjectNode searchResult = Json.newObject();
    			ArrayNode MonsterArray = searchResult.arrayNode();
    			
    	    	for(Monster m: Monsters) {
    	    		Logger.info("going into for");
    	    			if(m.id !=null&&m.id!=""&&m.id!="{}"&&m.getName() !=null&&m.getName()!=""&&m.getName()!="{}") {
    	    			Logger.info("getmymonsters went into if because entrys exist");
    	    			ObjectNode MonsterNode = Json.newObject();
    	    			Logger.info("name des monsters "+ m.getName());
    	    			MonsterNode.put("name",m.getName());
    	    			MonsterNode.put("level", m.getLevel());
    	    			
    	    			Logger.info("returning monster");
    	    			
    	    			MonsterArray.add(MonsterNode);
    	    			
    	    			} else {
    	    				Logger.info("adding monster");
    	    				AddMonster("001", "", "", FacebookID);
    	    				ret = getmyMonsters(FacebookID);
    	    			}
    	    			
    	    	} 
    	    	
    	    	// just add an array, if we actually have found users, otherwise just send back an empty json
    	    	if(MonsterArray.size() > 0){
    	    		searchResult.put("Monster", MonsterArray);
    	    	}
    	    	
    	    	ret = ok(searchResult);    			
    		
    		
    	} else {
    		Logger.info("trying to create monster with FacebookID" + FacebookID);
	    		AddMonster("001", "", "", FacebookID);
	    	ret = getmyMonsters(FacebookID);
    		//ret = badRequest("User not recognized");
    	}
    	
    	return ret;
    	
    }
    
    public static Result getweather(String facebookid) {
    	Result ret;
    	
    	User who = User.findByFacebookID(facebookid);
    	
    	ObjectNode searchResult = Json.newObject();
    	weather result = weather.getweather(who.loc);
    	
    	if(result != null ) {
    		
    		searchResult.put("weather", result.ergebnis);
        	ret = ok(searchResult);
        	
    	} else {
    		ret = badRequest("could not get weather");
    	}
    	
    	return ret;
    	
    }

    


}