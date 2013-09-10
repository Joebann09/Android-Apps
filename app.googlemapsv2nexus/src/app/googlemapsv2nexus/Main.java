package app.googlemapsv2nexus;


import java.util.ArrayList;

import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import app.googlemapsv2nexus.GMapV2Direction;

public class Main extends FragmentActivity {
	GoogleMap mMap;
    
	LatLng fromPosition = new LatLng(33.84781422, -84.37474045);
	LatLng toPosition = new LatLng(33.641203, -84.44456);
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		mMap = ((SupportMapFragment)getSupportFragmentManager()
						.findFragmentById(R.id.map))
						.getMap();

		LatLng coordinates = new LatLng(33.6411435, -84.4452815);		
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18));
		
		mMap.addMarker(new MarkerOptions()
				.position(fromPosition)
				.title("Start"));
		mMap.addMarker(new MarkerOptions()
				.position(toPosition)
				.title("End"));
		
		DirectionLines dirLine = new DirectionLines();
		dirLine.execute(mMap);
		
	}
	
	
	
	private class DirectionLines extends AsyncTask<GoogleMap, Void, PolylineOptions> {
		
		GoogleMap mMap;
		
		
		@Override
	    protected PolylineOptions doInBackground(GoogleMap... values) {
	    		    	
			Document doc = GMapV2Direction.getDocument(fromPosition,toPosition,GMapV2Direction.MODE_TRANSIT);	            
		    mMap = values[0];
					
			int duration = GMapV2Direction.getDurationValue(doc);
			String distance = GMapV2Direction.getDistanceText(doc);
			String start_address = GMapV2Direction.getStartAddress(doc);
			String copy_right = GMapV2Direction.getCopyRights(doc);
					
			ArrayList<LatLng> directionPoint = GMapV2Direction.getDirection(doc);
			PolylineOptions rectLine = new PolylineOptions()
										.width(8)
										.color(Color.BLUE);
					
			for(int i = 0 ; i < directionPoint.size() ; i++) {			
				rectLine.add(directionPoint.get(i));
			}
			
			return rectLine;
		}
		
		
	    @Override
	    protected void onPostExecute(PolylineOptions rectLine){
	    	
	    	ArrayList<LatLng> MarkerPoints =  GMapV2Direction.getMarkerPoints();
	    	
	    	
			for(int i = 0 ; i < MarkerPoints.size() ; i++) {			
				mMap.addMarker(new MarkerOptions()
				.position(MarkerPoints.get(i))
				.title("Here"));
			}
	    	
	    	mMap.addPolyline(rectLine);
		  
		  }
	   }
	}