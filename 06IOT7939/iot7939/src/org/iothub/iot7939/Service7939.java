package org.iothub.iot7939;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

import android.widget.Toast;

public class Service7939 extends Service implements SensorEventListener  {
	
	int PubLimit = 2;                   // number of times message will be published
	String LogMsgID = "iothub: ";
	String StatusMessage = "NIL";
	
	private SensorManager mSensorManager;
	private Sensor mLight;
	float mSensorData;
	
	String BROKER_URL = "tcp://broker.hivemq.com:1883";
	// websockets client at http://www.hivemq.com/demos/websocket-client/
	//String BROKER_URL = "tcp://test.mosquitto.org:1883";
	// websockets client at http://test.mosquitto.org/ws.html
	String TOPICpub = "iot79";   // Publish Topic
	String TOPICsub = "iot39";   // Subscribe Topic
	
	
	String ClientID ;
	
	private MqttClient client;
	
	@Override
	   public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(LogMsgID, "OnStart");
		
	   mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	   mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	   mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
			
	   ClientID = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 	// From Android
	   // ClientID = MqttClient.generateClientId();                             // From MQTT/Paho
	   
	   try {
           client = new MqttClient(BROKER_URL, ClientID, new MemoryPersistence());
       } catch (MqttException e) {
           e.printStackTrace();
           System.exit(1);
       }
	   
	   new mConSubscribe ().execute();
	   new mPublish ().execute();
	   
	   Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
	   return START_STICKY;
	}

	private class mConSubscribe extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	    	 try {
	             client.connect();
	    		 client.setCallback(new mCallback(Service7939.this));
	             client.subscribe(TOPICsub);
	            
	         } catch (MqttException e) {
	             e.printStackTrace();
	             Log.d(LogMsgID, "Connect Failed");
	             System.exit(1);
	         }
	        return null;
	     }
	}
	
	private class mPublish extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	     
	    	 final MqttTopic SensorTopic = client.getTopic(TOPICpub);
	    	 
	    	 for (int PubCount =0;PubCount < PubLimit; PubCount++){
	    		 
	    		 String Payload = "id:"+ClientID+":data:"+String.valueOf(mSensorData)+":cmd:"+StatusMessage; 
	    		 Log.d(LogMsgID, Payload);
		         MqttMessage message = new MqttMessage(Payload.getBytes());	
		         try {
		        	 SensorTopic.publish(message);
				} catch (MqttPersistenceException e) {
					e.printStackTrace();
					Log.d(LogMsgID, "Publish Failed # 1");
				} catch (MqttException e) {
					Log.d(LogMsgID, "Publish Failed # 2");
					e.printStackTrace();
				}
		         //System.out.println("Published [" + SensorTopic.getName() + "]: " + mSensorData + ":"+ PubCount);
		         justWait(5000);
	    	 
	    	 }
	        return null;
	     }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// The light sensor returns a single value.
	    // Many sensors return 3 values, one for each axis.
	    float lux = event.values[0];
	    // Do something with this sensor value.
	    mSensorData = lux;
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
public class mCallback implements MqttCallback {
		
		private ContextWrapper context;

	    public mCallback(ContextWrapper context) {
	        this.context = context;
	        Log.d(LogMsgID, "in mCallback");
	    }
	   
		@Override
	    public void connectionLost(Throwable cause) {
	        //We should reconnect here
	    }

		@Override
		public void deliveryComplete(IMqttDeliveryToken arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void messageArrived(String Topic, MqttMessage message) throws Exception {
			
	        StatusMessage = new String(message.getPayload());
	        processMessage(StatusMessage);
	        new mPublish ().execute();
		}
		
   }

public void processMessage(String arg){
	
	MediaPlayer mp;
	
	if (arg.equals("siren")) {
		mp = MediaPlayer.create(getApplicationContext(), R.raw.warningsiren);
		mp.start(); justWait(10000); mp.stop();
	} else {
		mp = MediaPlayer.create(getApplicationContext(), R.raw.indianbell);
		mp.start(); justWait(10000); mp.stop();
	}
	
	mp.release();
    mp = null;
	/**
	This is where we call external application that will flash a light
	 */
	
	String ExternalAppPackage = "org.iothub.flashlight";
	String ExternalAppActivity = "org.iothub.flashlight.MainActivity";
	
	Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(ExternalAppPackage,ExternalAppActivity));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
	}

public void justWait(int delay) {
	try {
	    Thread.sleep(delay);                 //1000 milliseconds is one second.
	 } catch(InterruptedException ex) {
	    Thread.currentThread().interrupt();
	 }
	
}

}
