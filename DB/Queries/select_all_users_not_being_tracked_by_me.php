<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("SELECT * FROM all_users WHERE mac_address NOT IN 
					(SELECT mac_address_tracked FROM users_tracked WHERE mac_address_tracked_by LIKE '" . $_GET["mac_address_tracked_by"] . "')
					AND mac_address NOT LIKE '". $_GET["mac_address_tracked_by"] . "'");


$output = array();

while($e=mysql_fetch_assoc($q))
{
        $output[]=$e;
}


print(json_encode($output));

mysql_close();

?>