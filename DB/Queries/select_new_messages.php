<?php

$databasehost = "sql313.byethost7.com";
$databasename = "b7_14574574_cps630webapp";
$databaseusername ="b7_14574574";
$databasepassword = "*****";

mysql_connect($databasehost,$databaseusername,$databasepassword);

mysql_select_db($databasename);

 

$q=mysql_query("SELECT message_id, message FROM messages WHERE mac_address = '" .$_GET['mac_address'] . "' AND is_new_message=1" );

$output = array();

while($e=mysql_fetch_assoc($q))
{
        $output[]=$e;
}

print(json_encode($output));

 

mysql_close();

?>