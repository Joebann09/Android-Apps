package de.vogella.android.locationapi.simple;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocationActivity extends FragmentActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
  private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0;
//Milliseconds per second
  private static final int MILLISECONDS_PER_SECOND = 1000;
  // Update frequency in seconds
  public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
  // Update frequency in milliseconds
  private static final long UPDATE_INTERVAL =
          MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
  // The fastest update frequency, in seconds
  private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
  // A fast frequency ceiling in milliseconds
  private static final long FASTEST_INTERVAL =
          MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

  
private TextView latituteField;
  private TextView longitudeField;
  private TextView altitudeField;
  private LocationManager locationManager;
  private String provider;
  LocationRequest mLocationRequest;
  boolean mUpdatesRequested;
  SharedPreferences mPrefs; 
  SharedPreferences.Editor mEditor;
  LocationClient mLocationClient;
  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_location);
    
    mLocationRequest = LocationRequest.create();
    // Use high accuracy
    mLocationRequest.setPriority(
            LocationRequest.PRIORITY_HIGH_ACCURACY);
    // Set the update interval to 5 seconds
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    // Set the fastest update interval to 1 second
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    
 // Open the shared preferences
    mPrefs = getSharedPreferences("SharedPreferences",
            Context.MODE_PRIVATE);
    // Get a SharedPreferences editor
    mEditor = mPrefs.edit();
    /*
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
    mLocationClient = new LocationClient(this, this, this);
    // Start with updates turned off
    mUpdatesRequested = false;


    
    latituteField = (TextView) findViewById(R.id.TextView02);
    longitudeField = (TextView) findViewById(R.id.TextView04);
    altitudeField = (TextView) findViewById(R.id.TextView06);

    // Get the location manager
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    // Define the criteria how to select the locatioin provider -> use
    // default
    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, false);
    Location location = locationManager.getLastKnownLocation(provider);

    // Initialize the location fields
    if (location != null) {
      System.out.println("Provider " + provider + " has been selected.");
      onLocationChanged(location);
    } else {
      latituteField.setText("Location not available");
      longitudeField.setText("Location not available");
      altitudeField.setText("Location not available");
    }
  }

  /* Request updates at startup */
  @Override
  protected void onStart() {
	  super.onStart();
      mLocationClient.connect();
  }
  
  /*
   * Called when the Activity is no longer visible at all.
   * Stop updates and disconnect.
   */
  @Override
  protected void onStop() {
      // If the client is connected
      if (mLocationClient.isConnected()) {
          /*
           * Remove location updates for a listener.
           * The current Activity is the listener, so
           * the argument is "this".
           */
          mLocationClient.removeLocationUpdates(this);
      }
      /*
       * After disconnect() is called, the client is
       * considered "dead".
       */
      mLocationClient.disconnect();
      super.onStop();
  }

  
  @Override
  protected void onResume() {
	  /*
       * Get any previous setting for location updates
       * Gets "false" if an error occurs
       */
      if (mPrefs.contains("KEY_UPDATES_ON")) {
          mUpdatesRequested =
                  mPrefs.getBoolean("KEY_UPDATES_ON", false);
       // Otherwise, turn off location updates
      } else {
          mEditor.putBoolean("KEY_UPDATES_ON", false);
          mEditor.commit();
      }

      super.onResume();	  
   // locationManager.requestLocationUpdates(provider, 400, 1, this);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
	// Save the current setting for updates
      mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
      mEditor.commit();
      super.onPause();

   // locationManager.removeUpdates(this);
  }

  @Override
  public void onLocationChanged(Location location) {
	  String msg = "Updated Location: " +
              Double.toString(location.getLatitude()) + "," +
              Double.toString(location.getLongitude());
	  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


    double lat =  location.getLatitude();
    double lng =  location.getLongitude();
    double alt =  location.getAltitude();
    latituteField.setText(String.valueOf(lat));
    longitudeField.setText(String.valueOf(lng));
    altitudeField.setText(String.valueOf(alt));
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProviderEnabled(String provider) {
    Toast.makeText(this, "Enabled new provider " + provider,
        Toast.LENGTH_SHORT).show();

  }

  @Override
  public void onProviderDisabled(String provider) {
    Toast.makeText(this, "Disabled provider " + provider,
        Toast.LENGTH_SHORT).show();
  }

@Override
public void onConnectionFailed(ConnectionResult connectionResult) {
	if (connectionResult.hasResolution()) {
        try {
            // Start an Activity that tries to resolve the error
            connectionResult.startResolutionForResult(
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
            * Thrown if Google Play services canceled the original
            * PendingIntent
            */
        } catch (IntentSender.SendIntentException e) {
            // Log the error
            e.printStackTrace();
        }
    } else {
        /*
         * If no resolution is available, display a dialog to the
         * user with the error.
         */
        showDialog(connectionResult.getErrorCode());
    }
	
}

@Override
public void onConnected(Bundle connectionHint) {
	 // Display the connection status
    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
 // If already requested, start periodic updates
    if (mUpdatesRequested) {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

	
}

@Override
public void onDisconnected() {
	// Display the connection status
    Toast.makeText(this, "Disconnected. Please re-connect.",
            Toast.LENGTH_SHORT).show();

	
}
} 