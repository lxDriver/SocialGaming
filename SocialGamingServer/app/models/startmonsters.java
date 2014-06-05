package models;

import org.jongo.MongoCollection;

import play.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.co.panaxiom.playjongo.PlayJongo;

import java.io.*;

public class startmonsters {

	//database id
		 @JsonProperty("_id")
		    public String id;
		 
	
	
	public startmonsters() {}
	//load the start monsters
	
	public static MongoCollection start() {
	       MongoCollection MonsterCollection =  PlayJongo.getCollection("startmonsters");
		        
	       return MonsterCollection;
	}
		 
	
	//looks up in the given file one monster
	public static Monster get(String mid) {
		Monster monster;
		
		monster = start().findOne("{mid: #}", mid).as(Monster.class);
		Monster neu = new Monster();
		if(monster!=null) {
			return monster;
		} else {
			//first start oft application 
			Logger.info("first start!! need to add Monsters, this may take a while");
			
			FileReader fr;
			try {
				Logger.info("opening file reader");
				fr = new FileReader("C:/Users/social-server/Desktop/test/SocialGamingServer/app/models/Monster.mon");
				
				BufferedReader br = new BufferedReader(fr);
				String zeile;
				Logger.info("while begin");
				int count = 0;
				boolean found=false;
				
				 while( (zeile = br.readLine()) != null &&!found)
				    {
					 
					 if(!zeile.equals("END")&&zeile.equals("START")&&(zeile = br.readLine()).equals(mid)){
						 Logger.info("found Monster in monster.mon");
						 
						 neu.mid = zeile;
						 neu.name = br.readLine();
						 neu.level = br.readLine();
						 neu.deff = br.readLine();
						 neu.health = br.readLine();
						 neu.exp = br.readLine();
						 neu.picture = br.readLine();
						 neu.bonus = br.readLine();
						 zeile = br.readLine();
						 zeile = br.readLine();
						 zeile = br.readLine();
						 neu.off = br.readLine();
						 zeile = br.readLine();
						 found = true;
						 
					 } else {
						 Logger.info("wrong monster or last entry");
					 }
					 
				     
				     // count ++;
				    }
		    

		    br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Logger.info("file not found");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Logger.info("ioexception");
			}
			
			
			
			
			
			
			
		
		}
		
		
		return neu;
		
	}
}
