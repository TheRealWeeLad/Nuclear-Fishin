package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Chad Auto", group="New")
public class ColorAuto extends LinearOpMode {
	// Declare Parts
	RevColorSensorV3 color;
	DcMotor frontLeftDrive;
	DcMotor frontRightDrive;
	DcMotor backLeftDrive;
	DcMotor backRightDrive;
	CRServoImpl leftRotate;
	CRServoImpl rightRotate;
	Servo leftClamp;
	Servo rightClamp;
	IMU imu;
	
	// Time milestones in milliseconds
	final int movingConeNewTime = 600;
	// final int movingOffWallTime = 400;
	// final int rotatingTime = 1700;
	// final int movingToConeTime = 2050;
	final int checkColorTime = 1000;
	final int repositioningForwardTime = 450;
	final int repositioningBackwardTime = 750;
	final int parkingLeftTime = 1000;
	final int parkingRightTime = 1000;
	
	// Sections of Autonomous
	boolean movingToCone = true;
	boolean checkingColor = false;
	// boolean colorChecked = false;
	boolean repositioning = false;
	boolean parking = false;
	
	// Parking Direction
	final String[] parkingDirections = {"LEFT", "MIDDLE", "RIGHT"};
	String parkingDirection = "NONE";
	
	// Initialize runtime
	ElapsedTime runtime = new ElapsedTime();
	
	@Override
	public void runOpMode() {
		// Find hardware parts
		color = hardwareMap.get(RevColorSensorV3.class, "color_1");
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
		
		waitForStart();
		
		runtime.reset();
		double time = 0;
		
		while (opModeIsActive()) {
			time = runtime.milliseconds();
			
			raiseClaw();
			
			if (movingToCone) {
			// 	if (time < movingOffWallTime) {
			// 		forward();
			// 		telemetry.addData("Moving Off Wall:", time);
			// 	}
			// 	else if (time < rotatingTime) {
			// 		rotate();
			// 		telemetry.addData("Rotating:", time);
			// 	}
			// 	else if (time < movingToConeTime) {
			// 		backward();
			// 		if (!colorChecked) {
			// 			checkColors();
			// 		}
			// 		telemetry.addData("Moving To Cone:", time);
			// 	}
			// 	else {
			// 		movingToCone = false;
			// 		checkingColor = true;
			// 		runtime.reset();
			// 	}
			
				if (time < movingConeNewTime) {
					backward();
				}
				else {
					movingToCone = false;
					checkingColor = true;
				}
			}
			else if (checkingColor) {
				// if (colorChecked) {
				// 	checkingColor = false;
				// 	repositioning = true;
				// 	runtime.reset();
				// }
				if (time < checkColorTime) {
					halt();
					checkColors();
				}
				else {
					checkingColor = false;
					repositioning = true;
					runtime.reset();
				}
			}
			else if (repositioning) {
				if (time < repositioningForwardTime) {
					backward();
					telemetry.addData("Repositioning Forward:", time);
				}
				else if (time < repositioningBackwardTime) {
					forward();
					telemetry.addData("Repositioning Backward: ", time);
				}
				else {
					repositioning = false;
					parking = true;
					runtime.reset();
				}
			}
			else if (parking) {
				if (parkingDirection.equals("LEFT")) {
					if (time < parkingLeftTime) {
						right();
					}
					else {
						halt();
						parking = false;
						runtime.reset();
					}
				}
				else if (parkingDirection.equals("MIDDLE")) {
					halt();
					parking = false;
					runtime.reset();
				}
				else if (parkingDirection.equals("RIGHT")) {
					if (time < parkingRightTime) {
						left();
					}
					else {
						halt();
						parking = false;
						runtime.reset();
					}
				}
				else {
					halt();
					parking = false;
					runtime.reset();
				}
				
				telemetry.addData("Parking:", time);
			}
			else {
				halt();
				checkColors();
			}
			
			telemetry.addData("Parking Direction:", parkingDirection);
			telemetry.update();
		}
	}
	
	void checkColors() {
		NormalizedRGBA normalizedColors = color.getNormalizedColors();
		float[] colors = {normalizedColors.red, normalizedColors.green, normalizedColors.blue};
		telemetry.addData("Red: ", colors[0]);
		telemetry.addData("Green: ", colors[1]);
		telemetry.addData("Blue: ", colors[2]);
		
		float highest = Math.max(colors[0], Math.max(colors[1], colors[2]));
		String[] colorNames = {"Red", "Green", "Blue"};
		
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] == highest) {
				telemetry.addData("Highest: ", colorNames[i]);
				
				parkingDirection = parkingDirections[i];
				
				// if (parkingDirection != "MIDDLE") {
				// 	colorChecked = true;
				// }
			}
		}
	}
	
	void forward() {
		powerMotorsRobot(0, -0.5, 0);
		// powerMotorsField(0, -0.5, 0);
	}
	void backward() {
		powerMotorsRobot(0, 0.5, 0);
		// powerMotorsField(0, 0.5, 0);
	}
	void left() {
		powerMotorsRobot(0.5, 0, 0);
		// powerMotorsField(0.5, 0, 0);
	}
	void right() {
		powerMotorsRobot(-0.5, 0, 0);
		// powerMotorsField(-0.5, 0, 0);
	}
	void rotate() {
		powerMotorsRobot(0, 0, 0.5);
		// powerMotorsField(0, 0, 0.5);
	}
	void halt() {
		powerMotorsRobot(0, 0, 0);
		// powerMotorsField(0, 0, 0);
	}
	void lowerClaw() {
		leftRotate.setPower(-0.35);
		rightRotate.setPower(0.35);
	}
	void raiseClaw() {
		leftRotate.setPower(0.35);
		rightRotate.setPower(-0.35);
	}
	void closeClaw() {
		leftClamp.setPosition(0.5);
		rightClamp.setPosition(0.4);
	}
	void openClaw() {
		leftClamp.setPosition(0.3);
		rightClamp.setPosition(0.7);
	}
	void spinSlow() {
		powerMotorsRobot(0, 0, 0.25);
	}
	void spinFast() {
		powerMotorsRobot(0, 0, 0.75);
	}
	void spinNormal() {
		powerMotorsRobot(0, 0, 0.5);
	}
	
	void powerMotorsRobot(double x, double y, double rx) {
		double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
		
		frontLeftDrive.setPower((y + x + rx) / denominator);
		backLeftDrive.setPower((y - x + rx) / denominator);
		frontRightDrive.setPower((y - x - rx) / denominator);
		backRightDrive.setPower((y + x - rx) / denominator);
	}
	
	void powerMotorsField(double x, double y, double rx) {
		double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
		double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
		double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
		
		double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
		frontLeftDrive.setPower((rotY + rotX + rx) / denominator);
		backLeftDrive.setPower((rotY - rotX + rx) / denominator);
		frontRightDrive.setPower((rotY - rotX - rx) / denominator);
		backRightDrive.setPower((rotY + rotX - rx) / denominator);
	}
}
