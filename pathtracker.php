<?php

mysql_connect("localhost","xxxxx","xxxx");
mysql_select_db("location");

//mysql_connect("xxxx.webhost.com","xxxxx","xxxx");
//mysql_select_db("xxxxxxxxxx");

if ($_SERVER['REQUEST_METHOD'] == 'POST') { 

	//$sql_conn = mysql_connect("localhost","xxxx","xxx") or die("Connection failed : " . mysql_error());
	//mysql_connect("localhost","xxxx","xxx") or die(mysql_error());
	//mysql_select_db("location") or die(mysql_error());
	
	
	//$q=mysql_query("SELECT * FROM pathtracks WHERE birthyear>'".$_REQUEST['year']."'");
	/*
	//$query = "SELECT * FROM pathtrack";
	$result = "";
	foreach($_post[latitude] as $key) {
		//$query = "INSERT INTO pathtrack (user_name, provider, latitude, longitude, accuracy, time) VALUES ('$_POST[user_name]', '$_POST[provider]', $_POST[latitude], $_POST[longitude], $_POST[accuracy], $_POST[time])";
		//$q=mysql_query($query);
	//	if (mysql_errno())
	//		break;
	$result += $key . " / ";
	}
	*/
	//$input = file_get_contents('php://input');
	
	//$input = substr($input, 5);  //remove json= 
	//$input = urldecode($input); 
	//$jsonObj = json_decode($input, true); 
	
	//$input = 7;
	//if( !empty($jsonObj)) {  
	//    try { 
	//	$input = "5";
	
	        //$input = $_POST['latitude'][1]; 
	        //$input = $input . $jsonObj[latitude]; 
	//    } 
	//} 
	/*
	$input = "";
	foreach($_POST as $key => $value) {
		//$input .= $key . " | ";
		//$input .= $value[0] . " * " . $value[1];
		//break;
		if(is_array($value)) {
			foreach($value as $val)
			//$$key = $value;
				$input .= $val . " / ";
		//	}
		} else {
			$input .= $key . " | ";
		}
	}
	*/
	
	$input = "";
	$query = "";
	$k = "";
	$keys = array_keys($_POST);
	for ($i = 0; $i < count($_POST[$keys[0]]); $i++) {
		//foreach($_POST as $key => $value)
		$v = "";
		for ($j = 0; $j < count($keys); $j++) 
		{ 
			if ($i == 0) {
				if ($j != 0)
					$k .= ",";
				$k .= $keys[$j];
			}
	
			if ($j != 0)
				$v .= ",";
	
			if ($j < 2)
				$v .= "'";
			$v .= $_POST[$keys[$j]][$i];
			if ($j < 2)
				$v .= "'";
			
			//$input .= count($value) . " / ";
		}
		if ($i != 0)
			$query .= ",";
		$query .= "(" . $v . ")";
		//break;
	}
	
	$query = "INSERT INTO pathtrack(" . $k . ")VALUES" . $query;
	
	mysql_query($query);

	//$input = "";
	//$keys = array_keys($_POST);
	//$input = $keys[0]; // user_name
	//$input = count($_POST[$keys[0]]); // 123
	
	//$input = $_POST[user_name][0]; // roman
	//$input = count($_POST['user_name']); // 119
	//$input = count(array_keys($_POST)); // 6
	//$input = count($_POST); // 6
	
	//print($query);


	if (mysql_errno()) { 
	    //header("HTTP/1.1 500 Internal Server Error");
	    //echo nl2br($query."\n");
	    //echo mysql_error(); 
	    //$result = array('result' => mysql_error());
	    //print(json_encode($result));
		print(json_encode(array('result' => mysql_error())));
		//print("[{'result':\"" . mysql_error() . "\"}]");
	}
	else
	{
		print(json_encode(array('result' => 'success')));
		//print("[{'result':'success'}]");
	
	}
} else if ($_SERVER['REQUEST_METHOD'] == 'GET') { 

	header('Content-type: application/json; charset=utf-8');
	header('Cache-Control: no-cache, must-revalidate');
	header('Expires: Mon, 1 Jan 1990 00:00:00 GMT');

	//print("<span>kuku</span>");

	$query = "SELECT * FROM pathtrack";
	$q = mysql_query($query);
	
	if (mysql_errno()) { 
	    //header("HTTP/1.1 500 Internal Server Error");
	    //echo nl2br($query."\n");
	    //echo mysql_error(); 
	    //$result = array('result' => mysql_error());
	    //print(json_encode($result));
		print(json_encode(array('result' => mysql_error())));
		//print('{"result":"' . mysql_error() . '"}');
	}
	else
	{
		$result = "";
	    while($e=mysql_fetch_assoc($q))
	        $result[]=$e;

		$json = json_encode($result);
		print(isset($_GET['callback']) ? "{$_GET['callback']}($json)" : $json);
	   	//print(json_encode($result));
	
		//print(json_encode(array('result' => 'success')));
		//print('{"result":"success"}');
	
	}
}

mysql_close();

?>
