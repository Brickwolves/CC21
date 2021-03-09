package org.firstinspires.ftc.teamcode.TeleOp.Diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Controls.Controller2;
import org.firstinspires.ftc.teamcode.Hardware.MecanumRobot;
import org.firstinspires.ftc.teamcode.Utilities.Utils;

import static org.firstinspires.ftc.teamcode.Utilities.DashConstants.Dash_Shooter.ps_rpm;
import static org.firstinspires.ftc.teamcode.Utilities.DashConstants.Dash_Shooter.rpm;

@TeleOp(name = "Shooter Diagnostic TeleOp", group="Linear TeleOp")
public class ShooterDiag extends LinearOpMode {

    private MecanumRobot robot;
    private Controller2 controller;

    public void initialize() {
        Utils.setOpMode(this);
        robot = new MecanumRobot();
        controller = new Controller2(gamepad1);
    }

    @Override
    public void runOpMode() {

        initialize();
        waitForStart();

        while (opModeIsActive()) {
            controller.updateToggles();

            robot.shooter.feederState(controller.src.right_trigger > 0.75);
            if (controller.circle_toggle) {
                robot.intake.armDown();
                robot.shooter.setRPM(ps_rpm);
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


