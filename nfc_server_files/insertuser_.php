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
        $res = $db->storeUser($data[$i]->nombre,$data[$i]->tipo,$data[$i]->sincro);        
        //Based on inserttion, create JSON response
        if($res){
            $b["nombre"] = $data[$i]->nombre;
            $b["tipo"] = $data[$i]->tipo;
            $b["sincro"] = $data[$i]->sincro;
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