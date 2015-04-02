<style>
    body {
        font: normal medium/1.4 sans-serif;
    }
    div.header{
        padding: 10px;
        background: #e0ffc1;
        width:30%;
        color: #008000;
        margin:5px;
    }
    table {
        border-collapse: collapse;
        width: 25%;
        margin-left: auto;
        margin-right: auto;
    }
    form{
        width: 30%;
        margin-left: auto;
        margin-right: auto;
        padding: 10px;
        border: 2px solid #edd3ff;
    }
    div#msg{
        margin-top:10px;
        width: 30%;
        margin-left: auto;
        margin-right: auto;
        text-align: center;
    }
    div#boton{
        margin-top:10px;
        width: 30%;
        margin-left: auto;
        margin-right: auto;
        text-align: center;
    }
</style>
<center>
    <div class="header">
        Android SQLite and MySQL Sync - Delete data
    </div>
</center>
<form method="POST">
    <table>
        <tr>
            <td>Time</td><td><input name="tiempo" /></td>
        </tr>
    </table>
    <div id="boton" colspan="2" align="center"><input type="submit" value="Delete User"> </div>
</form>
<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
if(isset($_POST["tiempo"]) && !empty($_POST["tiempo"])){
    $db = new DB_Functions(); 
    $tiempo = $_POST["tiempo"];
    $res = $db->delete($tiempo);
    //Based on inserttion, create JSON response
    if($res){ 
	
        $pushMessage = "Deleted Data";
        $gcmRegID  = file_get_contents("GCMRegId.txt");
        if (isset($gcmRegID) && isset( $pushMessage)) {		
            $gcmRegIds = array($gcmRegID);
            $message = array("m" => $pushMessage);	
            $pushStatus = $db->sendMessageThroughGCM($gcmRegIds, $message);	
        }
?>
<div id="msg">Deleted successfully.GCM status:<?php echo $pushStatus; ?></div>
<?php }else{ ?>
<div id="msg">Deleted failed</div>
<?php }
}  else{ ?>
<div id="msg">Please enter data and submit</div>
<?php }
?>




















