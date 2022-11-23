package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servos", group = "Driving")
public class DriveGrabServosOpMode extends LinearOpMode {

    // Define class members
    Servo servoLeft;
    Servo servoRight;
    
    // CONSTANTS
    final double leftPercent = 0.58;
    final double rightPercent = 0.42;
    final double leftRestState = 0.48;
    final double rightRestState = 0.52;

    @Override
    public void runOpMode() {

        // Find servo
        servoLeft = hardwareMap.get(Servo.class, "grabber_left");
        servoRight = hardwareMap.get(Servo.class, "grabber_right");

        // Save current pressed state of X and B buttons
        boolean xPrevState = false;
        boolean xCurrState = false;
        boolean bPrevState = false;
        boolean bCurrState = false;

        // Wait for start
        waitForStart();

        while(opModeIsActive()){
            // Press X button to close hand
            xCurrState = gamepad2.x;
            if (xCurrState && xCurrState != xPrevState) {
                servoLeft.setPosition(leftPercent);
                servoRight.setPosition(rightPercent);
            }
            // Update Previous State
            xPrevState = xCurrState;

            // Press B button to open hand
            bCurrState = gamepad2.b;
            if (bCurrState && bCurrState != bPrevState) {
                servoLeft.setPosition(leftRestState);
                servoRight.setPosition(rightRestState);
            }
            // Update Previous State
            bPrevState = bCurrState;

            // Display the current value
            telemetry.addData("Current Grab State:", bCurrState);
            telemetry.update();
        }
    }
}
