package com.andreijchildmonitorslave;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;






import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;






import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapLongClickListener,OnMarkerDragListener, OnMarkerClickListener,OnMyLocationChangeListener {
	GoogleMap googleMap;
	LatLng myPosition;
    Marker TP;
    LatLng TutorialsPoint;
    int x;
    int y;
    int level;
    String latitude;
    String longitude;
    double longit;
    double latit;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_map);
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));    
        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        googleMap = fm.getMap();

        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(this);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);
         Button poi= (Button) findViewById(R.id.POI);
         poi.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                	 TutorialsPoint = new LatLng(43.667719,-79.394748);
                	 TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("Royal Ontario Museum"));
                	 TutorialsPoint = new LatLng(43.653801,-79.392523);
                	 TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("Art Gallery of Ontario"));
                	 TutorialsPoint = new LatLng(43.642807,-79.387046);
                	 TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("CN Tower"));
                	 TutorialsPoint = new LatLng(43.635764,-79.410506);
                	 TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("Exhibition Place"));
                	 TutorialsPoint = new LatLng(43.645842, -79.378420);
                	 TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("Union Station/ Meeting Place"));
             }
         });
         
         Button back= (Button) findViewById(R.id.Back);
         back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
            	 finish();
             }
         });
         
    }
	@Override
	public void onMyLocationChange(Location location) {
		
        // Getting latitude of the current location
        latit = location.getLatitude();

        // Getting longitude of the current location
        longit = location.getLongitude();

        // Creating a LatLng object for the current location
        myPosition = new LatLng(latit, longit);
        new DBQueryUpdateUser().execute();
		
	}
	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}
	private class DBQueryUpdateUser extends AsyncTask<Integer,Void,String>
	{
					
			@Override
			protected String doInBackground(Integer... param) {
				// TODO Auto-generated method stub
				//final ProgressBar  users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
				//users_progressBar.setVisibility(ProgressBar.VISIBLE);
//				System.out.println("Address " + address);
				longitude = String.valueOf(longit);
				latitude = String.valueOf(latit);
		        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		        WifiInfo info = manager.getConnectionInfo();
		        String address = info.getMacAddress();
		        address = address.replace(":", "");
				if (longitude == null){
					longitude="0";
				}
				if (latitude == null){
					latitude="0";
				}
				System.out.println(longitude);
				System.out.println(latitude);
				String query = "update_gps_location.php?mac_address=" + address + "&battery=" + String.valueOf(level) + "&gps_latitude=" + latitude + "&gps_longitude=" + longitude;
//				System.out.println(query);
				return GetRequest.getData(query,getApplicationContext());
			}
			
			protected void onPostExecute(String result)
			{
//				System.out.println("Result " + result);
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
	
	  private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		    }
		};
}
