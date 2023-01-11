package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;


public class ColorSensorController {
	// Declare Color Sensor
	RevColorSensorV3 colorSensor;
	
	public ColorSensorController(RevColorSensorV3 colorSensor) {
		this.colorSensor = colorSensor;
	}
	
	public NormalizedRGBA getColors() {
		// Set the LED On
		colorSensor.enableLed(true);
		
		// Send back color information
		return colorSensor.getNormalizedColors();
  }
}
