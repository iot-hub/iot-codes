package org.iothub.iot7939;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {
	
	
	
	public static final String SERVICE_CLASSNAME = "org.iothub.iot7939.Service7939";
    private Button button;
	
	static String LogMsgID0 = "iothub: ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		  button = (Button) findViewById(R.id.button1);
	      updateButton();
	}

	@Override
    protected void onResume() {
        super.onResume();
        updateButton();
    }
    
	private void updateButton() {
        if (serviceIsRunning()) {
            button.setText("Stop Service");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button.setText("Start Service");
                    stopService7939();
                    updateButton();
                }
            });

        } else {
            button.setText("Start Service");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button.setText("Stop Service");
                    startService7939();
                    updateButton();
                }
            });
        }
    }
	
	 // Method to start the service
	   private void startService7939() {
		   Log.d(LogMsgID0, "StartService");
	       startService(new Intent(getBaseContext(), Service7939.class));
	   } 

	   // Method to stop the service
	   public void stopService7939() {
		   Log.d(LogMsgID0, "StopService");
	      stopService(new Intent(getBaseContext(), Service7939.class));
	      finish();
	   }
	   
	   private boolean serviceIsRunning() {
	        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	            if (SERVICE_CLASSNAME.equals(service.service.getClassName())) {
	                return true;
	            }
	        }
	        return false;
	    }
	
}

