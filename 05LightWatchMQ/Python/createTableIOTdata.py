#!/usr/bin/python3

import sqlite3

conn = sqlite3.connect('LWMQ.db');
print ('database opened');
conn.execute("drop table IOTdata");
conn.execute("Create table IOTdata(clientID varchar(50), clientData real)");
print ('table created');
conn.execute("Insert into IOTdata (clientID, clientData) VALUES ('testclient', 99.99)");
conn.commit();
conn.close();
