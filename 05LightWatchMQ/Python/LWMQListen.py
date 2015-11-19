#!/usr/bin/python3
# https://pypi.python.org/pypi/paho-mqtt

import paho.mqtt.client as mqtt
import sqlite3

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("iothub")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    
    recdData = str(msg.payload)[2:][:-1]  # -- strip of the first two and the last char of payload
    print(recdData)
    SplitData = recdData.split(":");
    conn.execute("Insert into IOTdata (clientID, clientData) VALUES ('"+SplitData[0]+"', "+SplitData[1]+")");
    conn.commit();
    

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("broker.hivemq.com", 1883, 60)
conn = sqlite3.connect('LWMQ.db');

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()
