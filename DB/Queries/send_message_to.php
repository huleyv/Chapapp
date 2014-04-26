<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("INSERT INTO messages (message_id, mac_address, message, is_new_message) VALUES (NULL, '" . $_GET["mac_address"] . "','" . $_GET["message"] . "', " . $_GET["is_new_message"]. ")" );


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