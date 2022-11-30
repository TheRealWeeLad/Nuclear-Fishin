package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.DriveGrabServos;

@TeleOp(name="CombinedDriver", group="Driving")
public class CombinedDriverOpMode extends LinearOpMode {
	// Declare Separate Drive Parts
	DriveGrabServos servoController;
	DriveMotors motorController;
	
	// Instantiate Timer
	ElapsedTime runtime = new ElapsedTime();
	
	@Override
	public void runOpMode() {
		// Find motors and servos
		DcMotor leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
		DcMotor rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
		DcMotor extendDrive = hardwareMap.get(DcMotor.class, "extend_drive");
		Servo servoLeft = hardwareMap.get(Servo.class, "grabber_left");
		Servo servoRight = hardwareMap.get(Servo.class, "grabber_right");
		
		// Instantiate Drive Parts
		servoController = new DriveGrabServos(servoLeft, servoRight, gamepad2);
		motorController = new DriveMotors(leftDrive, rightDrive, extendDrive, gamepad1, gamepad2);
		
		// Wait for OpMode to begin
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			// Control Servos and Motors separately
			servoController.runOpMode();
			motorController.runOpMode();
			
			// Display Runtime
			telemetry.addData("Status", "Run Time: " + runtime.toString());
			telemetry.update();
		}
	}
}
