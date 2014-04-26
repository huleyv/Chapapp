//Author: Volodymyr Huley

package com.team14.kidstracker;

import com.team14.kidstracker.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends Activity
{

	private static final Integer GET_USER_NAME_REQUEST_ID = 100;
	String userName;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		
		final TextView userName_View = (TextView) findViewById(R.id.userName_view);
		
		final Button map_Button = (Button) findViewById(R.id.map_button);
		final Button users_Button = (Button) findViewById(R.id.users_button);
		final Button exit_Button = (Button) findViewById(R.id.exit_button);
		
		
		//start Login Activity
		startLoginScreen();
		
		//open new activity (UsersScreenActivity) to add  users to a list
		users_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            		startUsersScreen();
            }
        });
		
		//open new activity (MonitorScreenActivity) to track users on a map
		map_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            			
            		startMonitorScreen();
            		
            }
        });
		
		//exit the app when Exit button is pressed
		exit_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
				}
		});	
	}
	
	//start map activity
	private void startMonitorScreen() {
		
		
		Intent launchMonitorScreen = new Intent(MainScreenActivity.this, MonitorScreenActivity.class);
		
		launchMonitorScreen.putExtra("userName", userName);
		startActivity(launchMonitorScreen);
		}
	
	//start addusers activity
	private void startUsersScreen() {
		
		
		Intent launchUsersScreen = new Intent(MainScreenActivity.this, UsersScreenActivity.class);
		startActivity(launchUsersScreen);
		}
	
	//start log in activity
	private void startLoginScreen() {
		Intent launchLoginScreen = new Intent(MainScreenActivity.this, LoginScreenActivity.class);
		startActivityForResult(launchLoginScreen,GET_USER_NAME_REQUEST_ID);
		}
	
	//start login screen and get user name
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      if (requestCode == GET_USER_NAME_REQUEST_ID) {
	          if (resultCode == RESULT_OK) {
	            
	        	userName = data.getStringExtra("name"); 
	           
	        	final TextView userName_View = (TextView) findViewById(R.id.userName_view);
	        	userName_View.setText("Hello " + userName);
	          }
	      }
	}

}
