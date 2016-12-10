<?php

	$username = "root";
	$password = "seng521";
	$host = "localhost";
	$dbname = "fleet";
	if (isset($_POST['vid']) && isset($_POST['time'])){
		$vehicleid = $_POST['vid'];
		$timestarted = $_POST['time'];
		try{
			$conn = new PDO("mysql:host={$host};dbname={$dbname}", $username, $password);
			$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "INSERT INTO trip_manager (VehicleID, timeStarted) VALUES ({$vehicleid}, {$timestarted})";
			$conn->exec($sql);
			
			$stmt = $conn->prepare("SELECT MAX(tripID) FROM trip_manager WHERE VehicleID={$vehicleid}");
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
	}
	else{
		print json_encode("Variables not set");
	}
	$conn = null;