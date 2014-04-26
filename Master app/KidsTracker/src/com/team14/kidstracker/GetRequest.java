//Author: Volodymyr Huley

package com.team14.kidstracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class GetRequest {
 
	public static String getData(String query, Context callingActivity) {
		
		String sResponse ="";
		
		
		WifiManager wifi = (WifiManager) callingActivity.getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()){
			//wifi is enabled
			
			//Set parameters
		    HttpParams httpParameters = new BasicHttpParams();
		    // Set the timeout in milliseconds until a connection is established.
		    // The default value is zero, that means the timeout is not used. 
		    int timeoutConnection = 3000;
		    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		    // Set the default socket timeout (SO_TIMEOUT) 
		    // in milliseconds which is the timeout for waiting for data.
		    int timeoutSocket = 5000;
		    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		    
		    // Create a new HttpClient and Get Header
		    HttpClient httpclient = new DefaultHttpClient(httpParameters);
		    //DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		    
		    //replace all spaces with %20. this is done becaouse GET method does not allow spaces in the URL
		    query = query.replace(" ", "%20");
		    //HttpGet httpget = new HttpGet("http://www.vlad_vee.byethost7.com/CPS633WebApp/" + query );
		    
		    //For testing reason
		    try {
				URI resolved = new URI("http://www.vladvee.byethost7.com/CPS633WebApp/" + query );
				HttpGet httpget = new HttpGet(resolved);
			    //end testing reason
       
	
		        // Execute HTTP Get Request
		        HttpResponse response = httpclient.execute(httpget);
		        
		        //Read reply from server
		       
		        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));  
		        sResponse = reader.readLine();
		        System.out.println("GET responce: " + sResponse);
		        
		        
		        
		    } catch (ClientProtocolException e)
		    
		    {
		        // TODO Auto-generated catch block
		    	System.out.println("Client Protocol ex = ");
		    	e.printStackTrace();
		    } catch (SocketTimeoutException e)    {
		    	System.out.println("SocketTimeoutException = ");
		    	e.getMessage();
		    	
		    } catch (IllegalArgumentException e){
		    	System.out.println("IllegalArgumentException = ");
		    	e.getMessage();
		    }catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
		    	System.out.println("URISyntaxException = ");
				e1.printStackTrace();
			} catch (IOException e) {
		    
		        // TODO Auto-generated catch block
		    	System.out.println("IO exception = ");
		    	e.printStackTrace();
		    }
		}
		else{
				System.out.println("Wi-Fi is OFF");			
		}
		return sResponse;
	} 
	

}
