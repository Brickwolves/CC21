package org.firstinspires.ftc.teamcode.Vision;

import android.os.Build;

import androidx.annotation.RequiresApi;

import static org.firstinspires.ftc.teamcode.Utilities.DashConstants.Dash_GoalFinder.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.abs;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_COMPLEX;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.putText;
import static org.opencv.imgproc.Imgproc.rectangle;


public class GoalFinderPipeline extends OpenCvPipeline
{
    private boolean viewportPaused;

    // Constants
    private int IMG_WIDTH = 0;
    private int IMG_HEIGHT = 0;
    private int FOV = 72;
    private double error = 0;

    // Init mats here so we don't repeat
    private Mat modified = new Mat();
    private Mat output = new Mat();

    // Thresholding values
    Scalar MIN_HSV, MAX_HSV;

    // Rectangle settings
    private Scalar color = new Scalar(255, 0, 255);
    private int thickness = 2;
    private int font = FONT_HERSHEY_COMPLEX;

    @Override
    public Mat processFrame(Mat input)
    {
        // Get image dimensions
        IMG_HEIGHT = input.rows();
        IMG_WIDTH = input.cols();

        // Copy to output
        input.copyTo(output);

        // Convert & Copy to outPut image
        cvtColor(input, modified, Imgproc.COLOR_RGB2HSV);

        // Blurring
        GaussianBlur(modified, modified, new Size(blur, blur), 0);

        // Thresholding
        MIN_HSV = new Scalar(MIN_H, MIN_S, MIN_V);
        MAX_HSV = new Scalar(MAX_H, MAX_S, MAX_V);
        inRange(modified, MIN_HSV, MAX_HSV, modified);

        // Erosion and Dilation
        erode(modified, modified, new Mat(erode_const, erode_const, CV_8U));
        dilate(modified, modified, new Mat(dilate_const, dilate_const, CV_8U));

        // Find contours of goal
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        findContours(modified, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        if (contours.size() == 0) return output;

        // Retrieve goal contours
        List<MatOfPoint> new_contours = findNLargestContours(2, contours);

        // Get and draw bounding rectangles
        Rect goalRect = getGoalRect(new_contours);
        rectangle(output, goalRect, color, thickness);
        for (MatOfPoint cnt : new_contours){
            Rect rect = boundingRect(cnt);
            rectangle(output, rect, color, thickness);
        }

        // Calculate error
        int center_x = goalRect.x + (goalRect.width / 2);
        int center_y = goalRect.y + (goalRect.height / 2);
        Point center = new Point(center_x, center_y);
        int pixel_error = (IMG_WIDTH / 2) - center_x;
        error = pixels2Degrees(pixel_error);
        line(output, center, new Point(center_x + pixel_error, center_y), new Scalar(0, 0, 255), thickness);

        // Log center
        String coords = "(" + center_x + ", " + center_y + ")";
        putText(output, coords, center, font, 0.5, color);

        // Return altered image
        return output;

    }

    public double pixels2Degrees(double pixels){
        if (IMG_WIDTH == 0) return 0;
        return pixels * (FOV / IMG_WIDTH);
    }

    private Rect getGoalRect(List<MatOfPoint> contours){

        // Return first contour if there is only one
        Rect goalRect = boundingRect(contours.get(0));

        // Extrapolate overarching rectangle if there are two
        if (contours.size() == 2){

            // Init coords of both rectangles
            Rect left = new Rect(0, 0, 0, 0);
            Rect right = new Rect(0, 0, 0, 0);

            // Get bounding rects of second rectangle
            Rect secondRect = boundingRect(contours.get(1));

            // Check second rect is within goal width
            int diff = abs(goalRect.x - secondRect.x);
            if (diff > goalWidth) return goalRect;

            // Check which side rectangles are on, and calculate surrounding box
            if (goalRect.x < secondRect.x){
                left.x = goalRect.x;
                left.y = goalRect.y;
                right.x = secondRect.x;
                right.y = secondRect.y;
                right.width = secondRect.width;
                right.height = secondRect.height;
            }
            else {
                left.x = secondRect.x;
                left.y = secondRect.y;
                right.x = goalRect.x;
                right.y = goalRect.y;
                right.width = goalRect.width;
                right.height = goalRect.height;
            }
            goalRect.x = left.x;
            goalRect.y = left.y;
            goalRect.width = abs(right.x - left.x) + right.width;
            goalRect.height = abs(right.y - left.y) + right.height;
        }

        return goalRect;
    }

    public int findLargestContourIndex(List<MatOfPoint> contours){
        int index = 0;
        double maxArea = 0;
        for (int i=0; i < contours.size(); i++){
            MatOfPoint cnt = contours.get(i);
            double area = contourArea(cnt);
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        return index;
    }

    public List<MatOfPoint> findNLargestContours(int n, List<MatOfPoint> contours){
        List<MatOfPoint> new_contours = new ArrayList<>();

        for (int j=0; j < n; j++){
            int largest_index = findLargestContourIndex(contours);
            new_contours.add(contours.get(largest_index));

            contours.remove(largest_index);
            if (contours.size() == 0) break;
        }
        return new_contours;
    }

    @Override
    public void onViewportTapped()
    {
        /*
        viewportPaused = !viewportPaused;
        if(viewportPaused)  webcam.pauseViewport();
        else                webcam.resumeViewport();
         */
    }
}