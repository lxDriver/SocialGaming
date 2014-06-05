package de.tum.socialcomp.android.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.http.HttpClientFactory;
import org.osmdroid.http.IHttpClientFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import de.tum.socialcomp.android.GameDialogs;
import de.tum.socialcomp.android.MainActivity;
import de.tum.socialcomp.android.R;
import de.tum.socialcomp.android.R.drawable;
import de.tum.socialcomp.android.R.id;
import de.tum.socialcomp.android.R.layout;
import de.tum.socialcomp.android.webservices.util.HttpGetter;
import de.tum.socialcomp.android.webservices.util.HttpPoster;
import android.R.string;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FightSectionFragment extends Fragment{

	
	String facebookID = MainActivity.getInstance()
			.getFacebookID(getActivity());
	
	
	protected int my_health=100;
	protected int enemy_health=100;
	
	//TODO aus db laden
	protected int my_attack = 100;
	protected int enemy_attack=100;
	protected int my_deff=20;
	protected int enemy_deff=30;
	protected int my_heal=10;
	protected int enemy_heal=20;
	
	protected ProgressBar enemy_health_pro;
	protected ProgressBar my_health_pro;
	protected ImageView enemy_image;
	protected ImageView my_image;
	protected View rootView;
	protected TextView enemy_text_health;
	protected TextView my_text_health;
	protected TextView happening;
	protected TextView my_name;
	protected TextView enemy_name;
	protected TextView my_level;
	protected TextView enemy_level;
	
	protected String gameid;
	
	private String locked = "locked";
	private String free = "free";
	private String state = free;
	
	
	
	private static FightSectionFragment instance = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		FightSectionFragment.instance = this;
		
		rootView = inflater.inflate(R.layout.fragment_section_fight,
				container, false);
	
		
		gameid = MainActivity.getInstance().gameid;
		
		//alle elemente laden
		enemy_health_pro = (ProgressBar)rootView.findViewById(R.id.health_enemy);
		my_health_pro = (ProgressBar)rootView.findViewById(R.id.health_me);
		enemy_text_health = (TextView)rootView.findViewById(R.id.enemy_text_health);
		my_text_health = (TextView)rootView.findViewById(R.id.my_health_text);
		happening = (TextView)rootView.findViewById(R.id.happening);
		enemy_image = (ImageView)rootView.findViewById(R.id.image_enemy);
		my_image = (ImageView)rootView.findViewById(R.id.image_me);
		my_name = (TextView)rootView.findViewById(R.id.my_name);
		enemy_name = (TextView)rootView.findViewById(R.id.enemy_name);
		my_level = (TextView)rootView.findViewById(R.id.my_level);
		enemy_level = (TextView)rootView.findViewById(R.id.enemy_level);
	
		
		
		//health bars 
		enemy_health_pro.setProgress(enemy_health%101);
		my_health_pro.setProgress(my_health%101);
		
		//die texte
		enemy_text_health.setText(String.valueOf(enemy_health));
		my_text_health.setText(String.valueOf(my_health));
		
		happening.setText("");
		//happening.setText(gameid);
		
		//fightbutton
				rootView.findViewById(R.id.fight).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								
								Log.v("fight ", state + " "+ state.equals("free"));
								
								if(state.equals("free")) {
									//TODO what happens 
									
									
									//zufälligen deff mit deff wert zuweisen
									
									int block = (int)Math.random() * 100;
									
									Log.v("fight", "values" + block + " " +enemy_deff);
									if(block <= enemy_deff){
										Log.v("fight", "if");
										enemy_health = enemy_health - my_attack/2;
									}
									else{
										Log.v("fight", "else");
										enemy_health = enemy_health - my_attack;
									}
									
									
									Log.v("fight", "creating server post");
									HttpPoster end = new HttpPoster();
									end.execute(new String[]{"games", gameid, facebookID, String.valueOf(my_health), String.valueOf(enemy_health), "interaction"});
								} else {}
							}
						}
						
				);
		
		//Item Button
				rootView.findViewById(R.id.items).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								if(state.equals(free)) {
									//TODO what happens 
									//TODO items abrufen game dialog
									
									if(my_health <100) {
									//solange heal funktion
									my_health = (my_health+my_heal)%101;
									
									
									HttpPoster end = new HttpPoster();
									end.execute(new String[]{"games", gameid, facebookID, String.valueOf(my_health), String.valueOf(enemy_health), "interaction"});
									} else {
										//TODO game dialog health ist voll 
									}
									
								} else {
									
								}
								
							}
						}
				);
		
		//Weather attack btn
				rootView.findViewById(R.id.btn_weather_attack).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								//TODO an den server schicken das wir eine attacke ausführen
								if(state.equals(free)) {
									//TODO what happens 
									//wetterdaten abrufen
									
									HttpPoster end = new HttpPoster();
									//end.execute(new String[]{"games", gameid, facebookID, String.valueOf(my_health), String.valueOf(enemy_health), "interaction"});
								}
							}
						}
				);
				
				//Escape btn
				rootView.findViewById(R.id.btn_escape).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								//TODO an den server schicken das wir eine attacke ausführen
								if(state.endsWith(free)) {
									//TODO are you sure screen 
									
									
									///games/$gameID<[^/]+>/$facebookID<[^/]+>/abort
									HttpPoster end = new HttpPoster();
									end.execute(new String[]{"games", gameid, facebookID, "abort"});
								}
							}
						}
				);
				
				
		
		
				
		return rootView;
	}
	
	//TODO test
	//TODO ausbauen
	public void setmonsters(String monster1) {
		HttpGetter request = new HttpGetter();
		request.execute(new String[] { "Monsters", monster1, facebookID,
				"get" });

		try {
			String requestResult = request.get();

			// if we just received an empty json, ignore
			if (requestResult.isEmpty()
					|| !requestResult.equals("{ }")) {

				JSONObject json = new JSONObject(requestResult);
				my_attack = Integer.parseInt(json.getString("off"));
				my_deff = Integer.parseInt(json.getString("deff"));
			}
		} catch (Exception e) { // various Exceptions can be
			// thrown in the process, for
			// brevity we do a 'catch all'
			Log.e("mymonsterfragment", e.getMessage());
		}
				
	}
	
	public void setnamesandlevel(String FacebookID, String monster1, String level1, String level2, String monster2) {
		if(FacebookID.equals(facebookID)) {
			
			setLevel(FacebookID, Integer.parseInt(level1), Integer.parseInt(level2));
			
			my_name.setText(monster1);
			
			enemy_name.setText(monster2);
		} else {
			
			setLevel(FacebookID, Integer.parseInt(level2), Integer.parseInt(level1));
			
			my_name.setText(monster2);
			
			enemy_name.setText(monster1);
		}
	}
	
	public void setattackanddeff(String FacebookID, int myattack, int enemyattack, int mydeff, int enemydeff) {
		if(FacebookID.equals(facebookID)) {
			this.my_attack = myattack;
			this.my_deff = mydeff;
			this.enemy_attack = enemyattack;
			this.enemy_deff = enemydeff;
		}
		else {
			this.my_attack = enemyattack;
			this.my_deff = enemydeff;
			this.enemy_attack = myattack;
			this.enemy_deff = mydeff;
		}
	}
	
	public static FightSectionFragment getfightscreen() {
		return instance;
	}
	
	//locks/unlocks the screen
	public void lock() {
		Log.v("fight", "locking");
		this.state = "locked";
		Log.v("fight", "locked");
	}
	public void release() {
		this.state = free;
	}
	
	//gets the level 
	public void setLevel(String FacebookID, int Level, int Level2) {
		Log.v("setlevel","START");
		//user ist der erste uebergebene 
		if(FacebookID.equals(facebookID)){
			
			
			//level neu setzen
			my_level.setText(String.valueOf(Level));
			
			enemy_level.setText(String.valueOf(Level2));
			
			
		} else {
			my_level.setText(Level2);
			enemy_level.setText(Level);
		}
	}
	
	//bekommt Facebookid von spieler1 mit seiner health und id von spieler2 mit seiner health 
	public void setHealth(String FacebookID, String Health, String Health2) {
		
		//user ist der erste uebergebene 
		if(FacebookID.equals(facebookID)){
			my_health = Integer.parseInt(Health);
			enemy_health = Integer.parseInt(Health2);
			
			//gesundheitsbalken verändern
			my_health_pro.setProgress(my_health%101);
			enemy_health_pro.setProgress(enemy_health%101);
			
			//text unter dem balken setzen
			enemy_text_health.setText(String.valueOf(enemy_health));
			my_text_health.setText(String.valueOf(my_health));
			
		} else {
			my_health = Integer.parseInt(Health2);
			enemy_health = Integer.parseInt(Health);
			
			
			my_health_pro.setProgress(my_health%101);
			enemy_health_pro.setProgress(enemy_health%101);
			
			enemy_text_health.setText(String.valueOf(enemy_health));
			my_text_health.setText(String.valueOf(my_health));
			
		}
	}

	public void setHappenText(String happen) {
		happening.setText(happen);
	}
	
	//Bilder auf die Richtigen Monster setzen 
	
	public void setImages(String FacebookID, String picture,  String picture2) {
		//user ist der erste uebergebene 
		
				if(FacebookID.equals(facebookID)){
					
					//set enemy picture
					try {
						   Class<drawable> c = R.drawable.class;
						   Field f = c.getDeclaredField(picture2);
						   Resources res = FightSectionFragment.this.getActivity().getResources();
						   Drawable d = res.getDrawable(f.getInt(c));
						   enemy_image.setImageDrawable(d);
						} catch(Exception ex) { }
					
					//set my picture
					try {
						   Class<drawable> c = R.drawable.class;
						   Field f = c.getDeclaredField(picture);
						   Resources res = FightSectionFragment.this.getActivity().getResources();
						   Drawable d = res.getDrawable(f.getInt(c));
						   my_image.setImageDrawable(d);
						} catch(Exception ex) { }
					
				} else {
					
					//set enemy picture
					try {
						   Class<drawable> c = R.drawable.class;
						   Field f = c.getDeclaredField(picture);
						   Resources res = FightSectionFragment.this.getActivity().getResources();
						   Drawable d = res.getDrawable(f.getInt(c));
						   enemy_image.setImageDrawable(d);
						} catch(Exception ex) { }
					
					//set my picture
					try {
						   Class<drawable> c = R.drawable.class;
						   Field f = c.getDeclaredField(picture2);
						   Resources res = FightSectionFragment.this.getActivity().getResources();
						   Drawable d = res.getDrawable(f.getInt(c));
						   my_image.setImageDrawable(d);
						} catch(Exception ex) { }
				}
	}
}
