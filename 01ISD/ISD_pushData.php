<?php
    // Connect to MySQL
    include("dbConnect.php");

    $con = mysql_connect($MyHostname, $MyUsername, $MyPassword) or die(mysql_error());
    if (!$con)
     {
       die("Could not connect: 1" . mysql_error());
     }
     mysql_select_db($MyDBname) or die(mysql_error());

    // Prepare the SQL statement
    $SQL = "INSERT INTO IOT_SensorData (sensorID ,Value) VALUES ('".$_GET["sensorID"]."', '".$_GET["Value"]."')";  
    
    // Execute SQL statement
    mysql_query($SQL,$con);
    
    // Send Mail
    $mailMSG = $SQL;
    $mailDEST = "prithwis@yantrajaal.com";
    if ($_GET["Value"] > 50){
      $mailSUB = "Warning : Value HIGH";
      } else {
      $mailSUB = "Value Normal";
      }
    mail($mailDEST,$mailSUB,$mailMSG);

?>