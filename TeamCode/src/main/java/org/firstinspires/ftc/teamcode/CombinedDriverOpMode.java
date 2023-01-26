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
	boolean parking = false;
	
	// Time Milestones in ms
	double moveToSleeveTime = 680;
	double moveDelay = 3000;
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
					leftDrive.setPower(0.5);
					rightDrive.setPower(0.5);
				}
				else if (time < moveToSleeveTime) {
					leftDrive.setPower(0.3);
					rightDrive.setPower(0.7);
				}
				else {
					leftDrive.setPower(0);
					rightDrive.setPower(0);
					
					// Get Color Information
					NormalizedRGBA colors = colorSensorController.getColors();
					
					telemetry.addData("Alpha: ", colors.alpha);
					telemetry.addData("Red: ", colors.red);
					telemetry.addData("Green: ", colors.green);
					telemetry.addData("Blue: ", colors.blue);
					telemetry.update();
					
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
					
					lookingForColor = false;
					delaying = true;
					runtime.reset();
					}
			}
			else if (delaying) {
				if (time > moveDelay) {
					delaying = false;
					parking = true;
					runtime.reset();
				}
			}
			else if (parking) {
				telemetry.addData("Dir: ", parkingDirection);
				telemetry.update();
				
				switch (parkingDirection) {
					case LEFT:
						// Park Left
						if (time < parkTimeLeft / 2) {
							// Go Straight
							leftDrive.setPower(0.5);
							rightDrive.setPower(0.5);
						}
						else if (time < parkTimeLeft) {
							// Move Back to the Right
							leftDrive.setPower(0.7);
							rightDrive.setPower(0.3);
						}
						else {
							parking = false;
						}
						break;
					default:
					case MIDDLE:
						// Park Middle
						if (time < parkTimeMiddle * 2 / 3) {
							// Turn back to the right
							leftDrive.setPower(0.7);
							rightDrive.setPower(0.3);
						}
						else if (time < parkTimeMiddle) {
							// Go straight
							leftDrive.setPower(0.5);
							rightDrive.setPower(0.5);
						}
						else {
							leftDrive.setPower(0);
							rightDrive.setPower(0);
							parking = false;
						}
						break;
					case RIGHT:
						// Park Right
						if (time < parkTimeMiddle) {
							
						}
						else {
							parking = false;
						}
						break;
				}
			}
		}
	}
}
