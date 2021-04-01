package org.firstinspires.ftc.teamcode.Autonomous;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Utilities.Utils;
import org.firstinspires.ftc.teamcode.Vision.VisionUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import static org.opencv.core.Core.rotate;

@Autonomous(name="Vision", group="Autonomous Linear Opmode")
public class Vision extends LinearOpMode
{

    public void initialize(){
        Utils.setOpMode(this);
        setUpWebcam();
    }

    public void setUpWebcam(){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VisionUtils.webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"), cameraMonitorViewId);
        VisionUtils.webcam.setPipeline(new Pipeline());
        VisionUtils.webcam.openCameraDeviceAsync(() -> VisionUtils.webcam.startStreaming((int) VisionUtils.IMG_WIDTH, (int) VisionUtils.IMG_HEIGHT, OpenCvCameraRotation.UPRIGHT));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runOpMode()
    {

        initialize();

        Utils.multTelemetry.addLine("Waiting for start");
        Utils.multTelemetry.update();
        waitForStart();


        /*
        ACTION
         */
        if (opModeIsActive()){

            Utils.multTelemetry.addData("FPS", String.format("%.2f", VisionUtils.webcam.getFps()));
            Utils.multTelemetry.update();
            VisionUtils.webcam.stopStreaming();
        }
    }

    class Pipeline extends OpenCvPipeline
    {
        boolean viewportPaused;

        @Override
        public Mat processFrame(Mat input)
        {
            rotate(input, input, Core.ROTATE_90_CLOCKWISE);
            return input;
        }

        @Override
        public void onViewportTapped()
        {
            viewportPaused = !viewportPaused;
            if (viewportPaused) VisionUtils.webcam.pauseViewport();
            else VisionUtils.webcam.resumeViewport();
        }
    }
}
