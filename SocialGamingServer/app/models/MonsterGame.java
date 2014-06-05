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
    	Logger.info("trying to find oponent");
    	User ret = null;
    	
    	//Get all friends that 1) participate and are 2) nearby and 3) logged in less that 60 mins ago  	
    	LinkedList<User> friends = new LinkedList<User>();
    	
    	for(String friendID: user.facebookFriendIDs) {
    		
    		User friend = User.findByFacebookID(friendID);
    		
    		if(friend != null && friend.participatesInGame) {
    			
    			if(Util.geoLocToDistInMeters(user.loc[0], user.loc[1], friend.loc[0], friend.loc[1]) <= GameConfiguration.MaxDistanceOfUserForNearbyUsers) {
    			
    				long currentTimeInSeconds = System.currentTimeMillis() / 1000;
    				
    				if(currentTimeInSeconds - friend.lastLogin <= GameConfiguration.MaxTimeForLoginTimeOutInSeconds){
    			
    					friends.add(friend);
    				} 				
    			}    			
    		}
    	}
    	 	
    	// choose one of the friends randomly, unless only one friend exists
    	if(friends.size() == 1) {
    		
    		ret = friends.get(0);
    		
    	} else if(friends.size() >= 1) {
    		
    		ret = friends.get( random.nextInt(friends.size()-1) );
    		
    	}
    	Logger.info("succes");
    	return ret;
    }
    
    
public static MonsterGame createAndStartNewGame(String FacebookID1, String FacebookID2) throws IOException {
    	
    	Logger.info("trying to create new game");
    	//user aus der Datenbank laden
		Logger.info("looking up users");
    	user1 =  User.findByFacebookID(FacebookID1);
    	Logger.info("found user 1");
    	user2 = User.findByFacebookID(FacebookID2);
    	Logger.info("found user 2");
    	Logger.info("creating game with users");
    	//neues Spiel erzeugen
    	MonsterGame newgame = new MonsterGame(user1, user2);
    	Logger.info("game created adding monster");    	
    	//newgame.id = FacebookID1+FacebookID2;
		newgame.monster_user1 = (Monster) Monster.getfirstmonster(FacebookID1);
		Logger.info("first monster id added");		
		newgame.monster_user2 = (Monster) Monster.getfirstmonster(FacebookID2);
		Logger.info("second monster id added");		
		
		//spiel in der Datenbank einfügen
    	games().insert(newgame);
    	
    	Logger.info("game inserted creating push notification");   	
    	//node an die spieler
    	ObjectNode gameestablished = PushMessages.createGameestablished(newgame, user1.facebookID);
    	Logger.info("created sending push");
    	user1.sendMessage(gameestablished);
    	Logger.info("player1 sended succesfull");    	
    	user2.sendMessage(gameestablished);
    	Logger.info("player2 sended succesfull");    	
    	
    	Logger.info("succes");
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
    
    public void fight(String FacebookID, String myhealth, String enemy_health) throws IOException {
    	
    	String turn;
    	
    	//next turn
    	ObjectNode fightnode;
    	
    	if(firstUserFbID.equals(FacebookID)) {
    		this.monster_user1.setHealth(myhealth);
    		this.monster_user2.setHealth(enemy_health);
    		turn = secondUserFbID;
    	} else {
    		this.monster_user2.setHealth(myhealth);
    		this.monster_user1.setHealth(enemy_health);
    		turn = firstUserFbID;
    	}
    	
    	//hat schon einer verloren?
    	if(Integer.parseInt(monster_user1.getHealth())<0) {
    		//TODO datenbankeintrag loeschen
    		fightnode = PushMessages.createwonmessage(id, user1);
    		
    		Logger.info("User 1 hat verloren");
    		return;
    	} else if(Integer.parseInt(monster_user2.getHealth())<0) {
    		//TODO user2 hat verloren
    		fightnode = PushMessages.createwonmessage(id, user2);
    		Logger.info("user 2 hat verloren");
    		return;
    	} else {
    	
    		fightnode = PushMessages.createnextturn(turn, this);
    		user1.sendMessage(fightnode);
    		user2.sendMessage(fightnode);
    	}
    	
    }
    
    
    
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