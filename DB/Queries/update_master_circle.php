<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);


$q=mysql_query("UPDATE master_circles SET ".
 														"master_latitude=" . $_GET['center_latitude'] .
														",master_longitude=" . $_GET['center_longitude'] .
														",master_radius=" . $_GET['radius'] .
														",radius_latitude=" .$_GET['radius_latitude'].
														",radius_longitude=" . $_GET['radius_longitude'] .
														",name='" . $_GET['name'].
														"',description='". $_GET['description'].
														"',is_enabled=" .$_GET['is_enabled'] .
					" WHERE mac_address='" .$_GET['mac_address']."'" );



$result = "Failure";

if ($q)
{
	$result = "Success";
}
					
print($result);
						

mysql_close();
?>