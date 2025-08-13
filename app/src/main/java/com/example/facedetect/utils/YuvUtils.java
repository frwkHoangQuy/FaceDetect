package com.example.facedetect.utils;

import android.media.Image;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

public class YuvUtils {

    /**
     * Chuyển ImageProxy (YUV_420_888) sang Mat RGB
     */
    @OptIn(markerClass = ExperimentalGetImage.class)
    public static Mat yuvToRgbMat(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) return null;

        byte[] nv21 = getNv21(image);

        // Tạo Mat từ NV21
        Mat yuvMat = new Mat(image.getHeight() + image.getHeight() / 2,
                image.getWidth(),
                CvType.CV_8UC1);
        yuvMat.put(0, 0, nv21);

        // Chuyển YUV → RGB
        Mat rgbMat = new Mat();
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21);

        return rgbMat;
    }

    private static byte[] getNv21(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        // Chuyển YUV_420_888 sang NV21
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);
        return nv21;
    }
}
