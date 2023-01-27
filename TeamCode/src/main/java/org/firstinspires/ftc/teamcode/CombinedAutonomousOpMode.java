package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.Collection;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import android.graphics.Color;
import org.firstinspires.ftc.teamcode.ColorSensorController;

import java.lang.Math;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Combined Autonomous", group="Autonomous")
public class CombinedAutonomousOpMode extends LinearOpMode {
	// Instantiate Timer
	ElapsedTime runtime = new ElapsedTime();
	
	// Declare color sensor and controller
	RevColorSensorV3 colorSensor;
	ColorSensorController colorSensorController;
	
	// Bools for different sections of autonomous control
	boolean lookingForColor = true;
	boolean delaying = false;
	boolean checkingForColor = false;
	boolean movingToPole = false;
	boolean rising = false;
	boolean movingToPoleAgain = false;
	boolean reversing = false;
	boolean parking = false;
	
	// Time Milestones in ms
	double moveToSleeveTime = 680;
	double moveDelay = 3000;
	double movePoleTime = 500;
	double riseTime = 500;
	double movePoleTimeAgain = 100;
	double parkTimeLeft = 1300;
	double parkTimeMiddle = 1300;
	
	// Determine Parking Direction
	enum ParkingDirection {LEFT, MIDDLE, RIGHT};
	ParkingDirection parkingDirection;
	
	@Override
	public void runOpMode() {
		// Instantiate Color Sensor
		colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");
		
		// Get Reference to Color Sensor Controller
		colorSensorController = new ColorSensorController(colorSensor);
		
		// Find Motors
		DcMotor leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
		DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
		
		// Reverse Left Motor
		leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
		
		// Wait for OpMode to start
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			double time = runtime.milliseconds();
			
			if (lookingForColor) {
				// Move Forward to Sleeve
				if (time < moveToSleeveTime * 2.5 / 7) {
					forward();
				}
				else if (time < moveToSleeveTime) {
					left();
				}
				else {
					off();
					
					lookingForColor = false;
					delaying = true;
					runtime.reset();
					}
			}
			else if (delaying) {
				if (time > moveDelay) {
					delaying = false;
					checkingForColor = true;
				}
			}
			else if (checkingForColor) {
				// Get Color Information
				NormalizedRGBA colors = colorSensorController.getColors();

				// Check for greatest color
				float[] colorsList = {colors.red, colors.green, colors.blue};
				float max = Math.max(Math.max(colorsList[0], colorsList[1]), colorsList[2]);
				if (max == colorsList[0]) {
					parkingDirection = ParkingDirection.LEFT;
				}
				else if (max == colorsList[2]) {
					parkingDirection = ParkingDirection.RIGHT;
				}
				else if (max == colorsList[1]) {
					parkingDirection = ParkingDirection.MIDDLE;
				}
				else {
					parkingDirection = ParkingDirection.MIDDLE;
				}
				
				checkingForColor = false;
				movingToPole = true;
				runtime.reset();
			}
			else if (movingToPole) {
				if (time < movePoleTime) {
					forward();
				}
				else {
					movingToPole = false;
					rising = true;
					runtime.reset();
				}
			}
			else if (rising) {
				if (time < riseTime) {
					extend();
				}
				else {
					extendOff();
					
					rising = false;
					moveToPoleAgain = true;
					runtime.reset();
				}
			}
			else if (movingToPoleAgain) {
				if (time < movePoleTimeAgain) {
					forward();
				}
				
			}
			else if (parking) {
				telemetry.addData("Dir: ", parkingDirection);
				telemetry.update();
				
				switch (parkingDirection) {
					case LEFT:
						/* Park Left
						parkLeft();
						break; */
					case RIGHT:
						/* Park Right
						parkRight();
						break;*/
					default:
					case MIDDLE:
						// Park Middle
						parkMiddle();
						break;
				}
			}
		}
	}
	
	void forward() {
		leftDrive.setPower(0.5);
		rightDrive.setPower(0.5);
	}
	
	void backward() {
		leftDrive.setPower(-0.5);
		rightDrive.setPower(-0.5);
	}
	
	void left() {
		leftDrive.setPower(0.3);
		rightDrive.setPower(0.7);
	}
	
	void right() {
		leftDrive.setPower(0.7);
		rightDriver.setPower(0.3);
	}
	
	void off() {
		leftDrive.setPower(0);
		rightDrive.setPower(0);
	}
	
	void extend() {
		extendDrive.setPower(0.5);
	}
	
	void retract() {
		extendDrive.setPower(-0.5);
	}
	
	void extendOff() {
		extendDrive.setPower(0);
	}
	
	void parkLeft() {
		if (time < parkTimeLeft / 2) {
			// Go Straight
			forward();
		}
		else if (time < parkTimeLeft) {
			// Move Back to the Right
			right();
		}
		else {
			parking = false;
		}
	}
	
	void parkRight() {
		if (time < parkTimeMiddle) {
			
		}
		else {
			parking = false;
		}
	}
	
	void parkMiddle() {
		if (time < parkTimeMiddle * 2 / 3) {
			// Turn back to the right
			right();
		}
		else if (time < parkTimeMiddle) {
			// Go straight
			forward();
		}
		else {
			off();
			parking = false;
		}
	}
}
