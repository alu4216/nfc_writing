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
        width: 60%;
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
</style>
<center>
    <div class="header">
        Android SQLite and MySQL Sync - Add Users
    </div>
</center>
<form method="POST">
    <table>
        <tr>
            <td>Relationships</td><td><input name="relacion" /></td>
            <td>ParentObject:</td><td><input name="objetoPadre" /></td>  
            <td>ChildObject:</td><td><input name="objeto" /></td> 
            <td>Interaction:</td><td><input name="interaccion" /></td>  
        </tr>
        
    </table>
</form>
<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
if(isset($_POST["relacion"]) && !empty($_POST["relacion"])&& isset($_POST["objetoPadre"]) && !empty($_POST["objetoPadre"])
                && isset($_POST["objeto"]) && !empty($_POST["objeto"])&& isset($_POST["interaccion"]) && !empty($_POST["interaccion"])){
    $db = new DB_Functions(); 
    //Store User into MySQL DB
    $relacion = $_POST["relacion"];
    $objetoPadre = $_POST["objetoPadre"];
    $objeto = $_POST["objeto"];
    $interaccion = $_POST["interaccion"];
    $tiempo = "1111";
    $sincro ="0";
    $res = $db->storeUser($relacion,$objetoPadre,$objeto,$interaccion,$tiempo,$sincro);
    //Based on inserttion, create JSON response
    if($res){ ?>
<div id="msg">Insertion successful</div>
<?php }else{ ?>
<div id="msg">Insertion failed</div>
<?php }
} else{ ?>
<div id="msg">Please enter data and submit</div>
<div id="msg"><tr><td colspan="2" align="center"><input type="submit" value="Add Data"/></td></tr></div>
<?php }
?>