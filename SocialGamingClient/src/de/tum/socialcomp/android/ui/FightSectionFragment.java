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
import android.app.Dialog;
import android.content.Context;
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
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_section_fight,
				container, false);
	
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
		
		//fightbutton
				rootView.findViewById(R.id.fight).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								//TODO an den server schicken das wir eine attacke ausführen
								HttpPoster request = new HttpPoster();
								request.execute(new String[] { "games", facebookID,
										"requestNew" });
								try {
									String requestResult = request.get();
									if (requestResult.isEmpty()
											|| !requestResult.equals("{ }")) {
										
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
				);
		
		//Item Button
				rootView.findViewById(R.id.items).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								//TODO an den server senden das wir uns heilen wollen
						
							}
						}
				);
		
		//Weather attack btn
				rootView.findViewById(R.id.btn_weather_attack).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								//TODO an den server schicken das wir eine attacke ausführen
								
							}
						}
				);
				
				//Escape btn
				rootView.findViewById(R.id.btn_escape).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								//TODO an den server schicken das wir eine attacke ausführen
								
							}
						}
				);
				
				
		
		
				
		return rootView;
	}
	
	//gets the level 
	public void setLevel(String FacebookID, int Level, String FacebookID2, int Level2)
	{
		
		//user ist der erste uebergebene 
		if(FacebookID.equals(facebookID)){
			
			
			//level neu setzen
			my_level.setText(Level);
			enemy_level.setText(Level2);
			
		} else {
			my_level.setText(Level2);
			enemy_level.setText(Level);
		}
	}
	
	//bekommt Facebookid von spieler1 mit seiner health und id von spieler2 mit seiner health 
	public void setHealth(String FacebookID, int Health, String FacebookID2, int Health2)
	{
		
		//user ist der erste uebergebene 
		if(FacebookID.equals(facebookID)){
			my_health = Health;
			enemy_health = Health2;
			
			//gesundheitsbalken verändern
			my_health_pro.setProgress(Health%101);
			enemy_health_pro.setProgress(Health2%101);
			
			//text unter dem balken setzen
			enemy_text_health.setText(String.valueOf(enemy_health));
			my_text_health.setText(String.valueOf(my_health));
			
		} else {
			my_health = Health2;
			enemy_health = Health;
			
			
			my_health_pro.setProgress(Health2%101);
			enemy_health_pro.setProgress(Health%101);
			
			enemy_text_health.setText(String.valueOf(enemy_health));
			my_text_health.setText(String.valueOf(my_health));
			
		}
	}

	public void setHappenText(String happen) {
		happening.setText(happen);
	}
	
	//Bilder auf die Richtigen Monster setzen 
	//TODO: ein bild wird immer nicht gesetzt
	public void setImages(String FacebookID, String picture, String FacebookID2, String picture2) {
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
