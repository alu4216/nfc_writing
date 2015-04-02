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
        width: 55%;
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
        Android SQLite and MySQL Sync - Add Data
    </div>
</center>
<form method="POST">
    <table>
        <tr>
            <td>Relation:</td><td><input name="relation" /></td>
            <td>ParentObject:</td><td><input name="parentObject" /></td>
            <td>Object:</td><td><input name="object" /></td> 
            <td>Interaction:</td><td><input name="interaction" /></td> 
        </tr>
    </table>
    <div id="boton" colspan="2" align="center"><input type="submit" value="Add User"> </div>
</form>
<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
if(isset($_POST["relation"]) && !empty($_POST["relation"])&& isset($_POST["parentObject"]) && !empty($_POST["parentObject"])&& isset($_POST["object"]) && !empty($_POST["object"])&& isset($_POST["interaction"]) && !empty($_POST["interaction"])){
    $db = new DB_Functions(); 
    //Store User into MySQL DB
    $relation = $_POST["relation"];
    $pObject = $_POST["parentObject"];
    $object = $_POST["object"];
    $interaction = $_POST["interaction"];
    $fecha = new DateTime();
    $time=$fecha->getTimestamp();
    $timestamp=idate('U', $time);
    $res = $db->storeUser($relation,$pObject,$object,$interaction,$timestamp,"0");
    if($res){ 
        //Post message to GCM when submitted
        $pushStatus = "New Data";	
        $pushMessage = "New Data";
        $gcmRegID  = file_get_contents("GCMRegId.txt");
        if (isset($gcmRegID) && isset( $pushMessage)) {		
            $gcmRegIds = array($gcmRegID);
            $message = array("m" => $pushMessage);	
            $pushStatus = $db->sendMessageThroughGCM($gcmRegIds, $message);	
        }
?>
<div id="msg">Insertion successful.GCM status:<?php echo $pushStatus; ?></div>

<?php }else{ ?>
<div id="msg">Insertion failed</div>
<?php }
} else{ ?>
<div id="msg">Please enter name and submit</div>
<?php }
?>