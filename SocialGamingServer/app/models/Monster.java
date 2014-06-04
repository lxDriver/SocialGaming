package models;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gcm.server.Result;
import com.google.common.collect.Lists;

import controllers.FacebookAppClient;
import controllers.PushNotifications;
import uk.co.panaxiom.playjongo.PlayJongo;

public class Monster {
	
	//database id
	 @JsonProperty("_id")
	    public String id;
	 
	 //facebookid des besitzers
	 protected String FacebookID="";
	//Name of the monster.
	 protected String name = "";
	//Current level
	 protected String level ="";
	 //damage
	 protected String off="";
	 //defensive
	 protected String deff="";
	//Experience the Monster has
	 protected String exp="";
	//Current Health. Reaching 0 means death;
	 protected String health="";
	//Max Health without HP Buff
	 protected String maxHealth="";
	
	 /****************
	     * Object methods
	     * -------------
	     ***************/
	 
	 //Create with the only values beeing changed by the user
	 Monster(String id, String Name, String Level, String FacebookID) {
		 this.id = id;
		 this.name = Name;
		 this.level = Level;
		 this.FacebookID = FacebookID;
		 //TODO: rest aus txt oder datenbank laden 
		 
		 //for debuging so setzen
		 off = "20";
		 deff = "30";
		 exp = "10";
		 health = "100";
		 maxHealth = "100";
		 
	 }
	 //simple Constructor for instantiate after db request
	 Monster(){}
	 	 
	 
	 /****************
	     * Class methods
	     * -------------
	     ***************/
	 
	 //gets our Monster database
	 public static MongoCollection monsters() {
	        MongoCollection MonsterCollection =  PlayJongo.getCollection("Monsters");
	        
	        return MonsterCollection;
	    }    
	 
	 
	 //update the database entry for a Monster
	 public static void updateMonster(String id, String off, String deff, String level, String health, String exp)
	 {
		 monsters().update("{_id: #}", id).with("{$set: {off: #}}", off);
	 }
	 
	 
	 //looking up database to find all! Monsters
	 //TODO Test
	 public static Iterable<Monster> findAllMonsters(String FacebookID) {    	
	    	Logger.info("looking up monsters for user " + FacebookID);
		 List<Monster> results = Lists.newArrayList(monsters().find("{FacebookID: #}", FacebookID).as(Monster.class));
		 Logger.info("found "+results.size() + " Monsters for user "+ FacebookID);
	    	
	    	return results;
	    }
	 
	 public static Monster getfirstmonster(String FacebookID) {
		return monsters().findOne("{FacebookID: #}", FacebookID).as(Monster.class);
		 
	 }
	 
	 
	 //looking up a monster by his id in the database
	public static Monster findbyid(String MonsterID) {
		
		Monster test;
		Logger.info("Requesting Monster NR " + MonsterID);
		
		//find the monster in the collection and save it as a class
		test = monsters().findOne("{_id: #}", MonsterID).as(Monster.class);
		
		Logger.info(test.name);
				
		return test;
		}
	
	
	//create new database entry for a new Monster
	public static Monster createMonster(String MonsterID, String Name, String Level, String FacebookID) throws IOException {
    	    	 	
		String newid = FacebookID+MonsterID;
		
		Logger.info("Beginn writing Monster Monster id ");
		//is there an entry with the same id?
		if((monsters().findOne("{_id: #}", FacebookID).as(Monster.class)==null)) {
			
			Monster newMonster = new Monster(FacebookID, Name, Level, FacebookID);
			monsters().save(newMonster);
			Logger.info("writing Monster "+ FacebookID);
		
		}
		else
		{	//if entry with same ID exists update with new name and level
			monsters().update("{_id: #}", FacebookID).with("{$set: {Name: #}}", Name);
			monsters().update("{_id: #}", FacebookID).with("{$set: {level: #}}", Level);
			
			Logger.info("updating Monster "+ FacebookID);
		}
    	
		return null;
    }
    
	
	/*
	 * GET Methoden
	 */
    

	 public String getID() {

	        return id;

	    }


	    public String getName() {

	        return name;

	    }


	    public String getLevel() {

	        return level;

	    }


	    public String getmaxHealth() {

	        return maxHealth;

	    }


	    public String getHealth() {

	        return health;

	    }


	    public String getDefence() {

	        return deff;

	    }


	    public String getEXP() {

	        return exp;

	    }
	    
	    public String getOffensive() {
	    	Logger.info("requested off "+ off);
	    	return off;
	    	
	    }

	    public String getFacebookID() {
	    	
	    	return FacebookID;
	    
	    }
/*
 * SET Methoden
 */
	    
	    public void setID(String ID) {

	        this.id = ID;

	    }


	    public void setName(String name) {

	        this.name = name;

	    }


	    public void setLevel(String level) {

	        this.level = level;

	    }


	    public void setmaxHealth(String maxHealth) {

	        this.maxHealth = maxHealth;

	    }


	    public void setHealth(String health) {

	        this.health = health;

	    }


	    public void setDefence(String defence) {

	        this.deff = defence;

	    }


	    public void setEXP(String exp) {

	        this.exp = exp;

	    }

 
	
	
}
