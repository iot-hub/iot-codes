#!/bin/bash
sqlite3 IOTdb 'drop table IOTdata;'
sqlite3 IOTdb 'create table IOTdata ( event datetime, user varchar(30), device varchar(50), datavalue numeric(5,2));'
sqlite3 IOTdb 'insert into IOTdata values("2015-09-15:10:10:10","testuser@nowhere.com","testdevice1234",100.4);'
sqlite3 IOTdb 'select * from IOTdata;'
