package models;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import org.jongo.MongoCollection;

import play.Configuration;
import play.Logger;
import play.libs.Json;
import uk.co.panaxiom.playjongo.PlayJongo;
import util.GameConfiguration;
import util.Util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * This class is for the database representation of the state of a Game 
 * AND most of the state-specific game logic
 * 
 * The game is a two-player game;
 * The user credentials such as Facebook ID and name are saved here in order
 * circumvent additional look-ups and to be able to show game statistics.
 * 
 *  
 * @author Niklas Klügel
 *
 */

public class MonsterGame {
	// used by Jongo to map JVM objects to database objects
    @JsonProperty("_id")
    public String id;
    
	
    // Credentials of the users
    public String firstUserFbID = "";
    public String secondUserFbID= "";
    
    public Monster monster_user1;
    public Monster monster_user2;
    
    public static User user1;
    public static User user2;
    
    private boolean firstUserAccepted = false;
    private boolean secondUserAccepted = false;
    
    /*
    public String firstUserName = "";
    public String secondUserName= "";
    */
    
    public Long firstUserInteractionTimeStamp = -1L;
    public Long secondUserInteractionTimeStamp= -1L;
    
    /*
    public String winnerFbID = "";
    public String winnerName = "";
    */
    
    public Date date = new Date();
    
    /*
    public static final String StateInitializing = "InInitializingState";
    public static final String StateProgress = "InProgressState";
    public static final String StateFinished = "InFinishedState";
    public static final String StateAborted = "InAbortedState";
    */
    
    private boolean aborted = false;
    
    //private String state = StateInitializing;
    
    /****************
     * Class methods
     * -------------
     ***************/
    
    /**
     * Looks-up all games that are finished or transient, i.e. used for 
     * the game statistics page.
     * @return
     */
    
    /*
    public static Iterable<MonsterGame> findAllGames() {    	
    	Iterable<MonsterGame> iterator = games().find().as(MonsterGame.class);
    	
    	return iterator;
    }*/
    
    /**
     * Looks up an opponent for the user to play the game against.
     * 
     * @param user
     * @return
     * @throws IOException 
     */
    
    public static User findOpponent(User user) throws IOException{
    	// we are looking up participating friends here, so you can start the application
    	// out of the box; preferable for such a game would be friend of friends etc
    	// 
    	// this method is costly but primitive
    	
    	User ret = null;
    	
    	//Get all friends that 1) participate and are 2) nearby and 3) logged in less that 60 mins ago  	
    	LinkedList<User> friends = new LinkedList<User>();
    	Logger.info("looking up friends");
    	for(String friendID: user.facebookFriendIDs) {
    		
    		User friend = User.findByFacebookID(friendID);
    		
    		if(friend != null && friend.participatesInGame) {
    			Logger.info("found one: "+ friend.name);
    			if(Util.geoLocToDistInMeters(user.loc[0], user.loc[1], friend.loc[0], friend.loc[1]) <= GameConfiguration.MaxDistanceOfUserForNearbyUsers) {
    				Logger.info(friend.name + " is compatible");
    				long currentTimeInSeconds = System.currentTimeMillis() / 1000;
    				
    				if(currentTimeInSeconds - friend.lastLogin <= GameConfiguration.MaxTimeForLoginTimeOutInSeconds){
    					Logger.info("friend is compatible and active" + friend.name +" now adding to list");
    					friends.add(friend);
    				} 				
    			}    			
    		}
    	}
    	 	Logger.info("found " + friends.size() + "friends taking one");
    	// choose one of the friends randomly, unless only one friend exists
    	if(friends.size() == 1) {
    		
    		ret = friends.get(0);
    		Logger.info("Found only 1 user ");
    		Logger.info("opponent: "+ret.name+ " me "+user.name);
    	} else if(friends.size() >= 1) {
    		Logger.info("found multiple users");
    		ret = friends.get( random.nextInt(friends.size()-1) );
    		Logger.info("opponent: "+ret.name+ " me "+user.name);
    	}
    	
    	
    	
    	Logger.info("creating json");
    	ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "testfound");
		Logger.info("sending message");
		user.sendMessage(json);
		Logger.info("returning");
    	return ret;
    }
    
   /* public static Game createAndStartNewGame(User user1, User user2) throws IOException {
    	//TODO: lookup and test whether a game between both users already exists
    	 	
    	Game newGame = new Game(user1, user2);
    	games().insert(newGame);
    	
    	ObjectNode requestMessage = PushMessages.createGameRequestMessage(newGame);
    	
    	user1.sendMessage(requestMessage);
    	user2.sendMessage(requestMessage);
    	
    	return newGame;
    }
    */
    
public static MonsterGame createAndStartNewGame(String FacebookID1, String FacebookID2) throws IOException {
    	
    	
    	//user aus der Datenbank laden
		Logger.info("trying to load the players with fbid "+ FacebookID1 + " " + FacebookID2);
    	user1 =  User.findByFacebookID(FacebookID1);
    	
    	Logger.info("trying to load player 2");
    	user2 = User.findByFacebookID(FacebookID2);
    	
    	Logger.info("user geladen, trying to create game");
    	//neues Spiel erzeugen
    	MonsterGame newgame = new MonsterGame(user1, user2);
    	
    	Logger.info("trying to load Monster from player 1");
    	//newgame.id = FacebookID1+FacebookID2;
		newgame.monster_user1 = (Monster) Monster.getfirstmonster(FacebookID1);
		
    	
		Logger.info("trying to load monster from player 2");
		newgame.monster_user2 = (Monster) Monster.getfirstmonster(FacebookID2);
		
		
		Logger.info("game id" + newgame.id);
		Logger.info(" trage spiel in die datenbank ein");
		//spiel in der Datenbank einfügen
    	games().insert(newgame);
    	Logger.info("spiel in die datenbank eingetragen");
    	//zufall
    	
    	Logger.info("sende nachrichten an die user");
    	//node an beginner
    	ObjectNode gameestablished = PushMessages.createGameestablished(newgame, user1.facebookID);
    	user1.sendMessage(gameestablished);
    	
    	Logger.info("user 1 erfolreich");
    	user2.sendMessage(gameestablished);
    	
    	Logger.info("user2 erfolgreich");
    	return newgame;
    }

    public static MonsterGame findByID(String id) { 	
    	return games().findOne("{_id: #}", id).as(MonsterGame.class);
    }
    
    
    
    public static MongoCollection games() {
        MongoCollection gameCollection =  PlayJongo.getCollection("games");
 
        return gameCollection;
    }
    
    public static MongoCollection users() {
    	MongoCollection userCollection = PlayJongo.getCollection("users");
    	
    	return userCollection;
    }

    public static MongoCollection monster() {
    	MongoCollection MonsterCollection = PlayJongo.getCollection("Monsters");
    	
    	return MonsterCollection;
    }
    
    
    /****************
     * Object methods
     * -------------
     ***************/
    
    /// these methods involve game logic
    
    
    /**
     * To be called when a user accepted a game, 
     * establishes a game as soon as both user have accepted it.
     * Sends an established-message to both users.
     * 
     * @param facebookId
     * @throws IOException
     */
    
    public void accept(String facebookId) throws IOException{
    	Logger.info("accepted game");
    	
    	if(firstUserFbID.equals(facebookId)) {
    		firstUserAccepted = true;
    	}
    	
    	if(secondUserFbID.equals(facebookId)) {
    		secondUserAccepted = true;
    	}
    	
    	this.update();
    	
    	if(this.isEstablished() /*&& this.state.equals(StateInitializing)*/){
    		
    		//this.state = StateProgress;
    		this.update();
    		
    		// if the game is just established by the new accept message, send
    		// users the message that they can start playing
    		
    		User user1 = User.findByFacebookID(this.firstUserFbID);
        	User user2 = User.findByFacebookID(this.secondUserFbID);
    		
        	//TODO 
        	ObjectNode reportMessage = PushMessages.createEstablishedGameMessage(this);
        	
        	user1.sendMessage(reportMessage);
        	user2.sendMessage(reportMessage);
    	}
    }
    
    private static Random random = new Random();
    private static boolean flipCoin(double probability) {
    	return random.nextDouble() < probability;
    }
    
    public boolean isAborted(){
    	return this.aborted;
    }
    
    public boolean isEstablished(){
    	return firstUserAccepted && secondUserAccepted;
    }
    
    /**
     * Aborts the game.
     * Sends an abort-message to both users
     * 
     * @param abortingUserFacebookID
     * @throws IOException
     */
    
    public void abort(String abortingUserFacebookID) throws IOException {
    	this.aborted = true;
    	//this.state = StateAborted;
    	//this.winnerName ="aborted";
    	this.update();
    	   	
    	User user1 = User.findByFacebookID(this.firstUserFbID);
    	User user2 = User.findByFacebookID(this.secondUserFbID);
    	
    	User abortingUser = null;
    	
		// subtract a point for giving up
    	if(user1.facebookID.equals(abortingUserFacebookID)){
    		user1.addToScoreAndUpdate(-1);
    		abortingUser = user1;
    		
    	} else {
    		user2.addToScoreAndUpdate(-1);
    		abortingUser = user2;
    	}
    	
    	//TODO
    	ObjectNode abortMessage = PushMessages.createAbortGameMessage(this, abortingUser);
    	
    	user1.sendMessage(abortMessage);
    	user2.sendMessage(abortMessage);
    }
    
    /**
     * If a social interaction happened, this method evaluates it.
     * If both users interacted within a narrow time-frame of 60 seconds, then
     * the game is "finished" and the winner is randomly selected.
     * 
     * @param facebookID
     * @throws IOException
     */
    
    public void fight(String FacebookID, int myhealth, int enemy_health) throws IOException {
    	
    	String turn;
    	
    	if(firstUserFbID.equals(FacebookID)) {
    		this.monster_user1.setHealth(String.valueOf(myhealth));
    		this.monster_user2.setHealth(String.valueOf(enemy_health));
    		turn = secondUserFbID;
    	} else {
    		this.monster_user2.setHealth(String.valueOf(myhealth));
    		this.monster_user1.setHealth(String.valueOf(enemy_health));
    		turn = firstUserFbID;
    	}
    	
    	//hat schon einer verloren?
    	if(Integer.parseInt(monster_user1.getHealth())<0) {
    		//TODO user1 hat verloren
    		Logger.info("User 1 hat verloren");
    		return;
    	}
    
    	if(Integer.parseInt(monster_user2.getHealth())<0) {
    		//TODO user2 hat verloren
    		Logger.info("user 2 hat verloren");
    		return;
    	}
    	
    	//next turn
    	ObjectNode gameestablished = PushMessages.createnextturn(turn);
    	user1.sendMessage(gameestablished);
    	user2.sendMessage(gameestablished);
    	
    	
    }
    
    /*
    public void socialInteraction(String facebookID) throws IOException{
    	Long time = System.currentTimeMillis();
    	
    	/*
    	if(this.state.equals(StateProgress)){
    		
    		Logger.info("offset " + java.lang.Math.abs(this.firstUserInteractionTimeStamp - this.secondUserInteractionTimeStamp)); 
    		
    		if(this.firstUserFbID.equals(facebookID) && this.firstUserInteractionTimeStamp < 0L) {
    			this.firstUserInteractionTimeStamp = time;
    			this.update();
    		}
    		
    		if(this.secondUserFbID.equals(facebookID) && this.secondUserInteractionTimeStamp < 0L){
    			this.secondUserInteractionTimeStamp = time;
    			this.update();
    		}*/
    		
    		// if both timestamps have been set, then the game is finished
   /* 	//TODO
    		if(this.firstUserInteractionTimeStamp > 0 && this.secondUserInteractionTimeStamp > 0) {
    			
    			User user1 = User.findByFacebookID(this.firstUserFbID);
            	User user2 = User.findByFacebookID(this.secondUserFbID);
    			
    			// see if social interaction took place in a similar time frame (one minute)
            	// else we have a draw
            	if(java.lang.Math.abs(this.firstUserInteractionTimeStamp - this.secondUserInteractionTimeStamp) < 60000) {
            	
	            	boolean user1Result = flipCoin(0.5); 
	    			boolean user2Result = flipCoin(0.5);
	    			
	    			if(user1Result && !user2Result){
	    				// user1 won
	    				user1.addToScoreAndUpdate(5);
	    				//this.winnerFbID = user1.facebookID;
	    				//this.winnerName = user1.name;
	    				
	    		    	user1.sendMessage(PushMessages.createWonGameMessage(user1, user2));
	    		    	user2.sendMessage(PushMessages.createLostGameMessage(user2, user1));
	    				
	    			} else if(!user1Result && user2Result){
	    				// user2 won
	    				user2.addToScoreAndUpdate(5);
	    				//this.winnerFbID = user2.facebookID;
	    				//this.winnerName = user2.name;
	    				
	    				user2.sendMessage(PushMessages.createWonGameMessage(user2, user1));
	    		    	user1.sendMessage(PushMessages.createLostGameMessage(user1, user2));
	    				
	    			} else {
	    				// draw
	    				user1.sendMessage(PushMessages.createDrawGameMessage(user2));
	    				user2.sendMessage(PushMessages.createDrawGameMessage(user1));
	    				
	    				//this.winnerName = "draw";
	    			} 			
	    			
            	} else {
       				// draw
    				user1.sendMessage(PushMessages.createDrawGameMessage(user2));
    				user2.sendMessage(PushMessages.createDrawGameMessage(user1));
    				
    				//this.winnerName = "draw";
            	}
            	//this.state = StateFinished;
            	
    			this.update();
    		//}
    	}
    }*/
    
    /**
     * Administrative methods
     * 
     */

	public MonsterGame(){}
	
	public MonsterGame(User user1, User user2){
		this.firstUserFbID = user1.facebookID;
		this.secondUserFbID= user2.facebookID;
		
		//this.firstUserName = user1.name;
		//this.secondUserName= user2.name;
	}
	
	/**
	 * creates a deep copy of this object
	 * @return
	 */
	private MonsterGame copy() {
		MonsterGame cp = new MonsterGame();
		cp.firstUserAccepted = this.firstUserAccepted;
		cp.firstUserFbID = this.firstUserFbID;
		//cp.firstUserName = this.firstUserName;
		cp.firstUserInteractionTimeStamp = this.firstUserInteractionTimeStamp;
		cp.secondUserAccepted=this.secondUserAccepted;
		cp.secondUserFbID= this.secondUserFbID;
		//cp.secondUserName=this.secondUserName;
		cp.secondUserInteractionTimeStamp = this.secondUserInteractionTimeStamp;
		cp.aborted = this.aborted;
		//cp.winnerFbID = this.winnerFbID;
		//cp.winnerName = this.winnerName;
		cp.date = this.date;
		//cp.state = this.state;
		
		return cp;
	}
	
	/**
	 * Updates the whole object in the database using a deep copy with unassigned database/jongo ID.
	 */
    private void update() {
    	games().update("{_id: #}",this.id).with(this.copy());
    }
    
    
    /*public String getState(){
    	return this.state;
    }*/
 
    
}