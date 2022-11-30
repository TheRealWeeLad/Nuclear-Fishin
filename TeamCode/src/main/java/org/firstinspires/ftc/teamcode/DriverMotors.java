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
	
	// Determine if OpMode was instantiated or run from Driver Hub
	boolean instantiated = false;
	
	public DriveMotors(DcMotor left, DcMotor right, DcMotor extend, Gamepad gp1, Gamepad gp2) {
		// Initialize motor variables - string parameters set on driver hub
		leftDrive  = left;
		rightDrive = right;
		extendDrive = extend;
		
		// Initialize gamepads
		gamepad1 = gp1;
		gamepad2 = gp2;
		
		// Motor axles are disaligned, so one must be reversed
		leftDrive.setDirection(DcMotor.Direction.REVERSE);
		rightDrive.setDirection(DcMotor.Direction.FORWARD);
		
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
			
			// Motor axles are disaligned, so one must be reversed
			leftDrive.setDirection(DcMotor.Direction.REVERSE);
			rightDrive.setDirection(DcMotor.Direction.FORWARD);
		}
		
		// Setup a variable for each motor
		double leftPower;
		double rightPower;
		double extendPower;

		// Combines drive power and turn power in one expression
		double drive = -gamepad1.left_stick_y;
		double turn  =  gamepad1.right_stick_x;
		leftPower	= Range.clip(drive + turn, -1.0, 1.0);
		rightPower   = Range.clip(drive - turn, -1.0, 1.0);

		// Tilt left stick forward on 2nd gamepad to extend arm
		double extend = -gamepad2.left_stick_y;
		extendPower = Range.clip(extend, -1.0, 1.0);

		// Send calculated power to wheels
		leftDrive.setPower(leftPower);
		rightDrive.setPower(rightPower);
		extendDrive.setPower(extendPower);
	}
}
