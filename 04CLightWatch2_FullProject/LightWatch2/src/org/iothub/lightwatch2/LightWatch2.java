package org.iothub.lightwatch2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class LightWatch2 extends Activity {
	
	String LogMsgID0 = "iothub: ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LogMsgID0, "The onCreate() event");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light_watch2);
		//Intent serviceIntent = new Intent(this, mService.class);
	    //startService(serviceIntent);
	    
		
	}

	// Method to start the service
	   public void startService(View view) {
		   Log.d(LogMsgID0, "StartService");
	      startService(new Intent(getBaseContext(), LightWatchService.class));
	   } 

	   // Method to stop the service
	   public void stopService(View view) {
	      stopService(new Intent(getBaseContext(), LightWatchService.class));
	      System.exit(0);
	   }
}

