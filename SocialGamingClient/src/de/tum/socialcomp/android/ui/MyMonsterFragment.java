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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyMonsterFragment extends Fragment {

	String facebookID = MainActivity.getInstance()
			.getFacebookID(getActivity());
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_my_monsters,
				container, false);
	
		
		HttpGetter request = new HttpGetter();
		request.execute(new String[] { "Monsters", facebookID,
				"getmyMonsters" });

		try {
			String requestResult = request.get();

			// if we just received an empty json, ignore
			if (requestResult.isEmpty()
					|| !requestResult.equals("{ }")) {

				JSONObject json = new JSONObject(requestResult);
				JSONArray jsonMonsters = json
						.getJSONArray("Monster");
				
				//array with the entrys for the listview
				List<String> mymonsters = new ArrayList<String>();
				
				for (int i = 0; i < jsonMonsters.length(); i++) {
					JSONObject jsonMonster = jsonMonsters
							.getJSONObject(i);

					String Name = jsonMonster
							.getString("name");
					
					//String fid = jsonMonster
						//	.getString(facebookID);
					
					String Level = jsonMonster
							.getString("level");
					
					mymonsters.add("Level "+Level + " "+Name);
				}
			
				if(mymonsters.size()<=0) {
					HttpPoster post = new HttpPoster();
					post.execute(new String[] {"Monsters","001","name", "2", facebookID, "add"});
				}
				//instantiate the list
				ListAdapter adapter = new ArrayAdapter<String>(MyMonsterFragment.this.getActivity(),
						android.R.layout.test_list_item, mymonsters);
				final ListView lv = (ListView)rootView.findViewById(R.id.Monster_List);

				lv.setAdapter(adapter);

				//add the onclick listener
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						//TODO create Monster fragment and open it here with the given arguments
						
						
						Toast.makeText(
								MyMonsterFragment.this.getActivity(),
								"clicked on"+ lv.getAdapter().getItem(arg2).toString(),
								Toast.LENGTH_LONG).show();
					}
				
					
					
					
					
				}); 
			
			}
				
			} catch (Exception e) { // various Exceptions can be
				// thrown in the process, for
				// brevity we do a 'catch all'
				Log.e("mymonsterfragment", e.getMessage());
			}

	
		
		
		
		
			
		
			
		
	return rootView;
	}
	
	
}
