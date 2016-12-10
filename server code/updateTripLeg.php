<?php

	$username = "root";
	$password = "seng521";
	$host = "localhost";
	$dbname = "fleet";
	$response = array();
	if (isset($_POST['tripid']) and isset($_POST['time']) and isset($_POST['xloc']) and isset($_POST['yloc']) and isset($_POST['speed'])){
		$tripid = $_POST['id'];
		$time = $_POST['time'];
		$xloc = $_POST['xloc'];
		$yloc = $_POST['yloc'];
		$speed = $_POST['speed'];
		try{
			$conn = new PDO("mysql:host={$host};dbname={$dbname}", $username, $password);
			$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "INSERT INTO trips(tripID, time, xloc, yloc, speed) VALUES ({$tripid}, {$time}, {$xloc}, {$yloc}, {$speed})";
				
			if ($conn->query($sql) === TRUE){
				print json_encode("Success");
			}			
		}
		catch (PDOException $e){
			echo "Connection failed" . $e->getMessage();
		}
			$conn = null;
	}
