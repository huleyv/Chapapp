//Author: Volodymyr Huley

package com.team14.kidstracker;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

public class MockLocationSource implements LocationSource {

    private static final float ACCURACY = 1; // Meters
    private static final int MAX_SPEED = 1; // m/s
    private static final LatLng CENTER = new LatLng(43.842908,-79.539438);
    private static final double DELTA_LAT = 0.00001;
    private static final double DELTA_LON = 0.00001;

    private static final long UPDATE_PERIOD = TimeUnit.SECONDS.toMillis(15);

    private final Handler handler = new Handler();
    private LatLng lastCoordinate = CENTER;
    private OnLocationChangedListener listener;

    private void scheduleNewFix() {
        handler.postDelayed(updateLocationRunnable, UPDATE_PERIOD);
    }

    private final Runnable updateLocationRunnable = new Runnable() {

        @Override
        public void run() {
            Location randomLocation = generateRandomLocation();
            listener.onLocationChanged(randomLocation);
            System.out.println("mock is running");
            scheduleNewFix();
        }
    };

    public Location generateRandomLocation() {

        Location location = new Location(getClass().getSimpleName());
        location.setTime(System.currentTimeMillis());
        location.setAccuracy(ACCURACY);
        //location.setBearing(1);//randomizer.nextInt(360));
        location.setSpeed(MAX_SPEED);//randomizer.nextInt(MAX_SPEED));
        location.setLatitude(lastCoordinate.latitude + DELTA_LAT);//scaleOffset(DELTA_LAT));
        location.setLongitude(lastCoordinate.longitude + DELTA_LON);//+ scaleOffset(DELTA_LON));

        lastCoordinate = new LatLng(location.getLatitude(), location.getLongitude());

        return location;
    }

    private final static Random randomizer = new Random();

    private double scaleOffset(double value) {
        return (randomizer.nextDouble() - 0.5) * value;
    }

    @Override
    public void activate(OnLocationChangedListener locationChangedListener) {
        listener = locationChangedListener;
        scheduleNewFix();
    }

    @Override
    public void deactivate() {
        handler.removeCallbacks(updateLocationRunnable);
    }

}
