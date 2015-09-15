#!/usr/bin/python
# command line jabber client http://stackoverflow.com/questions/170503/commandline-jabber-client
import sys
import xmpp

login = 'xxxx' # @server
pwd   = 'xxxx'
server = 'adastra.re'
dest = 'xxxx@adastra.re'
msg = ''
# -------- assemble the message
first = True
for arg in sys.argv:
 if (first) :  first = False
 else : msg = msg + arg + ' '
print msg
# -------- connect to server
cnx = xmpp.Client(server,debug=[])
cnx.connect()
cnx.auth(login,pwd)
# -------- transmit
cnx.send( xmpp.Message( dest , msg ) )
