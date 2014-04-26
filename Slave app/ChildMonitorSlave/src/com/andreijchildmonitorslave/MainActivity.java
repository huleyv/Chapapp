package com.andreijchildmonitorslave;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

import android.os.Vibrator;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends Activity implements OnMyLocationChangeListener  {
	Button agps;
	Button mgps;
	Button cdata;
	Button message;	
	Button map;
	int level;
	EditText LN;
	EditText FN;
	String longitude;
	String latitude;
	String fn;
	String ln;
	String address;
	TextView data;
	boolean AGPS = false;
	int exists = 0;
	int start = 0;
	boolean DGPS = false;
	int checked = 0;
	ImageView cl;
	int reg = 0;
	//Creates the main window
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
        agps = (Button) findViewById(R.id.AGPS);
        agps.setOnClickListener(myhandler1);
        mgps = (Button) findViewById(R.id.MGPS);
        mgps.setOnClickListener(myhandler2);
        cdata = (Button) findViewById(R.id.SyncDevice);
        cdata.setOnClickListener(myhandler3);     
        message = (Button) findViewById(R.id.Message);
        message.setOnClickListener(myhandler4);
        map = (Button) findViewById(R.id.Map);
        map.setOnClickListener(myhandler5);
        data = (TextView) findViewById(R.id.Data);
        cl = (ImageView) findViewById(R.id.Logo);
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();
        address = address.replace(":", "");
        FN = (EditText)findViewById(R.id.FirstName);
        FN.setOnClickListener(new View.OnClickListener(){
        	@Override
        	public void onClick(View v1)
        	{
        		FN.setText("");
        	}
        });
        LN = (EditText)findViewById(R.id.LastName);
        LN.setOnClickListener(new View.OnClickListener(){
        	@Override
        	public void onClick(View v1)
        	{
        		LN.setText("");
        	}
        });
	}
	//Acquires the manual gps coordinates
	  View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
			    if (reg == 1){
					   if (AGPS = false ){
						   Toast.makeText(getBaseContext(),"Acquiring coordinates", Toast.LENGTH_SHORT).show();
					   }
					   else {
						   new DBQueryCreateUser().execute();
					    	Toast.makeText(getBaseContext(),"New User Added", Toast.LENGTH_SHORT).show();
						   reg = 0;						   
					   }
			    }
			    else {
			    	if (AGPS = false ){
			    		Toast.makeText(getBaseContext(),"Acquiring coordinates", Toast.LENGTH_SHORT).show();
					}
			    	else {
					    new DBQueryUpdateUser().execute();
				    	Toast.makeText(getBaseContext(),"GPS Coordinats Updated", Toast.LENGTH_SHORT).show();
			    	}
			    }
		    }
		  };
		  //Acquire the GPS coordinates manually
		  View.OnClickListener myhandler2 = new View.OnClickListener() {
			    public void onClick(View v) {
			    	if (reg == 1) {
			        	longitude = FN.getText().toString();
			        	latitude = LN.getText().toString();
						FN.setText("Longitude");
						LN.setText("Latitude");
				    	new DBQueryCreateUser().execute();
				    	Toast.makeText(getBaseContext(),"New User Added", Toast.LENGTH_SHORT).show();
				    	reg = 0;
			    	}
			        else{
			        	longitude = FN.getText().toString();
			        	latitude = LN.getText().toString();
				    	FN.setText("Longitude");
				    	LN.setText("Latitude");
				    	new DBQueryUpdateUser().execute();
				    	Toast.makeText(getBaseContext(),"GPS Coordinats Updated", Toast.LENGTH_SHORT).show();
			        }
			    }
			  };
			//Checks if the user in the database, either goes to add new user screen or goes to add new coordinates
			View.OnClickListener myhandler3 = new View.OnClickListener() {
				    public void onClick(View v) {
				    	new DBQueryCheckUser().execute();
				    	if (checked == 1){
				    		if (exists == 1)
				    		{
					    		if (DGPS == true){
					    			LN.setVisibility(View.VISIBLE);
					    			FN.setVisibility(View.VISIBLE);
									FN.setText("Longitude");	
									FN.setBackgroundResource(R.drawable.gps_long);
									LN.setText("Latitude");
									LN.setBackgroundResource(R.drawable.gps_lat);
								    mgps.setVisibility(View.VISIBLE);
								    cdata.setVisibility(View.INVISIBLE);
								    cl.setVisibility(View.INVISIBLE);
								    message.setVisibility(View.VISIBLE);
								    map.setVisibility(View.VISIBLE);								    
					    		}
					    		else {
					    			LN.setVisibility(View.VISIBLE);
					    			FN.setVisibility(View.VISIBLE);
									FN.setText("Longitude");	
									FN.setBackgroundResource(R.drawable.gps_long);
									LN.setText("Latitude");
									LN.setBackgroundResource(R.drawable.gps_lat);
								    agps.setVisibility(View.VISIBLE);
								    mgps.setVisibility(View.VISIBLE);
								    cdata.setVisibility(View.INVISIBLE);
								    cl.setVisibility(View.INVISIBLE);
								    message.setVisibility(View.VISIBLE);
								    map.setVisibility(View.VISIBLE);
					    		}			
				    		}
				    		else {
				    			cdata.setBackgroundResource(R.drawable.button_register);
				    			LN.setVisibility(View.VISIBLE);
				    			FN.setVisibility(View.VISIBLE);
							    fn = FN.getText().toString();
							    ln = LN.getText().toString();
							    if ((ln.matches("") || fn.matches("")) || (fn.matches("First Name") || ln.matches("Last Name"))){
							        Toast.makeText(getBaseContext(),"First name or last name are not entered or blank", Toast.LENGTH_SHORT).show();
							    }
							    else {
								    reg = 1;
							    }
				    		}
				    	}
				    	else {
				    		cdata.setBackgroundResource(R.drawable.button_device_synced);
				    	}
				    	if (reg == 1){
				    		if (DGPS == true){
				    			LN.setVisibility(View.VISIBLE);
				    			FN.setVisibility(View.VISIBLE);
								FN.setText("Longitude");	
								FN.setBackgroundResource(R.drawable.gps_long);
								LN.setText("Latitude");
								LN.setBackgroundResource(R.drawable.gps_lat);
							    mgps.setVisibility(View.VISIBLE);
							    cdata.setVisibility(View.INVISIBLE);
							    cl.setVisibility(View.INVISIBLE);
							    message.setVisibility(View.VISIBLE);
							    map.setVisibility(View.VISIBLE);								    
				    		}
				    		else {
				    			LN.setVisibility(View.VISIBLE);
				    			FN.setVisibility(View.VISIBLE);
								FN.setText("Longitude");	
								FN.setBackgroundResource(R.drawable.gps_long);
								LN.setText("Latitude");
								LN.setBackgroundResource(R.drawable.gps_lat);
							    agps.setVisibility(View.VISIBLE);
							    mgps.setVisibility(View.VISIBLE);
							    cdata.setVisibility(View.INVISIBLE);
							    cl.setVisibility(View.INVISIBLE);
							    message.setVisibility(View.VISIBLE);
							    map.setVisibility(View.VISIBLE);
				    		}	
				    	}
				    	checked = 1;
				    }
				  };
		
				  //Displays the messages
					View.OnClickListener myhandler4 = new View.OnClickListener() {
					    public void onClick(View v) {
					    	new DBQueryMessage().execute();
					    }
					  };
				//Goes to the map
						View.OnClickListener myhandler5 = new View.OnClickListener() {
						    public void onClick(View v) {
						    	Intent amap = new Intent(MainActivity.this, MapActivity.class);
						    	startActivity(amap);					    	
						    }
						  };
		//Allows for the user to check the battery
		  private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
			    @Override
			    public void onReceive(Context context, Intent intent) {
			        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			    }
			};

	//Gets the GPS coordinates from antenna
	private class MyLocationListener implements LocationListener {

	    @Override
	    public void onLocationChanged(Location loc) {
	    	final String TAG = "GPS";
	        longitude = String.valueOf(loc.getLongitude());
	        Log.v(TAG, longitude);
	        System.out.println(longitude);
	        latitude = String.valueOf(loc.getLatitude());
	        Log.v(TAG, latitude);
	        System.out.println(latitude);
	    }

	    @Override
	    public void onProviderDisabled(String provider) {
	    	DGPS = true;
	    }

	    @Override
	    public void onProviderEnabled(String provider) {}

	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
	//Checks to see if user is the database
	private class DBQueryCheckUser extends AsyncTask<Integer,Void,String>
	{
					
			@Override
			protected String doInBackground(Integer... param) {
				// TODO Auto-generated method stub
				//final ProgressBar  users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
				//users_progressBar.setVisibility(ProgressBar.VISIBLE);
				System.out.println("Address " + address);
				String query = "check_if_user_exists.php?mac_address=" + address;
				return GetRequest.getData(query,getApplicationContext());
			}
			
			protected void onPostExecute(String result)
			{
				System.out.println("Result" + result);
				exists = Integer.parseInt(result);
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
	//Send the new user data to the database
	private class DBQueryCreateUser extends AsyncTask<Integer,Void,String>
	{
					
			@Override
			protected String doInBackground(Integer... param) {
				// TODO Auto-generated method stub
				//final ProgressBar  users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
				//users_progressBar.setVisibility(ProgressBar.VISIBLE);
//				System.out.println("Address " + address);
				if (longitude == null){
					longitude="0";
				}
				if (latitude == null){
					latitude="0";
				}
				String query = "insert_user_into_all_users.php?name=" + fn + " " + ln + "&mac_address=" + address + "&battery=" + String.valueOf(level) + "&gps_latitude=" + latitude + "&gps_longitude=" + longitude;
				System.out.println(query);
				return GetRequest.getData(query,getApplicationContext());
			}
			
			protected void onPostExecute(String result)
			{
				System.out.println("Result " + result);
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
	//Updates the suer location to the database
	private class DBQueryUpdateUser extends AsyncTask<Integer,Void,String>
	{
					
			@Override
			protected String doInBackground(Integer... param) {
				// TODO Auto-generated method stub
				//final ProgressBar  users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
				//users_progressBar.setVisibility(ProgressBar.VISIBLE);
//				System.out.println("Address " + address);
				if (longitude == null){
					longitude="0";
				}
				if (latitude == null){
					latitude="0";
				}
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
	
	//Gets the messages for that user
	private class DBQueryMessage extends AsyncTask<Integer,Void,String>
	{
			@Override
			protected String doInBackground(Integer... param) {
				// TODO Auto-generated method stub
				//final ProgressBar  users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
				//users_progressBar.setVisibility(ProgressBar.VISIBLE);
//				System.out.println("Address " + address);
				String query = "select_new_messages.php?mac_address=" + address;
//				System.out.println(query);
				return GetRequest.getData(query,getApplicationContext());
			}
			
			protected void onPostExecute(String result)
			{
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				try{
					JSONArray jArray = new JSONArray (result);
					String message = "";
					data.setBackgroundColor(Color.WHITE);
					int jsonArrLength = jArray.length();
					for (int i =0; i < jsonArrLength; i++){
						if (i<jsonArrLength-1){
							message +="Old Message: " + jArray.getJSONObject(i).getString("message") + "\n";
							data.setText(message);
						}
						else{
							message +="New Message: " + jArray.getJSONObject(i).getString("message") + "\n";
							data.setText(message);
						}
						if (jArray.getJSONObject(i).getString("message").equals("ping")){
							v.vibrate(4000);
						}
					}
				}
				catch (JSONException e){
				}
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
	@Override
	public void onMyLocationChange(Location loc) {
	}
}
