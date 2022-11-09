import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servos", group = "Driving")
public class DriverGrabServosOpMode extends LinearOpMode {

    static final double MAX_POS =  1.0;
    static final double MIN_POS =  0.0;

    // Define class members
    Servo   servo;
    double  position = (MAX_POS - MIN_POS) / 2; // Start at halfway position

    @Override
    public void runOpMode() {

        // Find servo
        servo = hardwareMap.get(Servo.class, "grabber");

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
                // CLOSE SERVO
            }
            // Update Previous State
            xPrevState = xCurrState;

            // Press B button to open hand
            bCurrState = gamepad2.b;
            if (bCurrState && bCurrState != bPrevState) {
                // OPEN SERVO
            }
            // Update Previous State
            bPrevState = bCurrState;

            // Display the current value
            telemetry.addData("Servo Position", "%5.2f", position);
            telemetry.update();
        }
    }
}
