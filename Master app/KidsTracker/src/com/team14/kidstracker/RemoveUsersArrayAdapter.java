//Author: Volodymyr Huley

package com.team14.kidstracker;

import java.util.List;

import com.team14.kidstracker.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RemoveUsersArrayAdapter extends ArrayAdapter<User> {

	  private final List<User> list;
	  private final Activity context;
	  public TextView userName_textView;
	  public TextView userMACAddress_textView;
	  public ImageView userAdd_imageView;
	  public IImageClick callback;

	  public RemoveUsersArrayAdapter(Activity context, List<User> list, IImageClick callbackOnImageClicked) {
	    super(context, R.layout.adduserrow, list);
	    this.context = context;
	    this.list = list;
	    this.callback = callbackOnImageClicked;
	  }


	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		 
		if (convertView == null)
		{
		      LayoutInflater inflator = context.getLayoutInflater();
		      convertView = inflator.inflate(R.layout.adduserrow, null);      
		}
		  
		
		  
		userName_textView = (TextView) convertView.findViewById(R.id.userName_TextView);
		userMACAddress_textView = (TextView) convertView.findViewById(R.id.userMACAddress_TextView);
		userAdd_imageView = (ImageView) convertView.findViewById(R.id.userAdd_ImageView);
		
		
		
		  
		userName_textView.setText(list.get(position).getName());
		userMACAddress_textView.setText(list.get(position).getMacAddress());
	   	userAdd_imageView.setImageResource(R.drawable.delete_sign);
	   	    	
	    final int positionClicked = position;
	    userAdd_imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callback.onImageClick(positionClicked);
			}
		});
	    
	    return convertView;
	  }
	} 
