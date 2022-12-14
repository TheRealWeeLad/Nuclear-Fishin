package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevColorSensorV3;


public class ColorSensorController {
	// Declare Color Sensor
	RevColorSensorV3 colorSensor;
	
	public class ColorInfo {
		public int alpha;
		public int red;
		public int green;
		public int blue;
		public int[] argb;
		
		public ColorInfo(int a, int r, int g, int b) {
			alpha = a;
			red = r;
			green = g;
			blue = b;
			argb = new int[] {alpha, red, green, blue};
		}
	}
	
	public ColorSensorController(RevColorSensorV3 colorSensor) {
		this.colorSensor = colorSensor;
	}
	
	public int[] getColors() {
		// Set the LED On
		colorSensor.enableLed(true);
		
		// Get Color Information
		int alpha = colorSensor.alpha();
		int red = colorSensor.red();
		int green = colorSensor.green();
		int blue = colorSensor.blue();
		
		ColorInfo colorInfo = new ColorInfo(alpha, red, green, blue);
		
		// Send back color information
		return colorInfo.argb;
  }
}
