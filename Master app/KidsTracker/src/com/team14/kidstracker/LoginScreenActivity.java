//Author: Volodymyr Huley

package com.team14.kidstracker;

import com.team14.kidstracker.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);
		
		
		final Button login_Button = (Button) findViewById(R.id.login_Button);
		final TextView userName_TextBox = (TextView) findViewById(R.id.userName_EditText);
		
		//get user name input; return user name to the calling activity (MainScreenActivity)
		login_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if (userName_TextBox.length() == 0)
            	{
            		Context context = getApplicationContext();
            		CharSequence text = "You have to enter your name";
            		int duration = Toast.LENGTH_SHORT;

            		Toast.makeText(context, text, duration).show();
            	}
            	else
            	{    
            		            		
            		Intent resultData = new Intent();
            		resultData.putExtra("name", userName_TextBox.getText().toString());
            		
            		//Toast.makeText(getApplicationContext(), userName_TextBox.getText(), Toast.LENGTH_SHORT).show();
            	            		
            		if (getParent() == null) {
            		    setResult(Activity.RESULT_OK, resultData);
            		} else {
            		    getParent().setResult(Activity.RESULT_OK, resultData);
            		}
            		
            		finish();
            	}
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}

}
