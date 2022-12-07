package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.rev.RevColorSensorV3;

@TeleOp(name="Color Sensor", group="Sensors")
public class ColorSensorController extends LinearOpMode {

  RevColorSensorV3 colorSensor;	// Hardware Device Object

  @Override
  public void runOpMode() {
		// bPrevState and bCurrState represent the previous and current state of the button.
		boolean bPrevState = false;
		boolean bCurrState = false;
	
		// bLedOn represents the state of the LED.
		boolean bLedOn = true;
	
		// get a reference to our ColorSensor object.
		colorSensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");
	
		// Set the LED in the beginning
		colorSensor.enableLed(bLedOn);
	
		// wait for the start button to be pressed.
		waitForStart();
	
		while (opModeIsActive()) {
	
		  // check the status of the x button on either gamepad.
		  bCurrState = gamepad1.x;
	
		  // check for button state transitions.
		  if (bCurrState && (bCurrState != bPrevState))  {
	
				// button is transitioning to a pressed state. So Toggle LED
				bLedOn = !bLedOn;
				colorSensor.enableLed(bLedOn);
		  }
	
		  // update previous state variable.
		  bPrevState = bCurrState;
		  
		  // send the info back to driver station using telemetry function.
		  telemetry.addData("LED", bLedOn ? "On" : "Off");
		  telemetry.addData("Clear", colorSensor.alpha());
		  telemetry.addData("Red  ", colorSensor.red());
		  telemetry.addData("Green", colorSensor.green());
		  telemetry.addData("Blue ", colorSensor.blue());
	
		  telemetry.update();
		}
  }
}
