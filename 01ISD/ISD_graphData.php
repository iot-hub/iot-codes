<?php // content="text/plain; charset=utf-8"

require_once ('jpgraph/jpgraph.php');
require_once ('jpgraph/jpgraph_line.php');

    // Start MySQL Connection
    include('dbConnect.php'); 
    $con = mysql_connect($MyHostname, $MyUsername, $MyPassword) or die(mysql_error());
    if (!$con)
     {
       die("Could not connect: 1" . mysql_error());
     }
    mysql_select_db($MyDBname) or die(mysql_error());


// --------------------------------------------------------------------------------------
    

    // Retrieve all records and display them
    $result = mysql_query("SELECT * FROM IOT_SensorData ORDER BY id ASC");

    $datValue = array();
    $datID = array();
    // process every record
    while( $row = mysql_fetch_array($result) )
    {
       //echo $row["temp"];
       array_push($datID,$row["id"]);
       array_push($datValue,$row["Value"]);
       

    }
    
// ----------------------------------------------------------------------------------------



// Setup the graph
$graph = new Graph(600,500);
$graph->SetScale("textlin");

$theme_class=new UniversalTheme;

$graph->SetTheme($theme_class);
$graph->img->SetAntiAliasing(false);
$graph->title->Set('Filled Y-grid');
$graph->SetBox(false);

$graph->img->SetAntiAliasing();

$graph->yaxis->HideZeroLabel();
$graph->yaxis->HideLine(false);
$graph->yaxis->HideTicks(false,false);

$graph->xgrid->Show();
$graph->xgrid->SetLineStyle("solid");
$graph->xaxis->SetTickLabels($datID);
$graph->xgrid->SetColor('#E3E3E3');

// Create the first line
$p1 = new LinePlot($datValue);
$graph->Add($p1);
$p1->SetColor("#6495ED");
$p1->SetLegend('Line 1');


// Create the third line
//$p3 = new LinePlot($datay3);
//$p3 = new LinePlot($datID);
//$graph->Add($p3);
//$p3->SetColor("#FF1493");
//$p3->SetLegend('Line 3');

$graph->legend->SetFrameWeight(1);

// Output line
$graph->Stroke();

?>