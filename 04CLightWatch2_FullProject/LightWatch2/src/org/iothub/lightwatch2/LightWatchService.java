package org.iothub.lightwatch2;

import java.io.IOException;


import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LightWatchService extends Service implements SensorEventListener {
	
	
	
	String LogMsgID = "iothub: ";
	Boolean Xmit;
	private SensorManager mSensorManager;
	private Sensor mLight;
	float mSensorData;
	
	String ControllerID = "xxx1@adastra.re"; // ID from where app can be controlled
	String ReporterID = "xxx3@adastra.re";   // ID where data will be sent to 
	String OwnUserID = "xxx2";
	String OwnPassword = "xxxxxxxx";
	String OurXMPPServer = "adastra.re";
	
	XMPPTCPConnectionConfiguration LWConnConfig = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(OwnUserID, OwnPassword)
            .setServiceName(OurXMPPServer)
            .setHost(OurXMPPServer)
            .setPort(5222)
            .setResource("chatIOT")
            .build();
    
    AbstractXMPPConnection LWConn = new XMPPTCPConnection(LWConnConfig);
   
   @Override
   public IBinder onBind(Intent arg0) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      // Let it continue running until it is stopped.
	   
	   mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	   mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	   
	   mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
	   
	   SmackConfiguration.setDefaultPacketReplyTimeout(10000); 
	   
	   new mConnect ().execute();
	   new mControl ().execute();
	    
      Toast.makeText(this, "LightWatch Service started", Toast.LENGTH_LONG).show();
      Log.d(LogMsgID, "LightWatch Service started");
      return START_STICKY;
   }
   
   @Override
   public void onDestroy() {
      super.onDestroy();
      Toast.makeText(this, "LightWatch Service Stopped", Toast.LENGTH_LONG).show();
      mSensorManager.unregisterListener(this);
      this.stopSelf();
   }
   
   private class mConnect extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	    	 
	    	 try {
	    		 LWConn.connect();
	    		 LWConn.login();
	 		} catch (SmackException | IOException | XMPPException e) {
	 			// TODO Auto-generated catch block
	 			Log.d(LogMsgID, "Failed to Connect");
	 			cancel(true);
	 		}	
	        return null;
	     }
	}
   
   private class mControl extends AsyncTask<Void, Void, Void> {
	   
	     protected Void doInBackground(Void...dummy) {
	 		ChatManager chatmanager1 = ChatManager.getInstanceFor(LWConn);
	 		Chat ChatIOT1 = chatmanager1.createChat(ControllerID, new ChatMessageListener() {
	 			public void processMessage(Chat chat, Message message) {
	 				String recdMsg = message.getBody().toLowerCase();
	 				Log.d(LogMsgID, recdMsg);	
	 				switch(recdMsg) {
	 				case "quit" : LWConn.disconnect();
	 				              Xmit = false;
					              break;
	 				case "xmit" : Xmit = true;
	 				              new mTransmit ().execute();
	 				              break;
	 				case "hold" : Xmit = false;
	 				              break;
	 				default     : Log.d(LogMsgID, "Illegal command");
	 				              break;
	 				}
	 				
	 			}
	 		});
	 		try {
	 	 		String payLoad = "Xmit, Hold or Quit? ";
	 			ChatIOT1.sendMessage(payLoad);
	 				
			} catch (NotConnectedException e1) {
				// TODO Auto-generated catch block
				Log.d(LogMsgID, "ChatIOT1 Not Connected");
				cancel(true);
			}
	        return null;
	     }
	 }
   
   private class mTransmit extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	     
	 		ChatManager chatmanager2 = ChatManager.getInstanceFor(LWConn);
	 		Chat ChatIOT2 = chatmanager2.createChat(ReporterID, new ChatMessageListener() {
	 			public void processMessage(Chat chat, Message message) {
	 				String recdMsg = message.getBody();
	 				//Log.d(LogMsgID, recdMsg);
	 				}
	 		});
	 		
	 		while (Xmit)
	 		{
	 			try {
	 				//Log.d(LogMsgID, "before Push");
	 				//Log.d(LogMsgID, String.valueOf(mSensorData));
	 	 			String payLoad = "PUSH " + String.valueOf(mSensorData);
	 				ChatIOT2.sendMessage(payLoad);
	 				
				} catch (NotConnectedException e1) {
					// TODO Auto-generated catch block
					Log.d(LogMsgID, "ChatIOT2 Not Connected");
					cancel(true);
				}
	 			try {
	 			    Thread.sleep(5000);                 //1000 milliseconds is one second.
	 			} catch(InterruptedException ex) {
	 			    Thread.currentThread().interrupt();
	 			}
	 		}
	        return null;
	     }

	 }
   
   @Override
	  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // Do something here if sensor accuracy changes.
	  }
	
	@Override
	  public final void onSensorChanged(SensorEvent event) {
	    // The light sensor returns a single value.
	    // Many sensors return 3 values, one for each axis.
	    float lux = event.values[0];
	    // Do something with this sensor value.
	    mSensorData = lux;
	    //Log.d(LogMsgID, String.valueOf(lux));
	  }
	
	
}
