<?php



$databasehost = "sql313.byethost7.com";

$databasename = "b7_14574574_cps630webapp";

$databaseusername ="b7_14574574";

$databasepassword = "*****";



mysql_connect($databasehost,$databaseusername,$databasepassword);



mysql_select_db($databasename);



 



$q=mysql_query("INSERT INTO all_users (mac_address,gps_latitude,gps_longitude,user_name,battery_charge,id)

					VALUES ('".$_GET['mac_address']."', 1.1, 2.2,'".$_GET['name']."', 32, NULL)");



$result = "Failure";



if ($q)

{

	$result = "Success";

}



print ($q);					

print($result);

						



mysql_close();

?>