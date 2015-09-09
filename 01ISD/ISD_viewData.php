<?php 
    // Start MySQL Connection
    include('dbConnect.php'); 
    $con = mysql_connect($MyHostname, $MyUsername, $MyPassword) or die(mysql_error());
    if (!$con)
     {
       die("Could not connect: 1" . mysql_error());
     }
    mysql_select_db($MyDBname) or die(mysql_error());

?>

<html>
<head>
    <title>Sensor Data</title>
    <style type="text/css">
        .table_titles, .table_cells_odd, .table_cells_even {
                padding-right: 20px;
                padding-left: 20px;
                color: #000;
        }
        .table_titles {
            color: #FFF;
            background-color: #666;
        }
        .table_cells_odd {
            background-color: #CCC;
        }
        .table_cells_even {
            background-color: #FAFAFA;
        }
        table {
            border: 2px solid #333;
        }
        body { font-family: "Trebuchet MS", Arial; }
    </style>
</head>

    <body>
        <h1>Sensor Data</h1>
    <table border="0" cellspacing="0" cellpadding="4">
      <tr>
            <td class="table_titles">ID</td>
            <td class="table_titles">Date and Time</td>
            <td class="table_titles">SensorID</td>
            <td class="table_titles">Value</td>
          </tr>
<?php

    

    // Retrieve all records and display them
    $result = mysql_query("SELECT * FROM IOT_SensorData ORDER BY id ASC");

    // Used for row color toggle
    $oddrow = true;

    // process every record
    while( $row = mysql_fetch_array($result) )
    {
        if ($oddrow) 
        { 
            $css_class=' class="table_cells_odd"'; 
        }
        else
        { 
            $css_class=' class="table_cells_even"'; 
        }

        $oddrow = !$oddrow;

        echo '<tr>';
        echo '   <td'.$css_class.'>'.$row["id"].'</td>';
        echo '   <td'.$css_class.'>'.$row["event"].'</td>';
        echo '   <td'.$css_class.'>'.$row["sensorID"].'</td>';
        echo '   <td'.$css_class.'>'.$row["Value"].'</td>';
        echo '</tr>';
    }
?>
    </table>
    </body>
</html>