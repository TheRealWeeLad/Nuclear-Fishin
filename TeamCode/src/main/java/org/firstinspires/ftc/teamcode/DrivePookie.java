package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="Pookie Bear Driving", group="Pookie")
public class DrivePookie extends LinearOpMode {
	// Declare Servos
	Servo pinchServo;
	Servo planeServo;
	
	// Declare Motors
	DcMotor frontLeftDrive;
	DcMotor frontRightDrive;
	DcMotor backLeftDrive;
	DcMotor backRightDrive;
	DcMotor arm1Motor;
	DcMotor arm2Motor;
	
	// Declare IMU
	IMU imu;
	
	// Robot Speed
	final double DEFAULT_SPEED = 0.5;
	final double TURBO_SPEED = 0.75;
	double speed = DEFAULT_SPEED;
	
	// Arm Power
	final double ARM_POWER = 0.5;
	
	// Servo Angles - TODO: FIX!
	float pinchRestAngle = 0.1f;
	float pinchAngle = 0.5f;
	float planeRestAngle = 0.2f;
	float planeLaunchAngle = 1f;
	
	// State bools
	boolean pinching = false;
	boolean bPrevState = false;
	
	// Instantiate Timer
	ElapsedTime runtime = new ElapsedTime();
	
	@Override
	public void runOpMode() {
		// Find motors and servos
		pinchServo = hardwareMap.get(Servo.class, "pinch-servo");
		planeServo = hardwareMap.get(Servo.class, "plane-servo");
		frontLeftDrive  = hardwareMap.get(DcMotor.class, "motor_1");
		frontRightDrive = hardwareMap.get(DcMotor.class, "motor_3");
		backLeftDrive = hardwareMap.get(DcMotor.class, "motor_4");
		backRightDrive = hardwareMap.get(DcMotor.class, "motor_2");
		arm1Motor = hardwareMap.get(DcMotor.class, "arm-1");
		arm2Motor = hardwareMap.get(DcMotor.class, "arm-2");
		
		// Reverse Right Motors
		frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
		backRightDrive.setDirection(DcMotor.Direction.REVERSE);
		arm2Motor.setDirection(DcMotor.Direction.REVERSE);
		
		// Initialize IMU
		imu = hardwareMap.get(IMU.class, "imu");
		IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
			RevHubOrientationOnRobot.LogoFacingDirection.FORWARD, // CHANGE
			RevHubOrientationOnRobot.UsbFacingDirection.UP)); // CHANGE
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
			
			moveArms();
			
			pinch();
			launchAirplane();
		}
	}
	
	void robotDrive(double x, double y, double rx) {
		// Apply Joystick values to motors
		double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
		
		frontLeftDrive.setPower((y + x + rx) * speed / denominator);
		backLeftDrive.setPower((y - x + rx) * speed / denominator);
		frontRightDrive.setPower((y - x - rx) * speed / denominator);
		backRightDrive.setPower((y + x - rx) * speed / denominator);
	}
	
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
	
	void moveArms() {
		double power = gamepad2.left_stick_y;
		
		arm1Motor.setPower(power * ARM_POWER);
		arm2Motor.setPower(power * ARM_POWER);
	}
	
	void pinch() {
		// Save current pressed state of B button
		boolean bCurrState = false;

		// Press B button to Pinch
		bCurrState = gamepad2.b;
		if (bCurrState && bCurrState != bPrevState) {
			float angle = pinching ? pinchAngle : pinchRestAngle;
			pinchServo.setPosition(angle);
			pinching = !pinching;
		}
		// Update Previous State
		bPrevState = bCurrState;
	}
	
	void launchAirplane() {
		boolean lt = gamepad1.left_trigger > 0.5;
		
		if (lt) planeServo.setPosition(planeLaunchAngle);
		else planeServo.setPosition(planeRestAngle);
	}
}