package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Motors", group="Driving")
public class DriveMotors extends LinearOpMode {

	// Declare OpMode members.
	ElapsedTime runtime = new ElapsedTime();
	DcMotor leftDrive = null;
	DcMotor rightDrive = null;
	DcMotor extendDrive = null;
	Gamepad gamepad1;
	Gamepad gamepad2;
	
	// Set speed of driving as a percent of total speed
	final double DEFAULT_SPEED = 0.5;
	double speed = DEFAULT_SPEED;
	
	// Store current Extend Arm target position
	int targetPos = 0;
	
	// Determine if OpMode was instantiated or run from Driver Hub
	boolean instantiated = false;
	
	public DriveMotors(DcMotor left, DcMotor right, DcMotor extend, Gamepad gp1, Gamepad gp2) {
		// Initialize motor variables - string parameters set on driver hub
		leftDrive  = left;
		rightDrive = right;
		extendDrive = extend;
		
		// Set Extending Arm to Position Mode
		//extendDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		
		// Initialize gamepads
		gamepad1 = gp1;
		gamepad2 = gp2;
		
		instantiated = true;
	}

	@Override
	public void runOpMode() {
		// Initialize motors if not instantiated
		if (!instantiated) {
			// Initialize motor variables - string parameters set on driver hub
			leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
			rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
			extendDrive = hardwareMap.get(DcMotor.class, "extend_drive");
			
			// Set Extending Arm to Position Mode
			//extendDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		}
		
		// Check if turbo is activated
		speed = DEFAULT_SPEED;
		if (gamepad1.right_trigger > 0.5) {
			speed = 1;
		}
		
		// Setup a variable for each motor
		double leftPower;
		double rightPower;

		// Combines drive power and turn power in one expression
		double drive = -gamepad1.left_stick_y;
		double turn  =  gamepad1.left_stick_x;
		leftPower	= Range.clip(speed * (drive + turn), -1.0, 1.0);
		rightPower   = Range.clip(speed * (drive - turn), -1.0, 1.0);

		// Set Position for Extend Arm to go to
		if (gamepad2.dpad_down) {
			targetPos = 0;
		}
		else if (gamepad2.dpad_up) {
			targetPos = 2;
		}

		// Send calculated power to wheels and arm
		leftDrive.setPower(leftPower);
		rightDrive.setPower(rightPower);
		//extendDrive.setTargetPosition(targetPos);
		
	}
}
