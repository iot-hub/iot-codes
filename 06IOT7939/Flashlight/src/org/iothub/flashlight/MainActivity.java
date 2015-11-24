package org.iothub.flashlight;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
 private Camera camera;
 
 private final Context context = this;
 /**
  * @see android.app.Activity#onStop()
  */
 @Override
 protected void onStop() {
  super.onStop();
  if (camera != null) {
   camera.release();
  }
 }

 @Override
 protected void onStart() {
     super.onStart();
     Log.d("info", "On Start .....");
     final PackageManager pm = context.getPackageManager();
     if(!isCameraSupported(pm)){
      Log.d("info", "The device's doesn't support camera.");
      finish();
      
     } else {
   	  if (!isFlashSupported(pm)){
   		   Log.d("info", "The device's doesn't support flash.");
   		   finish();
   		   
   	  } else {
   		  camera = Camera.open();
   		  flashOnce();
   		  if (camera != null) {
   			   camera.release();
   			  }
   		  finish();
   	  }
     }
 }
 
 /**
  * @see android.app.Activity#onCreate(android.os.Bundle)
  */
 @Override
 public void onCreate(Bundle savedInstanceState) {
  Log.d("info", "OnCreate");
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  
  
 }
 private void flashOnce() {
	 Log.d("info", "flashOnce");
	 PackageManager pm=context.getPackageManager();
	 final Parameters p = camera.getParameters();
	 
	 Log.d("info", "torch is turn on!");
     p.setFlashMode(Parameters.FLASH_MODE_TORCH);
	 camera.setParameters(p);
	 camera.startPreview();
	 justWait(5000);
	 Log.i("info", "torch is turn off!");
	 p.setFlashMode(Parameters.FLASH_MODE_OFF);
	 camera.setParameters(p);
	 camera.stopPreview();
}

 public void justWait(int delay) {
		try {
		    Thread.sleep(delay);                 //1000 milliseconds is one second.
		 } catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		 }
		
	}
 
 private boolean isFlashSupported(PackageManager packageManager){ 
  // if device support camera flash?
  if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
   return true;
  } 
  return false;
 }

 private boolean isCameraSupported(PackageManager packageManager){
  // if device support camera?
  if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
   return true;
  } 
  return false;
 }
}