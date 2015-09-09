
# -------------------------------------------------------
p1="http://prithwis.x10.bz/IOT/ISD_pushData.php?sensorID="
sensorID="1003B"
p2="&Value="
Value=$(shuf -i 1-80 -n 1)   # random number being generated for Value
URL=$p1$sensorID$p2$Value
echo $URL
# -------------------------------------------------------
curl $URL
