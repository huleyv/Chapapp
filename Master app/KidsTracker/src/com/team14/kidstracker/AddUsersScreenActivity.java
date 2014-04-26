//Author: Volodymyr Huley

package com.team14.kidstracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team14.kidstracker.R;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddUsersScreenActivity extends Activity implements IImageClick {
	 ArrayAdapter<User> adapter;
	 List<User> list ;
	 String myMacAddress;
	 ListView addUsers_listView;
	 Button usersAddReload_button;
	 ProgressBar  users_progressBar;
	 WifiManager wifiMan;
	 
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_users_screen);
		addUsers_listView = (ListView) findViewById(R.id.usersAdd_ListView);
		usersAddReload_button = (Button) findViewById(R.id.usersAddReload_Button);
		users_progressBar = (ProgressBar) findViewById(R.id.usersAdd_ProgressBar);
		
		list = new ArrayList<User>();
		adapter = new AddUsersArrayAdapter(this,list,this);
		addUsers_listView.setAdapter(adapter);
		//adapter.notifyDataSetChanged();
		
		wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		String macAddressRaw = wifiInf.getMacAddress();
		myMacAddress = macAddressRaw.replace(":","");
		System.out.println("MAC inside Activity = " + myMacAddress);
		
		if (wifiMan.isWifiEnabled()){
			new DBQueryPopulateList().execute();
		}
		else
		{
			Toast.makeText(this, "Wi-Fi is OFF", Toast.LENGTH_SHORT).show();
		}
		
		
		usersAddReload_button.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	        	 //clear the list before reloading it
	        	  list.clear();
	        	  
	              // Perform action on click   		            		
	        	//WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	      		if (wifiMan.isWifiEnabled()){
	      			users_progressBar.setVisibility(ProgressBar.VISIBLE);
	      			new DBQueryPopulateList().execute();
	      		}
	      		else
	      		{
	      			Toast.makeText(getApplicationContext(), "Wi-Fi is OFF", Toast.LENGTH_SHORT).show();
	      		}
	          }
	      });
		
	
	}

	  
	public void goBack(View view)
	{
		finish();
	}
	

	@Override
	public void onImageClick(int position) {		
		if (wifiMan.isWifiEnabled()){
			if (!list.get(position).isAdded())
			new DBQueryAddUser().execute(position);
		}
		else
		{
			Toast.makeText(this, "Wi-Fi is OFF", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class DBQueryPopulateList extends AsyncTask<String,Void,String>
	{
					
			@Override
			protected String doInBackground(String... param) {
				// TODO Auto-generated method stub			
				String query = "select_all_users_not_being_tracked_by_me.php?mac_address_tracked_by=" + myMacAddress;	
				String queryResult = GetRequest.getData(query, getApplicationContext());
				System.out.println("Query result = " + queryResult);
				
				return queryResult;
			}
			
			protected void onPostExecute(String queryResult)
			{
				 //System.out.println("Query Result = " + result);
				
				try {
					JSONArray jsonArr = new JSONArray(queryResult);
					String userName = "";
					String mac_address = "";
					
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
			@Override
	        protected void onPreExecute() {
				users_progressBar.setVisibility(ProgressBar.VISIBLE);
			}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}
	
	private class DBQueryAddUser extends AsyncTask<Integer,Void,String[]>
	{
					
			@Override
			protected String[] doInBackground(Integer... param) {
				// TODO Auto-generated method stub
				//final ProgressBar  users_progressBar = (ProgressBar) findViewById(R.id.users_ProgressBar);
				//users_progressBar.setVisibility(ProgressBar.VISIBLE);
				Integer position = param[0];
				String mac_address_tracked = list.get(position).getMacAddress();
				String query = "insert_user_into_users_tracked.php?mac_address_tracked_by=" + myMacAddress + "&mac_address_tracked=" + mac_address_tracked;
				
				String[] posAndResult = new String[2];
				posAndResult[0] = Integer.toString(position);
				posAndResult[1] = GetRequest.getData(query,getApplicationContext());
				return posAndResult;
			}
			
			protected void onPostExecute(String[] posAndResult)
			{
				 //System.out.println("Inside onPost = " + result[0] + " " + result[1]);
				Integer position = Integer.parseInt(posAndResult[0]);
				System.out.println("Query Responce: " + posAndResult[1]);
				if(posAndResult[1].toString().equals("Success"))
				{
					list.get(position).setAdded(true);
					adapter.notifyDataSetChanged();	
				}
				else
				{
					Toast.makeText(AddUsersScreenActivity.this, "Add status: " + posAndResult[1], Toast.LENGTH_SHORT).show();
				}	
		    }
			@Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	}

}
