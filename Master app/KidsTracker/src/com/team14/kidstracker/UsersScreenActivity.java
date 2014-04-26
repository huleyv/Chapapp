//Author: Volodymyr Huley

package com.team14.kidstracker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team14.kidstracker.R;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class UsersScreenActivity extends Activity implements IImageClick {
	ArrayAdapter<User> adapter;
	List<User> list ;
	String myMacAddress;
	Integer itemNumber;
	Button addUsers_button, usersReload_button, usersBack_Button;
	ListView users_listView;
	ProgressBar  users_progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usersscreen);
		// Show the Up button in the action bar.
		//setupActionBar();
		
		//final ListView users_listView = (ListView) findViewById(R.id.users_ListView);
		addUsers_button = (Button) findViewById(R.id.addUsers_Button);
		usersReload_button = (Button) findViewById(R.id.usersReload_Button);
		usersBack_Button = (Button) findViewById(R.id.usersBack_Button);
		users_listView = (ListView) findViewById(R.id.users_ListView);
		users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
		
		//set up adapter for list view
		list = new ArrayList<User>();
		adapter = new RemoveUsersArrayAdapter(this,list,this);
		users_listView.setAdapter(adapter);
		
		WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		String macAddressRaw = wifiInf.getMacAddress();
		myMacAddress = macAddressRaw.replace(":","");
		
		
	  addUsers_button.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              // Perform action on click   		            		
        	  startAddUsersScreen();
          }
      });
	  
	  usersReload_button.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              // Perform action on click   		            		
        	  populateTheList();
          }
      });
	    
	  populateTheList();
       
	    
	}
	public void populateTheList()
	{
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()){
			new DBQueryPopulateList().execute();
		}
		else
		{
			Toast.makeText(this, "Wi-Fi is OFF", Toast.LENGTH_SHORT).show();
		}	
	}
	public void goBack(View view)
	{
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usersscreen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class DBQueryPopulateList extends AsyncTask<String,Void,String>
	{
		@Override
        protected void onPreExecute() {
			users_progressBar.setVisibility(ProgressBar.VISIBLE);
		}
					
		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub			
			
			String query = "select_users_tracked_by_me.php?mac_address_tracked_by=" + myMacAddress;
			return GetRequest.getData(query,getApplicationContext()); 
		}
		
		protected void onPostExecute(String queryResult)
		{			
			System.out.println("Post Result: " + queryResult);
			
			try {
				JSONArray jsonArr = new JSONArray(queryResult);
				String userName = "";
				String mac_address = "";
				list.clear();
				int jsonArrLength = jsonArr.length();
				for (int i =0; i < jsonArrLength; i++)
				{
					userName = jsonArr.getJSONObject(i).getString("user_name");
					mac_address = jsonArr.getJSONObject(i).getString("mac_address");
					list.add(new User(userName,mac_address));				
				}					
				adapter.notifyDataSetChanged();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("Error with JSON object");
				e.printStackTrace();
			}
		  users_progressBar.setVisibility(ProgressBar.INVISIBLE);
	    }
	}	
	
	private void startAddUsersScreen() {
		Intent launchAddUsersScreen = new Intent(UsersScreenActivity.this, AddUsersScreenActivity.class);
		startActivity(launchAddUsersScreen);
		}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    populateTheList();

	    
	}
	@Override
	public void onImageClick(int position) {
		// TODO Auto-generated method stub
		itemNumber = position;
		removeUser();
	}
	
	public void removeUser()
	{
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()){
			new DBQueryRemoveUser().execute();
		}
		else
		{
			Toast.makeText(this, "Wi-Fi is OFF", Toast.LENGTH_SHORT).show();
		}	
		
	}
	
	private class DBQueryRemoveUser extends AsyncTask<String,Void,String>
	{
		@Override
        protected void onPreExecute() {
			users_progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub			
			String mac_address_tracked = list.get(itemNumber.intValue()).getMacAddress();
			String query = "remove_tracked_user_tracked_by.php?mac_address_tracked_by=" + myMacAddress + "&mac_address_tracked=" + mac_address_tracked;
			System.out.println("Query: " + query);
			return GetRequest.getData(query,getApplicationContext()); 
		}
		
		protected void onPostExecute(String queryResult)
		{
			
			
			System.out.println("Post Result: " + queryResult);
			
			if(queryResult.equals("Success"))
			{
				System.out.println("List Length before: " + list.size());
				list.remove(itemNumber.intValue());
				adapter.notifyDataSetChanged();
				System.out.println("List Length after: " + list.size());
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Failed to remove", Toast.LENGTH_SHORT).show();
			}
			users_progressBar.setVisibility(ProgressBar.INVISIBLE); 			
	    }
	}

}


