package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
	
	@Override
	public void runOpMode() {
		// Instantiate Color Sensor
		colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");
		
		// Get Reference to Color Sensor Controller
		colorSensorController = new ColorSensorController(colorSensor);
		
		// Wait for OpMode to start
		waitForStart();
		
		while (opModeIsActive()) {
			// Get Color Information
			int[] colors = colorSensorController.getColors();
			
			int alpha = colors[0];
			int red = colors[1];
			int green = colors[2];
			int blue = colors[3];
			
			telemetry.addData("Alpha: ", alpha);
			telemetry.addData("Red: ", red);
			telemetry.addData("Green: ", green);
			telemetry.addData("Blue: ", blue);
			telemetry.update();
		}
	}
}
