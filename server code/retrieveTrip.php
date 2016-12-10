<?php

	$username = "root";
	$password = "seng521";
	$host = "localhost";
	$dbname = "fleet";
	$response = array();
	if (isset($_GET['id']) and !empty($_GET['id'])){
		$tripid = $_GET['id'];
	}
	try{
		$conn = new PDO("mysql:host={$host};dbname={$dbname}", $username, $password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		$stmt = $conn->prepare("SELECT tripID, timeStarted FROM obd WHERE vehicleID={$vehicleid}");
		$stmt->execute();
	
		$result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
		$result = $stmt->fetchAll();
		if (empty($result)){
			print json_encode("No entry for id");
		}
		else{
			print json_encode($result);	
		}
	}
	catch (PDOException $e){
		echo "Connection failed" . $e->getMessage();
	}
	$conn = null;