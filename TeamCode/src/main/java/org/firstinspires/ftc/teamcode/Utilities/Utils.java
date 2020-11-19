package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Utils {

    public static HardwareMap hardwareMap;
    public static Telemetry telemetry;

    public static void setHardwareMap(HardwareMap hardwareMap){ Utils.hardwareMap = hardwareMap; }
    public static void setTelemetry(Telemetry telemetry) { Utils.telemetry = telemetry; }


    /**
     * @param position
     * @param distance
     * @param acceleration
     * @return
     */
    public static double powerRamp(double position, double distance, double acceleration){

        position += 0.01;           // Necessary otherwise we're stuck at position 0 (sqrt(0) = 0)
        double normFactor = 1 / Math.sqrt(0.1 * distance);

        // Modeling a piece wise of power as a function of distance
        double p1 = normFactor * Math.sqrt(acceleration * position);
        double p2 = 1;
        double p3 = normFactor * (Math.cbrt(acceleration * (distance - position)));
        telemetry.addData("p3", p3);
        telemetry.addData("normFactor", normFactor);
        telemetry.addData("acceleration", acceleration);
        telemetry.addData("distance", distance);
        telemetry.addData("position", position);
        return Math.min(Math.min(p1, p2), p3)+0.1;
    }


    /**
     * Super simple method to check toggles on buttons
     * @param current
     * @param previous
     * @return
     */
    public static Boolean buttonTapped(boolean current, boolean previous){
        if (current && !previous )return true;
        else if (!current) return false;
        else return previous;
    }


    /**
     * @param baseRGB
     * @param currentRGB
     * @return
     */
    public static double distance2Color(double[] baseRGB, double[] currentRGB){
        return Math.sqrt(Math.pow(baseRGB[0] - currentRGB[0], 2) + Math.pow(baseRGB[1] - currentRGB[1], 2) + Math.pow(baseRGB[2] - currentRGB[2], 2));
    }

    /**
     * @param angle
     * @return coTermAngle
     */
    public static double coTerminal(double angle){
        double coTermAngle = (angle + 180) % 360;
        coTermAngle -= 180;
        return coTermAngle;
    }
}
