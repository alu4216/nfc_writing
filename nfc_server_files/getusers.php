<?php
    include_once 'db_functions.php';
    $db = new DB_Functions();
    $users = $db->getUnSyncRowCount();
	$a = array();
	$b = array();
    if ($users != false){
        $no_of_users = mysql_num_rows($users);
		while ($row = mysql_fetch_array($users)) {		
			$b["relacion"] = $row["relacion"];
			$b["objetoPadre"] = $row["objetoPadre"];
            $b["objeto"] = $row["objeto"];
			$b["interaccion"] = $row["interaccion"];
			$b["tiempo"] = $row["tiempo"];
            $b["sincro"] = "1";
            array_push($a,$b);
		}
		echo json_encode($a);
	}
    /*else{
        $no_of_users = 0;
		$b["count"] = $no_of_users;
		echo json_encode($b);
	}*/
?>