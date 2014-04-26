<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("INSERT INTO circles (created_by_mac_address, circle_id, center_marker_latitude, center_marker_longitude, radius, radius_marker_latitude, radius_marker_longitude, name, description)
					VALUES ('".$_GET['mac_address']."',".$_GET['circle_id'].",".$_GET['center_latitude'].",".$_GET['center_longitude'].",".$_GET['radius'].",".$_GET['radius_latitude'].",".$_GET['radius_longitude'].",'".$_GET['name']."','".$_GET['description']."')");



$result = "Failure";

if ($q)
{
	$result = "Success";
}
					
print($result);
						

mysql_close();
?>