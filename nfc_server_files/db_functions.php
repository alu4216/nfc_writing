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
    public function storeUser($nombre,$tipo) {
        // Insert user into database
        $result = mysql_query("INSERT INTO workflow(nombre,tipo) VALUES('$nombre','$tipo')");

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
        $result = mysql_query("select * FROM workflow");
        return $result;
    }
    /**
     * Get Yet to Sync row Count
     */
    public function getUnSyncRowCount() {
        $result = mysql_query("SELECT * FROM workflow WHERE sincro = FALSE");
        return $result;
    }

    public function updateSyncSts($nombre, $sts){
        $result = mysql_query("UPDATE workflow SET sincro = $sts WHERE nombre = '$nombre'");
        return $result;
    }
}

?>