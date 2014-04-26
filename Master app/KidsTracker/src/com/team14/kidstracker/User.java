//Author: Volodymyr Huley

package com.team14.kidstracker;

import com.google.android.gms.maps.model.LatLng;

public class User {

	  private String name;
	  private String macAddress;
	  private String batteryCharge;
	  private double latitude, longitude;
	  private boolean added;
	  private boolean isOutsidePermArea;

	  public User(String name, String mac_address) {
	    this.name = name;
	    this.macAddress = mac_address;
	    added = false;
	  }
	  public User(String name,String mac_address, String battery_charge) {
		    this.name = name;
		    this.macAddress = mac_address;
		    added = false;
		  }

	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }
	  
	  public String getMacAddress() {
		    return macAddress;
		  }

	  public void setMacAddress(String macAddress) {
	    this.macAddress = macAddress;
	  }
	  
	  public String getBatteryCharge() {
		    return batteryCharge;
		  }

	  public void setBatteryCharge(String batteryCharge) {
	    this.batteryCharge = batteryCharge;
	  }

	  public boolean isAdded() {
	    return added;
	  }

	  public void setAdded(boolean added) {
	    this.added = added;
	  }
} 
