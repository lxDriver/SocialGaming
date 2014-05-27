package de.tum.socialcomp.android.ui;

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
import de.tum.socialcomp.android.R.id;
import de.tum.socialcomp.android.R.layout;
import de.tum.socialcomp.android.webservices.util.HttpGetter;
import de.tum.socialcomp.android.webservices.util.HttpPoster;
import android.R.string;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.Toast;

public class MyMonsterFragment extends Fragment {

	String facebookID = MainActivity.getInstance()
			.getFacebookID(getActivity());
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_my_monsters,
				container, false);
	
		rootView.findViewById(R.id.Monster_1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						/*
						 * what happens by clicking on the first monster
						 * 
						 * first send a test message to the server
						 * if he receives the message he sends a poke back to the user						
						 */
						
						Toast.makeText(
								MyMonsterFragment.this.getActivity(),
								"Sending Test Message",
								Toast.LENGTH_LONG).show();
						
						new HttpPoster().execute(new String[] { "test", facebookID,
								"test"});
						
					}});
		
		rootView.findViewById(R.id.Monster_2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						/*
						 * what happens by clicking on the first monster
						 * 
						 * first send a test message to the server
						 * if he receives the message he sends a poke back to the user						
						 */
						
						Toast.makeText(
								MyMonsterFragment.this.getActivity(),
								"Sending Monster",
								Toast.LENGTH_LONG).show();
						
						new HttpPoster().execute(new String[] { "Monsters", "2",
								"add"});
						
					}});
		
		
		rootView.findViewById(R.id.Monster_3).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						/*Toast.makeText(
								MyMonsterFragment.this.getActivity(),
								"Asking for Monsters",
								Toast.LENGTH_LONG).show();
						*/
						HttpGetter request = new HttpGetter();
						request.execute(new String[] { "Monsters", "2",
								"get" });
						
						String gameID = request.getResult().toString();
						
						Toast.makeText(
								MyMonsterFragment.this.getActivity(),
								gameID,
								Toast.LENGTH_LONG).show();
						
						
					}});
					
					
	return rootView;
	}
	
	
}
