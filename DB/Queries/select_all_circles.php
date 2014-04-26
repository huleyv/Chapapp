<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("SELECT * FROM circles WHERE created_by_mac_address = '".$_GET['mac_address']."'");

$output = array();

while($e=mysql_fetch_assoc($q))
{
        $output[]=$e;
}

print(json_encode($output));

 

mysql_close();

?>