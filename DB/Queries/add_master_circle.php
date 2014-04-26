<?php



$databasehost = "sql313.byethost7.com";

$databasename = "b7_14574574_cps630webapp";

$databaseusername ="b7_14574574";

$databasepassword = "*****";



mysql_connect($databasehost,$databaseusername,$databasepassword);



mysql_select_db($databasename);



 





$q=mysql_query("INSERT INTO master_circles (mac_address,master_latitude, master_longitude, master_radius, radius_latitude, radius_longitude, name, description,is_enabled)

VALUES ('".$_GET['mac_address']."',".$_GET['center_latitude'].",".$_GET['center_longitude'].",".$_GET['radius'].",".$_GET['radius_latitude'].",".$_GET['radius_longitude'].",'".$_GET['name']."','".$_GET['description']."',".$_GET['is_enabled'].")");







$result = "Failure";



if ($q)

{

	$result = "Success";

}

					

print($result);

						



mysql_close();

?>