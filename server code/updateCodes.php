<?php

	$username = "root";
	$password = "seng521";
	$host = "localhost";
	$dbname = "fleet";
	$response = array();
	if ((isset($_POST['vid']) and !empty($_POST['id'])) and isset($_POST['rpm']) and isset($_POST['fc']) and isset($_POST['sc']) and isset($_POST['abc'])){
		$vid = $_POST['vid'];
		$rpmcode = $_POST['rpm'];
		$fuelcode = $_POST['fc'];
		$seatcode = $_POST['sc'];
		$abscode = $_POST['abc'];
		try{
			$conn = new PDO("mysql:host={$host};dbname={$dbname}", $username, $password);
			$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "INSERT INTO obd(vehicleID, RPM, fuelcode, seatbeltcode, abscode) VALUES ({$vid}, {$rpmcode}, {$fuelcode}, {$seatcode}, {$abscode})";
				
			if ($conn->query($sql) === TRUE){
				print json_encode("success");
			}			
		}
		catch (PDOException $e){
			echo "Connection failed" . $e->getMessage();
		}
			$conn = null;
	}
	else{
		print json_encode("POST parameters set incorrectly");
	}
	$conn = null;