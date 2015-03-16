<?php

class DB_Functions {

    private $db;

    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {

    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($relacion,$objetoPadre,$objeto,$interaccion,$tiempo,$sincro) {
        // Insert user into database
        $result = mysql_query("INSERT INTO log(relacion,objetoPadre,objeto,interaccion,tiempo,sincro) VALUES('$relacion','$objetoPadre','$objeto','$interaccion','$tiempo','$sincro')");

        if ($result) {
            return true;
        } else {			
            // For other errors
            return false;
        }
    }
    /**
     * Getting all users
     */
    public function getAllUsers() {
        $result = mysql_query("select * FROM log");
        return $result;
    }
    /**
     * Get Yet to Sync row Count
     */
    public function getUnSyncRowCount() {
        $result = mysql_query("SELECT * FROM log WHERE sincro = FALSE");
        return $result;
    }

    public function updateSyncSts($relacion,$objetoPadre,$objeto,$interaccion,$tiempo,$sincro){
       
        $result = mysql_query("UPDATE log SET sincro = '$sincro'  WHERE relacion = '$relacion'AND objetoPadre= '$objetoPadre'AND objeto='$objeto'AND interaccion='$interaccion'
                              AND tiempo='$tiempo'");
        return $result;
    }
}

?>