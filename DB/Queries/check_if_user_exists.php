<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("SELECT user_name FROM all_users WHERE mac_address = '" . $_GET['mac_address'] . "' LIMIT 1");

$output = array();
$exists = 0;

while($e=mysql_fetch_assoc($q))
{
        $output[]=$e;
        $exists = 1;
}
 

print($exists);
//print(json_encode($output));

mysql_close();
?>