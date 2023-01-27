package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import org.firstinspires.ftc.teamcode.ColorSensorController;

import java.lang.Math;

@TeleOp(name="Combined Autonomous", group="Autonomous")
public class CombinedAutonomousOpMode extends LinearOpMode {
	// Instantiate Timer
	ElapsedTime runtime = new ElapsedTime();
	
	// Declare color sensor and controller
	RevColorSensorV3 colorSensor;
	ColorSensorController colorSensorController;
	
	// Declare Motors and Servos
	DcMotor leftDrive;
	DcMotor rightDrive;
	DcMotor extendDrive;
	Servo leftServo;
	Servo rightServo;
	
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
	int moveToSleeveTime = 770;
	int moveDelay = 1500;
	int movePoleTime = 800;
	int riseTime = 1000;
	int movePoleTimeAgain = 200;
	int reverseTime = 1000;
	int parkTimeLeft = 1500;
	int parkTimeMiddle = 1700;
	int parkTimeRight = 1300;
	
	// Determine Parking Direction
	enum ParkingDirection {LEFT, MIDDLE, RIGHT};
	ParkingDirection parkingDirection;
	
	@Override
	public void runOpMode() {
		// Instantiate Color Sensor
		colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");
		
		// Get Reference to Color Sensor Controller
		colorSensorController = new ColorSensorController(colorSensor);
		
		// Find Motors and Servos
		leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
		rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
		extendDrive = hardwareMap.get(DcMotor.class, "extend_drive");
		leftServo = hardwareMap.get(Servo.class, "grabber_left");
		rightServo = hardwareMap.get(Servo.class, "grabber_right");
		
		// Reverse Left Motor
		leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
		
		// Wait for OpMode to start
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			double time = runtime.milliseconds();
			
			if (lookingForColor) {
				// Move Forward to Sleeve
				if (time < moveToSleeveTime * 1.5 / 7) {
					forward();
					telemetry.addData("Moving To Sleeve Forward: ", time);
				}
				else if (time < moveToSleeveTime) {
					left();
					telemetry.addData("Moving To Sleeve Left: ", time);
				}
				else {
					off();
					
					lookingForColor = false;
					delaying = true;
					runtime.reset();
					}
			}
			else if (delaying) {
				telemetry.addData("Delaying: ", time);
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
				else if (max == colorsList[1]) {
					parkingDirection = ParkingDirection.MIDDLE;
				}
				else if (max == colorsList[2]) {
					parkingDirection = ParkingDirection.RIGHT;
				}
				else {
					parkingDirection = ParkingDirection.MIDDLE;
				}
				
				telemetry.addData("Red: ", colors.red);
				telemetry.addData("Green: ", colors.green);
				telemetry.addData("Blue: ", colors.blue);
				telemetry.addData("Dir: ", parkingDirection);
				
				checkingForColor = false;
				movingToPole = true;
				runtime.reset();
			}
			else if (movingToPole) {
				telemetry.addData("Dir: ", parkingDirection);
				if (time < movePoleTime * 2 / 3) {
					forward();
					
					telemetry.addData("Moving to Pole Forward: ", time);
				}
				else if (time < movePoleTime) {
					right();
					
					telemetry.addData("Moving to Pole Right: ", time);
				}
				else {
					off();
					
					movingToPole = false;
					movingToPoleAgain = true;
					runtime.reset();
				}
			}
			else if (rising) {
				if (time < riseTime) {
					extend();
					telemetry.addData("Rising: ", time);
				}
				else {
					extendOff();
					
					rising = false;
					movingToPoleAgain = true;
					runtime.reset();
				}
			}
			else if (movingToPoleAgain) {
				if (time < movePoleTimeAgain) {
					forward();
					
					telemetry.addData("Pole Again: ", time);
				}
				else {
					off();
					
					// Release Cone
					openClaw();
					
					movingToPoleAgain = false;
					reversing = true;
					runtime.reset();
				}
			}
			else if (reversing) {
				if (time < reverseTime) {
					backward();
					
					telemetry.addData("Reversing: ", time);
				}
				else {
					off();
					
					reversing = false;
					parking = true;
					runtime.reset();
				}
			}
			else if (parking) {
				parkingDirection = ParkingDirection.MIDDLE;
				telemetry.addData("Parking ", parkingDirection);
				telemetry.addData("Parking Time: ", time);
				
				switch (parkingDirection) {
					case LEFT:
						// Park Left
						parkLeft(time);
						break;
					case RIGHT:
						/* Park Right
						parkRight(time);
						break; */
					default:
					case MIDDLE:
						// Park Middle
						parkMiddle(time);
						break;
				}
			}
			
			telemetry.update();
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
		rightDrive.setPower(0.3);
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
	
	void openClaw() {
		leftServo.setPosition(0.6);
		rightServo.setPosition(0.4);
	}
	
	void parkLeft(double time) {
		if (time < parkTimeLeft / 3) {
			left();
		}
		else if (time < parkTimeLeft) {
			right();
		}
		else {
			off();
			parking = false;
		}
	}
	
	void parkRight(double time) {
		if (time < parkTimeRight / 2) {
			// Go Straight
			forward();
		}
		else if (time < parkTimeRight) {
			// Move Back to the Right
			right();
		}
		else {
			off();
			parking = false;
		}
	}
	
	void parkMiddle(double time) {
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
