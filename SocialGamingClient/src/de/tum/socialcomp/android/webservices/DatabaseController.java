package de.tum.socialcomp.android.webservices;

import de.tum.socialcomp.android.webservices.util.HttpGetter;
import de.tum.socialcomp.android.webservices.util.HttpPoster;

/**
*
* @author Kevin
*/
public class DatabaseController {
	//////////////////////////////////////
	/////////////Methoden/////////////////
	//////////////////////////////////////
   
       public void getPlayer(String FacebookID) {
       //TODO   
    	   
       }
       
       //returns the players monster
       public void getMonsters(String FacebookID) {
    	   //TODO
    	   
       }
       
       public void setMonster(String id) {
    	   new HttpPoster().execute(new String[] { "Monsters", (String)id,
			"add"});
       }
       
       
}