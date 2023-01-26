package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name="Servos", group="Driving")
public class DriveGrabServos extends LinearOpMode {

	// Define class members
	Servo servoLeft;
	Servo servoRight;
	Gamepad gamepad2;
	
	// CONSTANTS
	final double leftPercent = 0.6;
	final double rightPercent = 0.4;
	final double leftRestState = 0.48;
	final double rightRestState = 0.52;
	
	// Determine whether OpMode was instantiated or run as from Driver Hub
	boolean instantiated = false;
	
	public DriveGrabServos(Servo left, Servo right, Gamepad gp2) {
		// Instantiate servos
		servoLeft = left;
		servoRight = right;
		
		// Instantiate gamepad2
		gamepad2 = gp2;
		
		instantiated = true;
	}

	@Override
	public void runOpMode() {
		// Find servos if not instantiated
		if (!instantiated) {
			servoLeft = hardwareMap.get(Servo.class, "grabber_left");
			servoRight = hardwareMap.get(Servo.class, "grabber_right");
		}
		
		// Save current pressed state of X and B buttons
		boolean xPrevState = false;
		boolean xCurrState = false;
		boolean bPrevState = false;
		boolean bCurrState = false;

		// Press X button to close hand
		xCurrState = gamepad2.x;
		if (xCurrState && xCurrState != xPrevState) {
			servoLeft.setPosition(leftPercent);
			servoRight.setPosition(rightPercent);
		}
		// Update Previous State
		xPrevState = xCurrState;

		// Press B button to open hand
		bCurrState = gamepad2.b;
		if (bCurrState && bCurrState != bPrevState) {
			servoLeft.setPosition(leftRestState);
			servoRight.setPosition(rightRestState);
		}
		// Update Previous State
		bPrevState = bCurrState;
	}
}
