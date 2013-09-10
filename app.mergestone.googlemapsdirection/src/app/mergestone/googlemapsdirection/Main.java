package app.mergestone.googlemapsdirection;

import java.util.ArrayList;

import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.app.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Main extends Activity {
	GoogleMap mMap;
    GMapV2Direction md;
    
	LatLng fromPosition = new LatLng(33.8484546, -84.3736199);
	LatLng toPosition = new LatLng(33.814424, -84.727577);
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
       md = new GMapV2Direction();
		mMap = ((MapFragment)getFragmentManager()
						.findFragmentById(R.id.map)).getMap();
		
		LatLng coordinates = new LatLng(33.83135685, -84.55059845);		
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));
		
		mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
		mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));
		
		Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
		int duration = md.getDurationValue(doc);
		String distance = md.getDistanceText(doc);
		String start_address = md.getStartAddress(doc);
		String copy_right = md.getCopyRights(doc);

		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
		
		for(int i = 0 ; i < directionPoint.size() ; i++) {			
			rectLine.add(directionPoint.get(i));
		}
		
		mMap.addPolyline(rectLine);
    }
}

