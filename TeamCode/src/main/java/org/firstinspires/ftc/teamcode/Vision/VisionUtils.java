package org.firstinspires.ftc.teamcode.Vision;

import org.opencv.core.MatOfPoint;
import org.openftc.easyopencv.OpenCvCamera;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;

public class VisionUtils {

    public static OpenCvCamera webcam;
    public static final double IMG_WIDTH = 320;
    public static final double IMG_HEIGHT = 240;
    public static final double FOV = 72;
    public static final double FOCAL_LENGTH = 1;
    public static final double RING_HEIGHT = 20;
    public static final double SENSOR_HEIGHT = 1;

    public static double getDistance2Object(double object_pixel_height, double object_height) {
        if (object_pixel_height == 0) return 0;
        return (FOCAL_LENGTH * object_height * IMG_HEIGHT) / (object_pixel_height * SENSOR_HEIGHT);
    }

    public static double pixels2Degrees(double pixels) {
        return pixels * (FOV / IMG_WIDTH);
    }

    public static int findWidestContourIndex(List<MatOfPoint> contours){
        int index = 0;
        double maxWidth = 0;
        for (int i=0; i < contours.size(); i++){
            MatOfPoint cnt = contours.get(i);
            double width = boundingRect(cnt).width;
            if (width > maxWidth) {
                maxWidth = width;
                index = i;
            }
        }
        return index;
    }

    public static List<MatOfPoint> findNWidestContours(int n, List<MatOfPoint> contours){
        List<MatOfPoint> widest_contours = new ArrayList<>();
        for (int j=0; j < n; j++){
            int largest_index = findWidestContourIndex(contours);
            widest_contours.add(contours.get(largest_index));

            contours.remove(largest_index);
            if (contours.size() == 0) break;
        }
        return widest_contours;
    }

    public static int findLargestContourIndex(List<MatOfPoint> contours) {
        int index = 0;
        double maxArea = 0;
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint cnt = contours.get(i);
            double area = contourArea(cnt);
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        return index;
    }

    public static List<MatOfPoint> findNLargestContours(int n, List<MatOfPoint> contours) {
        List<MatOfPoint> new_contours = new ArrayList<>();

        for (int j = 0; j < n; j++) {
            int largest_index = findLargestContourIndex(contours);
            new_contours.add(contours.get(largest_index));

            contours.remove(largest_index);
            if (contours.size() == 0) break;
        }
        return new_contours;
    }

}
