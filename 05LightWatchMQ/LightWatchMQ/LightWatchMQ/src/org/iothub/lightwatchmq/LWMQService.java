package org.iothub.lightwatchmq;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

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
import android.provider.Settings.Secure;

public class LWMQService extends Service implements SensorEventListener {
	
	String LogMsgID = "iothub: ";
	Boolean Xmit;
	int PubLimit = 50;
	
	private SensorManager mSensorManager;
	private Sensor mLight;
	float mSensorData;
	
	String BROKER_URL = "tcp://broker.hivemq.com:1883";
	String TOPIC = "iothub";
	//String ClientID = MqttClient.generateClientId();
	String ClientID ;
	
	private MqttClient client;
	
   @Override
   public IBinder onBind(Intent arg0) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
	   
	   mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	   mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	   mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
		
	   ClientID = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 
	  
	   try {
      
           client = new MqttClient(BROKER_URL, ClientID, new MemoryPersistence());
       } catch (MqttException e) {
           e.printStackTrace();
           System.exit(1);
       }
	   new mConnect ().execute();
	   new mTransmit ().execute();
	   
      Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
      Log.d(LogMsgID, "LightWatch Service started");
      return START_STICKY;
   }
   
   @Override
   public void onDestroy() {
      super.onDestroy();
      Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
      try {
		client.disconnect();
	} catch (MqttException e) {
		// TODO Auto-generated catch block
		Log.d(LogMsgID, "disConnect Failed");
		e.printStackTrace();
	}
      mSensorManager.unregisterListener(this);
      this.stopSelf();
      return;
   }
   
   private class mConnect extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	    	 try {
	             client.connect();
	            
	         } catch (MqttException e) {
	             e.printStackTrace();
	             Log.d(LogMsgID, "Connect Failed");
	             System.exit(1);
	         }
	        return null;
	     }
	}
   
   
   private class mTransmit extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	     
	    	 int PubCount = 0;
	    	 
	    	 
	    	 Xmit = true;
	    	 final MqttTopic SensorTopic = client.getTopic(TOPIC);
	    	 Log.d(LogMsgID, "Transmit- 1");
	         
	         while (Xmit) {
	         String Payload = ClientID+":"+String.valueOf(mSensorData); 
	         MqttMessage message = new MqttMessage(Payload.getBytes());	
	        	 
	         try {
	        	 SensorTopic.publish(message);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(LogMsgID, "Publish Failed # 1");
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				Log.d(LogMsgID, "Publish Failed # 2");
				e.printStackTrace();
			}

	         System.out.println("Published [" + SensorTopic.getName() + "]: " + mSensorData);
	 		 PubCount++;
	 		 if (PubCount > PubLimit) Xmit = false;
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
	    Log.d(LogMsgID, String.valueOf(lux));
	  }
	
	
}