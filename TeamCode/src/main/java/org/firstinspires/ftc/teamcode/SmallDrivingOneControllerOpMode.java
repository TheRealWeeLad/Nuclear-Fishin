package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.lang.Math;

@TeleOp(name="Small Driving One Controller", group="Small")
public class SmallDrivingOneControllerOpMode extends LinearOpMode {
	// Declare Motors
	DcMotor frontLeftDrive;
	DcMotor frontRightDrive;
	DcMotor backLeftDrive;
	DcMotor backRightDrive;
	
	// Declare Servos
	CRServoImpl leftRotate;
	CRServoImpl rightRotate;
	Servo leftClamp;
	Servo rightClamp;
	
	// SERVO CONSTANTS
	final double leftPercent = 0.55;
	final double rightPercent = 0.35;
	final double leftRestState = 0.2;
	final double rightRestState = 0.8;
	// final double leftUpPercent = 0.2;
	// final double rightUpPercent = 0.8;
	// final double leftRotateRest = 0.5;
	// final double rightRotateRest = 0.5;
	// final double leftDownPercent = 0.4;
	// final double rightDownPercent = 0.4;
	
	// Robot Speed
	final double DEFAULT_SPEED = 0.5;
	// final double TURBO_SPEED = 0.75;
	final double TURBO_SPEED = 1;
	double speed = DEFAULT_SPEED;
	final double rotateSpeed = 0.45;
	
	// Declare IMU
	IMU imu;
	
	// Instantiate Timer
	ElapsedTime runtime = new ElapsedTime();
	
	@Override
	public void runOpMode() {
		// Find motors and servos
		frontLeftDrive  = hardwareMap.get(DcMotor.class, "motor_1");
		frontRightDrive = hardwareMap.get(DcMotor.class, "motor_3");
		backLeftDrive = hardwareMap.get(DcMotor.class, "motor_4");
		backRightDrive = hardwareMap.get(DcMotor.class, "motor_2");
		leftRotate = hardwareMap.get(CRServoImpl.class, "rotate_1");
		rightRotate = hardwareMap.get(CRServoImpl.class, "rotate_2");
		leftClamp = hardwareMap.get(Servo.class, "clamp_1");
		rightClamp = hardwareMap.get(Servo.class, "clamp_2");
		
		// Reverse Right Motors
		frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
		backRightDrive.setDirection(DcMotor.Direction.REVERSE);
		
		// Initialize IMU
		imu = hardwareMap.get(IMU.class, "imu");
		IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
			RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
			RevHubOrientationOnRobot.UsbFacingDirection.UP));
		imu.initialize(parameters);
		
		// Wait for OpMode to begin
		waitForStart();
		runtime.reset();
		
		if (isStopRequested()) return;
		
		while (opModeIsActive()) {
			
			// Declare x and y
			double y = gamepad1.right_stick_y;
			double x = -gamepad1.right_stick_x;
			double rx = -gamepad1.left_stick_x;
			
			// Check if turbo is activated
			speed = DEFAULT_SPEED;
			if (gamepad1.right_trigger > 0.5) {
				speed = TURBO_SPEED;
			}
			
			robotDrive(x, y, rx);
			//fieldDrive(x, y, rx);
			
			clamp();
			rotate();
			
			// Display Runtime
			telemetry.addData("Status", "Run Time: " + runtime.toString());
			telemetry.update();
		}
	}
	
	// Robot-centric Driving
	void robotDrive(double x, double y, double rx) {
		// Apply Joystick values to motors
		double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
		
		frontLeftDrive.setPower((y + x + rx) * speed / denominator);
		backLeftDrive.setPower((y - x + rx) * speed / denominator);
		frontRightDrive.setPower((y - x - rx) * speed / denominator);
		backRightDrive.setPower((y + x - rx) * speed / denominator);
	}
	
	//Field-centric Driving
	void fieldDrive(double x, double y, double rx) {
		double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
		double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
		double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
		
		double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
		frontLeftDrive.setPower((rotY + rotX + rx) / denominator);
		backLeftDrive.setPower((rotY - rotX + rx) / denominator);
		frontRightDrive.setPower((rotY - rotX - rx) / denominator);
		backRightDrive.setPower((rotY + rotX - rx) / denominator);
	}
	
	// Clamp
	void clamp() {
		// Save current pressed state of X and B buttons
		boolean xPrevState = false;
		boolean xCurrState = false;
		boolean bPrevState = false;
		boolean bCurrState = false;

		// Press X button to close hand
		xCurrState = gamepad1.x;
		if (xCurrState && xCurrState != xPrevState) {
			clampNormal();
		}
		// Update Previous State
		xPrevState = xCurrState;

		// Press B button to open hand
		bCurrState = gamepad1.b;
		if (bCurrState && bCurrState != bPrevState) {
			restNormal();
		}
		// Update Previous State
		bPrevState = bCurrState;
	}
	void clampNormal() {
		leftClamp.setPosition(leftPercent);
		rightClamp.setPosition(rightPercent);
	}
	void restNormal() {
		leftClamp.setPosition(leftRestState);
		rightClamp.setPosition(rightRestState);
	}
	
	// Rotate Claw
	void rotate() {
		double rot_left = gamepad1.left_bumper ? 1 : 0;
		double rot_right = gamepad1.right_bumper ? 1 : 0;
		
		if (rot_left == 1) {
			leftRotate.setPower(rotateSpeed);
			rightRotate.setPower(-rotateSpeed);
			telemetry.addData("Up", rotateSpeed);
		}
		else if (rot_right == 1) {
			leftRotate.setPower(-rotateSpeed);
			rightRotate.setPower(rotateSpeed);
			telemetry.addData("Down", rotateSpeed);
		}
		else {
			leftRotate.setPower(0);
			rightRotate.setPower(0);
			telemetry.addData("Rest", 0);
		}
		
		// if (Math.abs(rot) > 0.1) { 
		// 	leftRotate.setPower(rot * rotateSpeed);
		// 	rightRotate.setPower(-rot * rotateSpeed);
		// 	telemetry.addData("Up/Down", rot);
		// }
		// else {
		// 	leftRotate.setPower(0);
		// 	rightRotate.setPower(0);
		// 	telemetry.addData("Rest", rot);
		// }
		//Save current pressed state of X and B buttons
		// boolean upPrevState = false;
		// boolean upCurrState = false;
		// boolean downPrevState = false;
		// boolean downCurrState = false;
		// boolean rightPrevState = false;
		// boolean rightCurrState = false;

		// // Press Up on D-pad to go up
		// upCurrState = gamepad2.dpad_up;
		// if (upCurrState && upCurrState != upPrevState) {
		// 	leftRotate.setPosition(leftUpPercent);
		// 	rightRotate.setPosition(rightUpPercent);
		// 	telemetry.addData(">", "UP");
		// }
		// // Update Previous State
		// upPrevState = upCurrState;

		// // Press Down on D-pad to go down
		// downCurrState = gamepad2.dpad_down;
		// if (downCurrState && downCurrState != downPrevState) {
		// 	leftRotate.setPosition(leftDownPercent);
		// 	rightRotate.setPosition(rightDownPercent);
		// 	telemetry.addData(">", "DOWN");
		// }
		// // Update Previous State
		// downPrevState = downCurrState;
		
		// // Press Right on D-pad to rest
		// rightCurrState = gamepad2.dpad_right;
		// if (rightCurrState && rightCurrState != rightPrevState) {
		// 	leftRotate.setPosition(leftRotateRest);
		// 	rightRotate.setPosition(rightRotateRest);
		// 	telemetry.addData(">", "RIGHT");
		// }
		// // Update Previous State
		// rightPrevState = rightCurrState;
	}
}