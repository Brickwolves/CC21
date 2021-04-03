package org.firstinspires.ftc.teamcode.TeleOp.Diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Controls.ControllerCollin;
import org.firstinspires.ftc.teamcode.Hardware.Mecanum;
import org.firstinspires.ftc.teamcode.Utilities.Utils;

import static org.firstinspires.ftc.teamcode.DashConstants.Dash_Shooter.rpm;

@Disabled
@TeleOp(name = "Shooter Diagnostic TeleOp", group="Linear TeleOp")
public class ShooterDiag extends LinearOpMode {

    private Mecanum robot;
    private ControllerCollin controller;

    public void initialize() {
        Utils.setOpMode(this);
        robot = new Mecanum();
        controller = new ControllerCollin(gamepad1);
    }

    @Override
    public void runOpMode() {

        initialize();
        waitForStart();

        while (opModeIsActive()) {
            controller.updateToggles();

            robot.shooter.feederState(controller.src.right_trigger > 0.75);
            if (controller.circle_toggle) {
                robot.intake.armMid();
                robot.shooter.setRPM(rpm);
            }
            else {
                robot.shooter.setPower(0);
            }



            Utils.multTelemetry.addData("RPM", robot.shooter.getRPM());
            Utils.multTelemetry.addData("Power", robot.shooter.getPower());
            Utils.multTelemetry.addData("Position", robot.shooter.getPosition());
            Utils.multTelemetry.addData("Imu", robot.imu.getAngle());

            Utils.multTelemetry.update();

        }
    }
}


