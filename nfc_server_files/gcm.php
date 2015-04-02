<?php

if(!empty($_GET["shareRegId"])) {
    $gcmRegID  = $_POST["regId"]; 
    file_put_contents("GCMRegId.txt",$gcmRegID);
    echo "Done!";
    exit;
}	
?>