package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Hardware.MecanumRobot;
import org.firstinspires.ftc.teamcode.Utilities.DashConstants;
import org.firstinspires.ftc.teamcode.Utilities.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvPipeline;

@Autonomous(name="MarkIV - Scrimmage", group="Autonomous Linear Opmode")
public class MarkIV extends LinearOpMode
{
    private MecanumRobot mecanumRobot;

    OpenCvCamera webcam;

    private FtcDashboard dashboard = FtcDashboard.getInstance();
    private Telemetry dashboardTelemetry = dashboard.getTelemetry();
    private MultipleTelemetry multTelemetry = new MultipleTelemetry(telemetry, dashboardTelemetry);


    private static double ringCount = 0;
    private boolean ringsFound = false;

    public void initialize(){
        Utils.setOpMode(this);
        mecanumRobot = new MecanumRobot();
    }

    @Override
    public void runOpMode()
    {

        initialize();


        /*
        Set up camera, and pipeline

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"), cameraMonitorViewId);

        webcam.setPipeline(new RingDetectingPipeline());
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }
        });
        */


        telemetry.addLine("Waiting for start");
        telemetry.update();
        waitForStart();


        /*
        ACTION
         */
        if (opModeIsActive()){


            mecanumRobot.strafe(0, 12, 0, 0.09, null);
            sleep(5000);

            /*
            double MOE = 1;
            if (DashConstants.diagnostic_ring_count == 1.0 || DashConstants.diagnostic_ring_count == 4.0) ringsFound = true;

            // Go to 1st Position
            multTelemetry.addData("Status", "Strafing to Deposit Wobble Goal Position");
            multTelemetry.update();
            //mecanumRobot.strafe(90, 7, 0, 0.075, null);
            //mecanumRobot.strafe(0, 37, 0, 0.075, null);
            mecanumRobot.strafe(13.7, 38, 0, 0.075, null);


            if (DashConstants.diagnostic_ring_count == 0.0){

                // Go to A
                multTelemetry.addData("Status", "Moving to A");
                multTelemetry.update();
                //mecanumRobot.strafe(0, 34, 0, 0.075, null);
                //mecanumRobot.strafe(-90, 2, 0, 0.05, null);
                mecanumRobot.strafe(-3.37, 34, 0, 0.05, null);
            }
            else if (DashConstants.diagnostic_ring_count == 1.0){

                // Go to B
                multTelemetry.addData("Status", "Moving to B");
                multTelemetry.update();
                //mecanumRobot.strafe(0, 59, 0, 0.075, null);
                //mecanumRobot.strafe(-90, 25, 0, 0.075, null);
                mecanumRobot.strafe(-23, 64.08, 0, 0.1, null);

            }
            else {

                // Go to C
                multTelemetry.addData("Status", "Moving to C");
                multTelemetry.update();
                //mecanumRobot.strafe(0, 84, 0, 0.075, null);
                //mecanumRobot.strafe(-90, 2, 0, 0.075, null);
                mecanumRobot.strafe(-1.36, 84.02, 0, 0.1, null);
            }


            // Drop Wobble Goal
            multTelemetry.addData("Status", "Dropping Wobble Goal");
            multTelemetry.update();
            mecanumRobot.arm.down();
            mecanumRobot.claw.openFull();
            mecanumRobot.arm.up();
            sleep(1000);


            // Go to next Wobble Goal
            multTelemetry.addData("Status", "Strafing to pickup next Wobble Goal");
            multTelemetry.update();
            //mecanumRobot.strafe(-90, 18, 0, 0.075, null);
            //mecanumRobot.strafe(180, 30, 0, 0.075, null);
            mecanumRobot.strafe(-149.04, 35, 0, 0.075, null);

            */
        }
    }

    class RingDetectingPipeline extends OpenCvPipeline
    {
        boolean viewportPaused;

        // Init mats here so we don't repeat
        Mat YCbCr = new Mat();
        Mat outPut = new Mat();
        Mat upperCrop = new Mat();
        Mat lowerCrop = new Mat();

        // Rectangles starting coordinates      // Rectangles starting percentages
        int rectTopX1; int rectTopX2;           //double rectTopX1Percent = 0; double rectTopX2Percent = 0;
        int rectTopY1; int rectTopY2;           //double rectTopY1Percent = 0; double rectTopY2Percent = 0;

        // Rectangles starting coordinates      // Rectangles starting percentages
        int rectBottomX1; int rectBottomX2;     //double rectBottomX1Percent = 0; double rectBottomX2Percent = 0;
        int rectBottomY1; int rectBottomY2;     //double rectBottomY1Percent = 0; double rectBottomY2Percent = 0;


        @Override
        public Mat processFrame(Mat input)
        {
            // Convert & Copy to outPut image
            Imgproc.cvtColor(input, YCbCr, Imgproc.COLOR_RGB2YCrCb);
            input.copyTo(outPut);

            // Dimensions for top rectangle
            rectTopX1 = (int) (input.rows() * DashConstants.rectTopX1Percent);
            rectTopX2 = (int) (input.rows() * DashConstants.rectTopX2Percent) - rectTopX1;
            rectTopY1 = (int) (input.cols() * DashConstants.rectTopY1Percent);
            rectTopY2 = (int) (input.cols() * DashConstants.rectTopY2Percent) - rectTopY1;

            // Dimensions for bottom rectangle
            rectBottomX1 = (int) (input.rows() * DashConstants.rectBottomX1Percent);
            rectBottomX2 = (int) (input.rows() * DashConstants.rectBottomX2Percent) - rectBottomX1;
            rectBottomY1 = (int) (input.cols() * DashConstants.rectBottomY1Percent);
            rectBottomY2 = (int) (input.cols() * DashConstants.rectBottomY2Percent) - rectBottomY1;

            // VISUALIZATION: Create rectangles and scalars, then draw them onto outPut
            Scalar rectangleColor = new Scalar(0, 0, 255);
            Rect rectTop = new Rect(rectTopX1, rectTopY1, rectTopX2, rectTopY2);
            Rect rectBottom = new Rect(rectBottomX1, rectBottomY1, rectBottomX2, rectBottomY2);
            Imgproc.rectangle(outPut, rectTop, rectangleColor, 2);
            Imgproc.rectangle(outPut, rectBottom, rectangleColor, 2);




            // IDENTIFY RINGS //

            // Crop
            upperCrop = YCbCr.submat(rectTop);
            lowerCrop = YCbCr.submat(rectBottom);

            // Extract Channels [Y, Cr, Cb], where 2 = index of Cb channel
            Core.extractChannel(lowerCrop, lowerCrop, 2);
            Core.extractChannel(upperCrop, upperCrop, 2);

            // Store Averages
            Scalar lowerAveOrange = Core.mean(lowerCrop);
            Scalar upperAveOrange = Core.mean(upperCrop);
            double finalLowerAve = lowerAveOrange.val[0];
            double finalUpperAve = upperAveOrange.val[0];


            // Check 4 rings
            if (

                    finalUpperAve > DashConstants.orangeMin &&
                            finalUpperAve < DashConstants.orangeMax

            ) ringCount = 4.0;
                // Check 0 rings
            else if (

                    finalLowerAve > DashConstants.orangeMax ||
                            finalLowerAve < DashConstants.orangeMin

            ) ringCount = 0.0;
            else ringCount = 1.0;

            /**
             * RECT_BOTTOM_X1: 0.75
             * RECT_BOTTOM_X2: 0.9
             * RECT_BOTTOM_Y1: 0.38
             * RECT_BOTTOM_Y2: 0.42
             * RECT_TOP_X1: 0.75
             * RECT_TOP_X2: 0.9
             * RECT_TOP_Y1: 0.3
             * RECT_TOP_Y2: 0.38
             * Given a distance of around 3ft from rings
             */

            /*
            multTelemetry.addData("RECT_TOP_X1", DashConstants.rectTopX1Percent);
            multTelemetry.addData("RECT_TOP_Y1", DashConstants.rectTopY1Percent);
            multTelemetry.addData("RECT_TOP_X2", DashConstants.rectTopX2Percent);
            multTelemetry.addData("RECT_TOP_Y2", DashConstants.rectTopY2Percent);
            multTelemetry.addData("RECT_BOTTOM_X1", DashConstants.rectBottomX1Percent);
            multTelemetry.addData("RECT_BOTTOM_Y1", DashConstants.rectBottomY1Percent);
            multTelemetry.addData("RECT_BOTTOM_X2", DashConstants.rectBottomX2Percent);
            multTelemetry.addData("RECT_BOTTOM_Y2", DashConstants.rectBottomY2Percent);
            */

            multTelemetry.addData("Ring Count", ringCount);
            multTelemetry.addData("finalLowerAve: ", finalLowerAve);
            multTelemetry.addData("finalUpperAve: ", finalUpperAve);
            multTelemetry.update();

            // Return altered image
            return outPut;
        }

        @Override
        public void onViewportTapped()
        {
            viewportPaused = !viewportPaused;

            if(viewportPaused)
            {
                webcam.pauseViewport();
            }
            else
            {
                webcam.resumeViewport();
            }
        }
    }
}
