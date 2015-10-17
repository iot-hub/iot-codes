package com.yantrajaal.chatiot;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
// Smack libraries 
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

import java.util.Random; // required for Random Number Generation

public class MainActivity extends Activity {
	String msg = "PM1139 : ";
	
	XMPPTCPConnectionConfiguration configChatIOT = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword("pm2", "passw0rd")
            .setServiceName("adastra.re")
            .setHost("adastra.re")
            .setPort(5222)
            .setResource("chatIOT")
            .build();
    
    AbstractXMPPConnection conxChatIOT = new XMPPTCPConnection(configChatIOT);
	/** Called when the activity is first created. **/
	
	@Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);
	      SmackConfiguration.setDefaultPacketReplyTimeout(10000);
	      Log.d(msg, "The onCreate() event");
	   }

	private class pmConnect extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	    	 
	    	 try {
	    		 conxChatIOT.connect();
	    		 conxChatIOT.login();
	 		} catch (SmackException | IOException | XMPPException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}	
	        return null;
	     }
/*
// this piece of code is kept just for the sake of completeness of Async Tasks
	     protected void onProgressUpdate(Void...progress) {
	        // setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Void...result) {
	         //showDialog("Downloaded  bytes");
	     }
*/
	 }
	private class pmSend extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void...dummy) {
	    	 Random randomGenerator = new Random();
	    	 int pseudoSensorData;
	    	 	
	 	// Assume we've created an XMPPConnection name "conxChatIOT"._
	 		ChatManager chatmanager = ChatManager.getInstanceFor(conxChatIOT);
	 		
	 		Chat ChatIOT = chatmanager.createChat("pm3@adastra.re", new ChatMessageListener() {
	 			public void processMessage(Chat chat, Message message) {
	 				System.out.println("Received message: " + message);
	 			}
	 		});
	 		
	 		for(int i=0 ; i < 4 ; i++)
	 		{
	 		    
	 			try {
	 				if (i==0){
	 					ChatIOT.sendMessage("Sound 2");
	 				} else {
	 				// this data needs to be generated from some Android physical sensor
	 	 			// instead of using a random number generator
	 	 			pseudoSensorData = randomGenerator.nextInt(100);
	 	 			String payLoad = "Push " + String.valueOf(pseudoSensorData);
	 				ChatIOT.sendMessage(payLoad);
	 				}
				} catch (NotConnectedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
	
	   //Method to start the Connection
	   public void startCon(View view) {
		   Toast.makeText(this, "Connecting", Toast.LENGTH_LONG).show();
		   new pmConnect ().execute();
	   }
	   
	 //Method to start the Connection
	   public void pushData(View view) {
		   Toast.makeText(this, "Push Data", Toast.LENGTH_LONG).show();
		   new pmSend ().execute();
	   }
	   
	 //Method to Quit 
	   public void stopCon(View View) {
		   Toast.makeText(this, "Disconnection", Toast.LENGTH_LONG).show();
		   conxChatIOT.disconnect(); 
	   }
	 
}