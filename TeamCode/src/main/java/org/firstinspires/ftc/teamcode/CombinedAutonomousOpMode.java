package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import org.firstinspires.ftc.teamcode.ColorSensorController;

@TeleOp(name="Combined Autonomous", group="Autonomous")
public class CombinedAutonomousOpMode extends LinearOpMode {
	// Instantiate Timer
	ElapsedTime runtime = new ElapsedTime();
	
	// Declare color sensor and controller
	RevColorSensorV3 colorSensor;
	ColorSensorController colorSensorController;
	
	// Booleans for different sections of autonomous control
	boolean lookingForColor = true;
	
	@Override
	public void runOpMode() {
		// Instantiate Color Sensor
		colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");
		
		// Get Reference to Color Sensor Controller
		colorSensorController = new ColorSensorController(colorSensor);
		
		// Find Motors
		DcMotor leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
		DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
		
		// MAYBE REVERSE ONE MOTOR
		
		// Wait for OpMode to start
		waitForStart();
		
		while (opModeIsActive()) {
			if (lookingForColor) {
				// Move Forward
				leftDrive.setPower(1.0);
				rightDrive.setPower(1.0);
				
				// Get Color Information
				NormalizedRGBA colors = colorSensorController.getColors();
				
				telemetry.addData("Alpha: ", colors.alpha);
				telemetry.addData("Red: ", colors.red);
				telemetry.addData("Green: ", colors.green);
				telemetry.addData("Blue: ", colors.blue);
				telemetry.update();
				
				// TODO: CHECK FOR SLEEVE COLOR
			}
			
			
		}
	}
}
