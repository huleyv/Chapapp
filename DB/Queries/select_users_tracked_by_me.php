<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("SELECT all_users.* FROM all_users LEFT JOIN users_tracked ON 
					all_users.mac_address = users_tracked.mac_address_tracked 
					WHERE users_tracked.mac_address_tracked_by LIKE '" . $_GET["mac_address_tracked_by"]. "'");


$output = array();

while($e=mysql_fetch_assoc($q))
{
        $output[]=$e;
}
 
print(json_encode($output));

 

mysql_close();

?>

