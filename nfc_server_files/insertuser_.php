<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
$db = new DB_Functions(); 
//Get JSON posted by Android Application
header('Content-Type: application/json');

$json = (isset($_POST["usersJSON"])? $_POST["usersJSON"] : ''); 
if($json!='')
{
    //Remove Slashes
    if (get_magic_quotes_gpc()){
        $json = stripslashes($json);
    }
    //Decode JSON into an Array
    $data = json_decode($json);
    //Util arrays to create response JSON
    $a=array();
    $b=array();
    //Loop through an Array and insert data read from JSON into MySQL DB

    for($i=0; $i<count($data) ; $i++)
    {
        //Store User into MySQL DB
        $res = $db->storeUser($data[$i]->relacion,$data[$i]->objetoPadre,$data[$i]->objeto,$data[$i]->interaccion,$data[$i]->tiempo,$data[$i]->sincro);        
        //Based on inserttion, create JSON response
        if($res){
            $b["relacion"] = $data[$i]->relacion;
            $b["objetoPadre"] = $data[$i]->objetoPadre;
            $b["objeto"] = $data[$i]->objeto;
            $b["interaccion"] = $data[$i]->interaccion;
            $b["tiempo"] = $data[$i]->tiempo;
            $b["sincro"] = "1";
            array_push($a,$b);
        }
    }
    //Post JSON response back to Android Application

    echo json_encode($a);    
}
else
{
    echo ("nada");
}

?>