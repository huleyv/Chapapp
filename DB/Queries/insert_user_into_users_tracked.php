<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("INSERT INTO users_tracked (mac_address_tracked_by, mac_address_tracked) VALUES ('" . $_GET["mac_address_tracked_by"] . "','" . $_GET["mac_address_tracked"] . "')" );

if ($q)
{
	print("Success");
}
else
{
	print("Failure");
}

 
mysql_close();
?>