package com.vogella.android.first;

//import de.vogella.android.locationapi.simple.R;
import java.util.Arrays;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView latitudeField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		latitudeField = (TextView) findViewById(R.id.TextView01);
		latitudeField.setText(getDeviceId(null));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public static String getDeviceId(Context context) {
	    String id = getUniqueID(context);
	    if (id == null)
	        id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	    return id;
	}

	private static String getUniqueID(Context context) {

	    String telephonyDeviceId = "NoTelephonyId";
	    String androidDeviceId = "NoAndroidId";

	    // get telephony id
	    try {
	        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        telephonyDeviceId = tm.getDeviceId();
	        if (telephonyDeviceId == null) {
	            telephonyDeviceId = "NoTelephonyId";
	        }
	    } catch (Exception e) {
	    }

	    // get internal android device id
	    try {
	        androidDeviceId = android.provider.Settings.Secure.getString(context.getContentResolver(),
	                android.provider.Settings.Secure.ANDROID_ID);
	        if (androidDeviceId == null) {
	            androidDeviceId = "NoAndroidId";
	        }
	    } catch (Exception e) {

	    }

	    // build up the uuid
	    try {
	        String id = getStringIntegerHexBlocks(androidDeviceId.hashCode())
	                + "-"
	                + getStringIntegerHexBlocks(telephonyDeviceId.hashCode());

	        return id;
	    } catch (Exception e) {
	        return "0000-0000-1111-1111";
	    }
	}

	public static String getStringIntegerHexBlocks(int value) {
	    String result = "";
	    String string = Integer.toHexString(value);

	    int remain = 8 - string.length();
	    char[] chars = new char[remain];
	    Arrays.fill(chars, '0');
	    string = new String(chars) + string;

	    int count = 0;
	    for (int i = string.length() - 1; i >= 0; i--) {
	        count++;
	        result = string.substring(i, i + 1) + result;
	        if (count == 4) {
	            result = "-" + result;
	            count = 0;
	        }
	    }

	    if (result.startsWith("-")) {
	        result = result.substring(1, result.length());
	    }

	    return result;
	}

}
