//Author: Volodymyr Huley

package com.team14.kidstracker;


import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;




import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;



import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
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
import com.team14.kidstracker.R;

public class MonitorScreenActivity extends FragmentActivity implements OnMapLongClickListener,
																		OnMapClickListener,
																		OnMarkerDragListener, 
																		OnMarkerClickListener,
																		OnMyLocationChangeListener,
																		OnInfoWindowClickListener
																		
{
	
    private GoogleMap mMap;
	
	private MockLocationSource mLocationSource;


    Button circleMap_button, settingsMap_Button, deleteCircleMap_Button, myCircle_Button, clearAllCircles_Button;
    View subSettings_Layout, clearAllPopUp_Layout, pointOfInterestPopUp_Layout, radiusButtons_Layout,sendMsg_Layout, alertPopUp_Layout;
    EditText circleName_EditText, circleDescription_EditText, sendMsgTo_EditText;
    TextView sendMsgToUserName_TextView, numOfUsersOutsideTheRange_TextView;
    private boolean settingsVisible = false;
    private boolean deleteCircle = false;
    private boolean putCircle = false;
    private boolean isMasterCircle = false;
    private List<TrackedUser> trackedUsersList = new ArrayList<TrackedUser>(0);
    private String myMacAddress;
    private Handler handler, handler2;
    private int refreshInterval = 10000;
    private LatLng circleToBeAddedLocation;
    private String userName;
    private Marker lastMarkerClicked;
    private boolean usersAreInRange;
    private List<TrackedUser> alertUsersList = new ArrayList<TrackedUser>(0);
    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); //used to play alert
    Ringtone ringtone; //used to play alert

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitorscreen);
        
        circleMap_button = (Button) findViewById(R.id.circleMap_Button);
        settingsMap_Button = (Button) findViewById(R.id.settingsMap_Button);
        deleteCircleMap_Button = (Button) findViewById(R.id.deleteCircleMap_Button);
        myCircle_Button = (Button) findViewById(R.id.myCircle_Button);
        clearAllCircles_Button = (Button) findViewById(R.id.clearAllCircles_Button);
        subSettings_Layout = (View) findViewById(R.id.subSettings_Layout);
        clearAllPopUp_Layout = (View) findViewById(R.id.clearAllPopUp_Layout);
        radiusButtons_Layout = (View) findViewById(R.id.radiusButtons_Layout);
        pointOfInterestPopUp_Layout = (View) findViewById(R.id.pointOfInterestPopUp_Layout);
        sendMsg_Layout = (View) findViewById(R.id.sendMsg_Layout);
        alertPopUp_Layout = (View) findViewById(R.id.alertPopUp_Layout);
        circleName_EditText = (EditText) findViewById(R.id.circleName_EditText);
        circleDescription_EditText = (EditText) findViewById(R.id.circleDescription_EditText);
        sendMsgTo_EditText = (EditText) findViewById(R.id.sendMsgTo_EditText);
        sendMsgToUserName_TextView = (TextView) findViewById(R.id.sendMsgToUserName_TextView);
        numOfUsersOutsideTheRange_TextView = (TextView) findViewById(R.id.numOfUsersOutsideTheRange_TextView);
        
        //hide layouts
        subSettings_Layout.setVisibility(View.GONE);
        clearAllPopUp_Layout.setVisibility(View.GONE);
        pointOfInterestPopUp_Layout.setVisibility(View.GONE);
        radiusButtons_Layout.setVisibility(View.GONE);
        sendMsg_Layout.setVisibility(View.GONE);
        alertPopUp_Layout.setVisibility(View.GONE);
        
        //retrieve MAC address of Wi-Fi and store it in a variable
        setMyMACAddress();
        
        //retrieve userName value that was passed to this activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	userName = extras.getString("userName");
        }
        
        //for testing reason. It produces fake GPS coordinates
        //mLocationSource = new MockLocationSource();
        
        setUpMapIfNeeded();
        
        
        
        //start perioduc task that adds and updates users location on the map
        handler = new Handler();
        startUsersUpdateTask();
        
        
        //starts periodic task that checks  if all users are in range
        handler2 = new Handler();
        startDistanceCheckerTask();
        
        
        //retrieve point of interests from the DB and display on the map
        new DBQueryGetCircles().execute();
        
        //delete master circle (my old location) from DB if any before initializing the app. This is done just to synchronize Map data and DB
        new DBQueryDeleteMasterCircle().execute();
        
        //add master location to DB with empty location. Location will be updated dynamically
	    new DBQueryAddMasterCircle().execute(myMacAddress, "0","0","0","0","0",userName,"Your_guide","0");
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setLocationSource(mLocationSource);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnInfoWindowClickListener(this);    
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        
    }

    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	stopUsersUpdateTask();
    	stopDistanceCheckerTask();
    	//stop generation of fake gps coordinates (for testing reason)
    	//mLocationSource.deactivate();
    }
    
    //change "Settings" button icon upon click 
    public void onSettingsMapClick(View view) {
    	
    	if (settingsVisible)
    	{
    		settingsMap_Button.setBackgroundResource(R.drawable.button_settings_opacity);
    		subSettings_Layout.setVisibility(View.GONE);
    		settingsVisible = false;
    	}
    	else{
    		settingsMap_Button.setBackgroundResource(R.drawable.button_settings_ena_opacity);
    		subSettings_Layout.setVisibility(View.VISIBLE);
    		settingsVisible = true;
    	}
    }
    
    //change "Add POI" button icon upon click 
    public void onCirlceMapClick(View view) {
     	
    	if (putCircle){
    		circleMap_button.setBackgroundResource(R.drawable.button_poi_opacity);
    		putCircle = false;
    	}
    	else{
    		circleMap_button.setBackgroundResource(R.drawable.button_poi_enabled_opacity);
    		putCircle = true;
    	}
    }
    
    //set different icon on a delete POI button
    public void onDeleteCircleMapClick(View view) {
    	
    	if (deleteCircle){
    		deleteCircleMap_Button.setBackgroundResource(R.drawable.button_delete_poi_opacity);
    		deleteCircle = false;
    	}
    	else{
    		deleteCircleMap_Button.setBackgroundResource(R.drawable.button_delete_poi_enabled_opacity);
    		deleteCircle = true;
    	}

    }
   //build and show master circle; creates a circle around my location with specific radius.
   public void onShowMasterCircleMapClick(View view) {
    	
    	if (isMasterCircle && masterCircle != null){
    		myCircle_Button.setBackgroundResource(R.drawable.me_dis_opacity);
    		//delete master circle from the map and set it to null
    		masterCircle.deleteMasterCircle();
    		masterCircle = null;

    		//update master circle in DB as disabled
    		new DBQueryEnableDisableMasterCircle().execute("0");
    		
    		isMasterCircle = false;
    	}
    	else{
    		
    		Location mLocation = mMap.getMyLocation();
    		if (mLocation != null)
    		{
    			//change button background color
    			myCircle_Button.setBackgroundResource(R.drawable.me_ena_opacity);
    			
    			//get my location from map and create DraggableCircle object
    			LatLng myLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
	    		Point pointOnScreen = mMap.getProjection().toScreenLocation(myLatLng);
				// We know the center, let's place the outline at a point 3/4 along the view.
	    		//View viewMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getView();	      
	 	        LatLng radiusLatLng = mMap.getProjection().fromScreenLocation(new Point(pointOnScreen.x + RADIUS, pointOnScreen.y));
	 	        
	 	        
		        // now create circle
		        masterCircle = new MasterCircle(new LatLng (mLocation.getLatitude(), mLocation.getLongitude()), radiusLatLng);
		       
		        
		        masterCircle.isEnabled = 1;
	 	        //update Master circle in a DB
	 	        new DBQueryUpdateMasterCircle().execute(myMacAddress,
	 	        									String.valueOf(myLatLng.latitude),
	 	        									String.valueOf(myLatLng.longitude),
	 	        									String.valueOf(toRadiusMeters(myLatLng, radiusLatLng)),
	 	        									String.valueOf(radiusLatLng.latitude),
	 	        									String.valueOf(radiusLatLng.longitude),
	 	        									userName,
	 	        									"Your_guide",
	 	        									String.valueOf(masterCircle.isEnabled));
    		}
    		
    		 isMasterCircle = true;
    	}
    }
   
   public void showClearAllPopUp(View view){
	   clearAllPopUp_Layout.setVisibility(View.VISIBLE);
   }
   
   //hides a view; used by multiple cancel buttons
   public void cancel(View view){
	   RelativeLayout rl = (RelativeLayout) view.getParent();
	   rl.setVisibility(View.GONE);
   }
   
   //hides a view
   public void cancel2(View view){
	   RelativeLayout rl = (RelativeLayout) view.getParent().getParent();
	   rl.setVisibility(View.GONE);
   }
   
   //deletes all points of interest (POI) from a map and DB
   public void clearAllCircles(View view) {
		if (mCircles != null)
		{
			for (DraggableCircle draggableCircle : mCircles) {
	            	//remove circle from a map
	            	draggableCircle.deleteCircle();	            	
	            }
		//remove all circle objects from a mCircles list
    	mCircles.clear();
        }
		
		//delete all circles from DB
		new DBQueryDeleteAllCircles().execute();
		//close clearAllPopUp layout
		clearAllPopUp_Layout.setVisibility(View.GONE);
   }
   
   public void decreaseRadius(View view)
   {
	   if (tempDraggableCircle != null)
	   {
		   tempDraggableCircle.decreaseRadius(); 
	   }
   }
   public void increaseRadius(View view)
   {
	   if (tempDraggableCircle != null)
	   {
		   tempDraggableCircle.increaseRadius(); 
	   }
   }
   
   private void setMyMACAddress()
   {
	   WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		String macAddressRaw = wifiInf.getMacAddress();
		myMacAddress = macAddressRaw.replace(":","");
   }
   
   //updates all circles (POI) and master circle in DB
   public void updateDB(View view)
   {
	   for (DraggableCircle draggableCircle : mCircles) {
		   
			String circleID = String.valueOf(draggableCircle.circleID);
			String centerLatitude = String.valueOf(draggableCircle.centerMarker.getPosition().latitude);
			String centerLongitude = String.valueOf(draggableCircle.centerMarker.getPosition().longitude);
			String radius = String.valueOf(draggableCircle.radius);
			String radiusLatitude = String.valueOf(draggableCircle.radiusMarker.getPosition().latitude);
			String radiusLongitude = String.valueOf(draggableCircle.radiusMarker.getPosition().longitude);
			String name = draggableCircle.centerMarker.getTitle();
			String description = draggableCircle.centerMarker.getSnippet();
			
			
           	//apdate circle in the DB
           	new DBQueryUpdateCircle().execute(circleID,centerLatitude,centerLongitude,radius,radiusLatitude,radiusLongitude,name,description);

       }
	   
   }
   
   //create periodic task that updates users location on the map
   Runnable statusChecker = new Runnable()
   {
        @Override 
        public void run() {
        	new DBQueryAddUsers().execute();
            handler.postDelayed(statusChecker, refreshInterval);
        }
   };
   
   private void startUsersUpdateTask()
   {
       statusChecker.run(); 
   }

   private void stopUsersUpdateTask()
   {
       handler.removeCallbacks(statusChecker);
   }
   //end of periodic task that updates users location on the map
  
   
  //create periodic task that updates users location on the map
   Runnable distanceChecker = new Runnable()
   {
        @Override 
        public void run() {
        	checkIfUsersWithinAllowableDistance();
            handler2.postDelayed(distanceChecker, refreshInterval);
        }
   };
   
   private void startDistanceCheckerTask()
   {
	   distanceChecker.run(); 
   }

   private void stopDistanceCheckerTask()
   {
       handler2.removeCallbacks(distanceChecker);
   }
   //end of periodic task that updates users location on the map
   
   private void checkIfUsersWithinAllowableDistance()
   {
	   //clear alert users List
	   alertUsersList.clear();
	   
	   //check if users are in rage of master circle or other circles (points of interest)
	   for (TrackedUser trackedUser : trackedUsersList) {
		   if (mCircles.size() > 0)
		   {
			   for (DraggableCircle draggableCircle : mCircles) {
				   double distanceBtwUserAndMarker = toRadiusMeters(draggableCircle.centerMarker.getPosition(), trackedUser.userMarker.getPosition());
				    
				   if (draggableCircle.radius > distanceBtwUserAndMarker)
				   {
					   usersAreInRange = true;
					   break;
				   }
				   else{
					   if (masterCircle != null && masterCircle.isEnabled == 1)
					   {
						   double distanceBtwUserAndMaster = toRadiusMeters(draggableCircle.centerMarker.getPosition(), masterCircle.circle.getCenter());
   
						   if(masterCircle.radius > distanceBtwUserAndMaster)
						   {
							   usersAreInRange = true;
							   break;
						   }
					   }
					   else{
						   usersAreInRange = false;
						   alertUsersList.add(trackedUser);
						   break;  
					   }  
				   } 
		       }
		   }
		   else
		   {
			   if (masterCircle != null && masterCircle.isEnabled == 1)
			   {
				   double distanceBtwUserAndMaster = toRadiusMeters(trackedUser.userMarker.getPosition(), masterCircle.circle.getCenter());

				   if(masterCircle.radius > distanceBtwUserAndMaster)
				   {
					   usersAreInRange = true;
				   }
				   else
				   {
					   usersAreInRange = false;
					   alertUsersList.add(trackedUser);
				   }
			   }
		   }
		   
	   }
	   
	   
	   if (alertUsersList.size()>0)
	   {
		   alertPopUp_Layout.setVisibility(View.VISIBLE);
		   numOfUsersOutsideTheRange_TextView.setText("Users out of range: " + String.valueOf(alertUsersList.size()));
		   playAlert();
	   }
	   
	   //change user icon to red/green if user is outside the range
	   chageTrackedUserIcon();
	   //System.out.println("Num of users outside the range: " + alertUsersList.size());
	   
   }
   
    
    private static final double RADIUS_OF_EARTH_METERS = 6371009;
    private static final int RADIUS = 100;
    private static final double decreaseIncreaseRadius = 10;

   
    private MasterCircle masterCircle;
    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);
    private DraggableCircle tempDraggableCircle; //used as a reference by change radius buttons
    private DraggableCircle lastAddedCircle; // points to last added circle
    private int circleCounterID = 0;
    
    //class that is used for point of interest
    public class DraggableCircle {
        public final Marker centerMarker;
        public final Marker radiusMarker;
        public final Circle circle;
        public double radius;
        public float circleBorderWidth = 6;
        public int circleBorderColour = Color.BLUE;
        public int circleFillColour = Color.HSVToColor(10, new float[] {10, 1, 1});
        public int circleID;
        
        public DraggableCircle(LatLng center, double radius) {
            this.radius = radius;
            centerMarker = mMap.addMarker(new MarkerOptions()
            		.position(center)
            		.draggable(false)
            		.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_small)));
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(toRadiusLatLng(center, radius))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.radius)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(circleBorderWidth)
                    .strokeColor(circleBorderColour)
                    .fillColor(circleFillColour));
            this.circleID = circleCounterID;
            circleCounterID += 1;
        }
        public DraggableCircle(LatLng center, LatLng radiusLatLng,String name,String description) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true)
                    .title(name)
		            .snippet(description)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_small)));
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(radiusLatLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.radius)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(circleBorderWidth)
                    .strokeColor(circleBorderColour)
                    .fillColor(circleFillColour));
            this.circleID = circleCounterID;
            circleCounterID += 1;
        }
        public DraggableCircle(int circleID, LatLng center, LatLng radiusLatLng,String name,String description) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true)
                    .title(name)
		            .snippet(description)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_small)));
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(radiusLatLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.radius)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(circleBorderWidth)
                    .strokeColor(circleBorderColour)
                    .fillColor(circleFillColour));
            
            if ( circleCounterID < circleID)
            {
            	circleCounterID = circleID;
            }
            
            
            this.circleID = circleCounterID;
            circleCounterID += 1;
        }
        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(centerMarker)) {
                circle.setCenter(marker.getPosition());
                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
                return true;
            }
            if (marker.equals(radiusMarker)) {
                 radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
                 circle.setRadius(radius);
                 return true;
            }
            return false;
        }
        public boolean onCenterMarkerClick(Marker marker) {
            if (marker.equals(centerMarker)) {
                return true;
            }
          return false;
        }
        public void deleteCircle() {
            this.centerMarker.remove();
            this.radiusMarker.remove();
            this.circle.remove();
        }
        public void decreaseRadius(){
        	if (0 < radius - decreaseIncreaseRadius)
        	radius = radius - decreaseIncreaseRadius;
        	circle.setRadius(radius);
        	radiusMarker.setPosition(toRadiusLatLng(centerMarker.getPosition(), radius));
        }
        public void increaseRadius(){
        	radius = radius + decreaseIncreaseRadius;
        	circle.setRadius(radius);
        	radiusMarker.setPosition(toRadiusLatLng(centerMarker.getPosition(), radius));
        }
        
    }
    
    //*****MASTER CIRCLE*******; my location with a circle of specific radius
    private class MasterCircle {
        private final Marker radiusMarker;
        private final Circle circle;
        private double radius;
        private float circleBorderWidth = 5;
        private int circleBorderColour = Color.BLUE;
        private int circleFillColour = Color.HSVToColor(100, new float[] {200, 1, 1});
        public int isEnabled = 0;
        
        
        public MasterCircle(LatLng center, LatLng radiusLatLng) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(radiusLatLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.radius)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(circleBorderWidth)
                    .strokeColor(circleBorderColour)
                    .fillColor(circleFillColour));
        }
        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(radiusMarker)) {
            	Location myLocation = mMap.getMyLocation();
            	LatLng myLatLng = new LatLng (myLocation.getLatitude(),myLocation.getLongitude());
                 radius = toRadiusMeters(myLatLng, radiusMarker.getPosition());
                 circle.setRadius(radius);
                 return true;
            }
            return false;
        }
        public void onLocationMoved(LatLng center) {
                 circle.setCenter(center);
                 radiusMarker.setPosition(toRadiusLatLng(center, radius));
                 
             }
        	
        public void deleteMasterCircle() {
            this.radiusMarker.remove();
            this.circle.remove();
        }   
    }
    
    
    
    //return radius marker location (lat, long) based on center marker location and radius distance
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }
    
    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }
    

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub
		if (masterCircle != null)
		{
			System.out.println("Update master circle");
			masterCircle.onLocationMoved(new LatLng(location.getLatitude(),location.getLongitude()));
			
			//updated master circle in DB
			new DBQueryUpdateMasterCircle().execute(myMacAddress,
						String.valueOf(location.getLatitude()),
						String.valueOf(location.getLongitude()),
						String.valueOf(masterCircle.radius),
						String.valueOf(masterCircle.radiusMarker.getPosition().latitude),
						String.valueOf(masterCircle.radiusMarker.getPosition().longitude),
						userName,
						"Your_guide",
						"1");
			
		}
		
	}

	@Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker) {
        for (DraggableCircle draggableCircle : mCircles) {
            if (draggableCircle.onMarkerMoved(marker)) {
                break;
            }
        }
        if(masterCircle != null)
        {
        	masterCircle.onMarkerMoved(marker);
        }
        
    }

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		//if delete circle button is active then delete circle by clicking on the center marker
		if (deleteCircle)
		{
			for (DraggableCircle draggableCircle : mCircles) {
	            if (draggableCircle.onCenterMarkerClick(marker)){
	            	//remove circle from a map
	            	draggableCircle.deleteCircle();
	            	//remove circle object from a mCircles list
	            	mCircles.remove(draggableCircle);
	            	
	            	//remove circle from the DB
	            	new DBQueryDeleteCircle().execute(String.valueOf(draggableCircle.circleID));
	                break;
	            }
	        }

		}
		else
		{
			//show radius - + buttons if center marker clicked
			for (DraggableCircle draggableCircle : mCircles) {
	            if (draggableCircle.onCenterMarkerClick(marker)){
	            	tempDraggableCircle = draggableCircle;
	            	radiusButtons_Layout.setVisibility(View.VISIBLE);
	                break;
	            }
	        }
			
		}
		
		
		
		return false;
	}
	
	public void addCircleToAMap(View view)
	{
		
		Point pointOnScreen = mMap.getProjection().toScreenLocation(circleToBeAddedLocation);     
        LatLng radiusLatLng = mMap.getProjection().fromScreenLocation(new Point(pointOnScreen.x + RADIUS, pointOnScreen.y));
        String name = circleName_EditText.getText().toString();
        String description = circleDescription_EditText.getText().toString();
        // ok create it
        lastAddedCircle = new DraggableCircle(circleToBeAddedLocation, radiusLatLng, name, description);
        mCircles.add(lastAddedCircle);
        
        //close pop up window
        RelativeLayout rl = (RelativeLayout) view.getParent();
        rl.setVisibility(View.GONE);
        //clear text fields
        circleName_EditText.setText("");
        circleDescription_EditText.setText("");
        
        //add circle to the DB
        String circleID = String.valueOf(lastAddedCircle.circleID);
		String centerLatitude = String.valueOf(lastAddedCircle.centerMarker.getPosition().latitude);
		String centerLongitude = String.valueOf(lastAddedCircle.centerMarker.getPosition().longitude);
		String radius = String.valueOf(lastAddedCircle.radius);
		String radiusLatitude = String.valueOf(lastAddedCircle.radiusMarker.getPosition().latitude);
		String radiusLongitude = String.valueOf(lastAddedCircle.radiusMarker.getPosition().longitude);
		
		
		String[] circleData = {circleID,centerLatitude,centerLongitude,radius,radiusLatitude,radiusLongitude,name,description};
        
		//add point off interest to a DB
		new DBQueryAddCircle().execute(circleData);
	}
	
	@Override
	public void onMapClick(LatLng location) {
		// TODO Auto-generated method stub
		if(putCircle)
		{	
			circleToBeAddedLocation = location;
			pointOfInterestPopUp_Layout.setVisibility(View.VISIBLE);
		}
		//hide change radius buttons
		tempDraggableCircle = null;
    	radiusButtons_Layout.setVisibility(View.GONE);
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		
		
		sendMsg_Layout.setVisibility(View.VISIBLE);
		sendMsgToUserName_TextView.setText(marker.getTitle());
		lastMarkerClicked = marker;
	}
	
	//sends message to a user
	public void sendMsgTo(View view)
	{
		String message = sendMsgTo_EditText.getText().toString();
		String userMacAddress;
		String isNewMsg = "1";
		
		//find user's mac address
		for (TrackedUser oneTrackedUser : trackedUsersList) {
			//if mac exists then updated data
            if (oneTrackedUser.userMarker.equals(lastMarkerClicked) ){
            	
            	userMacAddress = oneTrackedUser.macAddress;
            	System.out.println("User mac address: " + userMacAddress);
            	
            	new DBQuerySendMsgTo().execute(userMacAddress,message,isNewMsg);
            	sendMsg_Layout.setVisibility(View.GONE);
            	break;
            }
        }
		
		//clear text message area
		sendMsgTo_EditText.setText("");
	}
	
	public void dismissAlert(View view){
		RelativeLayout rl = (RelativeLayout) view.getParent();
		rl.setVisibility(View.GONE);
		stopPlayAlert();
	}
	private void playAlert(){
		ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
		ringtone.play();
	}
	private void stopPlayAlert(){
		ringtone.stop();
	}
	
	//*****Tracked Users*******
    private class TrackedUser {
        private Marker userMarker;
        private String userName;
        private String macAddress;
        private int batteryCharge;

        //private double latitude = 0;
        //private double longitude = 0;
       
        
        public TrackedUser(String name, String mac, int battery, double latitude, double longitude) {
        	this.userName = name;
        	this.macAddress = mac;
        	this.batteryCharge = battery;
        	
        	userMarker = mMap.addMarker(new MarkerOptions()
			            .position(new LatLng(latitude,longitude))
			            .draggable(false)
			            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
			            .title(name)
		                .snippet("Battery: " + String.valueOf(battery) + "%"));
        }
        public void setLocation(double latitude,double longitude)
        {
        	userMarker.setPosition(new LatLng(latitude,longitude));
        }
        
        public void inRange()
        {
        	userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.user));
        }
        public void outOfRange()
        {
        	userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.useroutofrange));
        }
    
    }
    
    private void chageTrackedUserIcon()
    {
    	for (TrackedUser trackedUser : trackedUsersList) {
    		trackedUser.inRange();
    		
    		for (TrackedUser alertUser : alertUsersList) {
        		
    			if (trackedUser.equals(alertUser))
    			{
    				trackedUser.outOfRange();
    				break;
    			}	
        	}
    		
    		
    	}
    }
    
    //Retrieve users from DB and show them on the map
    private class DBQueryAddUsers extends AsyncTask<Void,Void,String>
	{
					
			@Override
			protected String doInBackground(Void...param) {
				// TODO Auto-generated method stub
				String query = "select_users_tracked_by_me.php?mac_address_tracked_by=" + myMacAddress;	
				String queryResult = GetRequest.getData(query, getApplicationContext());
				//System.out.println("Query : = " + query);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 //System.out.println("Query Result = " + queryResult);
				try {
					JSONArray jsonArr = new JSONArray(queryResult);
					String userName = "";
					String mac_address = "";
					int batteryCharge = 0;
					double latitude = 0;
					double longitude = 0;
					
					int jsonArrLength = jsonArr.length();
					//ass users toDBQueryAddCircle a list or updated users' location 
					if (trackedUsersList.size() == 0)
					{
						for (int i =0; i < jsonArrLength; i++)
						{
							
							userName = jsonArr.getJSONObject(i).getString("user_name");
							mac_address = jsonArr.getJSONObject(i).getString("mac_address");
							batteryCharge = Integer.parseInt(jsonArr.getJSONObject(i).getString("battery_charge"));
							latitude = Double.valueOf(jsonArr.getJSONObject(i).getString("gps_latitude"));
							longitude = Double.valueOf(jsonArr.getJSONObject(i).getString("gps_longitude"));
							trackedUsersList.add(new TrackedUser(userName, mac_address, batteryCharge, latitude, longitude));
							System.out.println("User created: " + userName);
						}
					}
					else{
						for (int i =0; i < jsonArrLength; i++)
						{
							userName = jsonArr.getJSONObject(i).getString("user_name");
							mac_address = jsonArr.getJSONObject(i).getString("mac_address");
							batteryCharge = Integer.parseInt(jsonArr.getJSONObject(i).getString("battery_charge"));
							latitude = Double.valueOf(jsonArr.getJSONObject(i).getString("gps_latitude"));
							longitude = Double.valueOf(jsonArr.getJSONObject(i).getString("gps_longitude"));
							//
							for (TrackedUser oneTrackedUser : trackedUsersList) {
								//if mac exists then updated data
					            if (oneTrackedUser.macAddress.equals(mac_address) ){
					            	//update location
					            	oneTrackedUser.setLocation(latitude,longitude);
					            	System.out.println("User updated: " + userName);
					            }
					        }
						}
						
						
					}
						
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					System.out.println("Error with JSON object");
					e.printStackTrace();
				}
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
    
    
  //Retrieve users from DB and show them on the map
    private class DBQueryAddCircle extends AsyncTask<String[],Void,String>
	{
					
			@Override
			protected String doInBackground(String[]...param) {
				// TODO Auto-generated method stub
				
				
				String query = "add_circle.php?mac_address=" + myMacAddress +
						"&circle_id=" + param[0][0] +
						"&center_latitude=" + param[0][1] + 
						"&center_longitude=" + param[0][2] +
						"&radius=" + param[0][3] +
						"&radius_latitude=" + param[0][4] +
						"&radius_longitude=" + param[0][5] +
						"&name=" + param[0][6] +
						"&description=" + param[0][7] + "&";
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query : = " + query);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 System.out.println("Query Result = " + queryResult);
				
				if (queryResult.equals("Success"))
				{
					Toast.makeText(getApplicationContext(), "Point added to a DB", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Failed to add point to a DB", Toast.LENGTH_SHORT).show();
				}
					
					
				
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
    
  //add Master circles to a DB
    private class DBQueryAddMasterCircle extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub

				String query = "add_master_circle.php?" +
						"mac_address=" + param[0] +
						"&center_latitude=" + param[1] + 
						"&center_longitude=" + param[2] +
						"&radius=" + param[3] +
						"&radius_latitude=" + param[4] +
						"&radius_longitude=" + param[5] +
						"&name=" + param[6] +
						"&description=" + param[7] + 
						"&is_enabled=" + param[8];
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query : = " + query);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 /*System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
					}
					*/
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
    
  //update Master circles to a DB
    private class DBQueryUpdateMasterCircle extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub

				String query = "update_master_circle.php?" +
						"mac_address=" + param[0] +
						"&center_latitude=" + param[1] + 
						"&center_longitude=" + param[2] +
						"&radius=" + param[3] +
						"&radius_latitude=" + param[4] +
						"&radius_longitude=" + param[5] +
						"&name=" + param[6] +
						"&description=" + param[7] +
						"&is_enabled=" + param[8];
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query : = " + query);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 /*System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Success to updated Master", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Failure to update Master", Toast.LENGTH_SHORT).show();
					}
					*/
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
  
  //update circles in a DB
    private class DBQueryUpdateCircle extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub

				String query = "update_circle.php?" +
						"mac_address=" + myMacAddress +
						"&circle_id=" + param[0] +
						"&center_latitude=" + param[1] + 
						"&center_longitude=" + param[2] +
						"&radius=" + param[3] +
						"&radius_latitude=" + param[4] +
						"&radius_longitude=" + param[5] +
						"&name=" + param[6] +
						"&description=" + param[7] + 
						"&";
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query : = " + query);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Success to updated cirlce", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Failure to update circle", Toast.LENGTH_SHORT).show();
					}
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
  //Retrieve users from DB and show them on the map
    private class DBQueryGetCircles extends AsyncTask<String[],Void,String>
	{
					
			@Override
			protected String doInBackground(String[]...param) {
				// TODO Auto-generated method stub
				
				
				String query = "select_all_circles.php?mac_address=" + myMacAddress;
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query : = " + query);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				// System.out.println("Query Result = " + queryResult);
				
				 try {
						JSONArray jsonArr = new JSONArray(queryResult);
						int circleID;
						double center_latitute;
						double center_longitude;
						double radius;
						double radius_latitude;
						double radius_longitude;
						String name;
						String description;
						
						
						int jsonArrLength = jsonArr.length();
						for (int i =0; i < jsonArrLength; i++)
						{
							
							circleID = Integer.parseInt(jsonArr.getJSONObject(i).getString("circle_id"));
							center_latitute = Double.valueOf(jsonArr.getJSONObject(i).getString("center_marker_latitude"));
							center_longitude = Double.valueOf(jsonArr.getJSONObject(i).getString("center_marker_longitude"));
							radius = Double.valueOf(jsonArr.getJSONObject(i).getString("radius"));
							radius_latitude = Double.valueOf(jsonArr.getJSONObject(i).getString("radius_marker_latitude"));
							radius_longitude = Double.valueOf(jsonArr.getJSONObject(i).getString("radius_marker_longitude"));
							name = jsonArr.getJSONObject(i).getString("name");
							description = jsonArr.getJSONObject(i).getString("description");
							
							if (description.equals("null"))
							{
								description = "";
							}
							
							
							mCircles.add(new DraggableCircle(circleID,
											new LatLng(center_latitute,center_longitude),
											new LatLng(radius_latitude,radius_longitude),
											name,
											description));
						}					
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						System.out.println("Error with JSON object");
						e.printStackTrace();
					}
	
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
    
  //delete circle from DB
    private class DBQueryDeleteCircle extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub
				
				
				String query = "delete_circle.php?mac_address=" + myMacAddress + "&circle_id=" + param[0];
				System.out.println("Query : = " + query);
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query Result doInB = " + queryResult);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Deleted from DB", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Failed to delete from DB", Toast.LENGTH_SHORT).show();
					}
	
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
    
  //delete all circles from DB
    private class DBQueryDeleteAllCircles extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub
				
				
				String query = "delete_all_circles.php?mac_address=" + myMacAddress;
				System.out.println("Query : = " + query);
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query Result doInB = " + queryResult);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
					}
	
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
    
  //delete circle from DB
    private class DBQueryDeleteMasterCircle extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub
				
				
				String query = "delete_master_circle.php?mac_address=" + myMacAddress;
				System.out.println("Query : = " + query);
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query Result doInB = " + queryResult);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Master deleted from DB", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Master not deleted from DB", Toast.LENGTH_SHORT).show();
					}
	
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}

  //delete circle from DB
    private class DBQueryEnableDisableMasterCircle extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String...param) {
				// TODO Auto-generated method stub
				
				
				String query = "enable_master_circle.php?mac_address=" + myMacAddress +
														"&is_enabled=" + param[0];
				System.out.println("Query : = " + query);
				
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query Result doInB = " + queryResult);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 System.out.println("Query Result = " + queryResult);
				
				 if (queryResult.equals("Success"))
					{
						Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
					}
	
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}


    
    private class DBQuerySendMsgTo extends AsyncTask<String,Void,String>
   	{
   					
   			@Override
   			protected String doInBackground(String...param) {
   				// TODO Auto-generated method stub
   				
   				
   				String query = "send_message_to.php?mac_address=" + param[0] +
   														"&message=" + param[1] +
   														"&is_new_message=" + param[2] + "&";
   				System.out.println("Query : = " + query);
   				
   				String queryResult = GetRequest.getData(query, getApplicationContext());
   				
   				return queryResult;
   			}
   			
   			protected void onPostExecute(String queryResult)
   			{
   				 System.out.println("Query Result = " + queryResult);
   				
   				 if (queryResult.equals("Success"))
   					{
   						Toast.makeText(getApplicationContext(), "Msg sent", Toast.LENGTH_SHORT).show();
   					}
   					else
   					{
   						Toast.makeText(getApplicationContext(), "Msg failed", Toast.LENGTH_SHORT).show();
   					}
   	
   		    }
   			@Override
   	        protected void onPreExecute() {}

   	        @Override
   	        protected void onProgressUpdate(Void... values) {}
   	}
}
