<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);


$q=mysql_query("UPDATE circles SET ".
 														" center_marker_latitude=" . $_GET['center_latitude'] .
														", center_marker_longitude=" . $_GET['center_longitude'] .
														", radius=" . $_GET['radius'] .
														", radius_marker_latitude=" .$_GET['radius_latitude'].
														", radius_marker_longitude=" . $_GET['radius_longitude'] .
														", name='" . $_GET['name'].
														"',description='". $_GET['description']. "'"	.
																											
					" WHERE created_by_mac_address='" .$_GET['mac_address']. "' AND circle_id=". $_GET['circle_id'] );



$result = "Failure";

if ($q)
{
	$result = "Success";
}
					
print($result);
						

mysql_close();
?>