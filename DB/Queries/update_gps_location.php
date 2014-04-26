<?php



$databasehost = "sql313.byethost7.com";

$databasename = "b7_14574574_cps630webapp";

$databaseusername ="b7_14574574";

$databasepassword = "*****";



mysql_connect($databasehost,$databaseusername,$databasepassword);



mysql_select_db($databasename);



 



$q=mysql_query("UPDATE all_users SET gps_latitude =" . $_GET['gps_latitude'] . ", gps_longitude=". $_GET['gps_longitude'] .

", battery_charge=". $_GET['battery']." WHERE mac_address = '" . $_GET['mac_address']. "'");





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