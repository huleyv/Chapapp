<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);


$q=mysql_query("UPDATE master_circles SET is_enabled=" .$_GET['is_enabled'] .
					" WHERE mac_address='" .$_GET['mac_address']."'" );



$result = "Failure";

if ($q)
{
	$result = "Success";
}
					
print($result);
						

mysql_close();
?>