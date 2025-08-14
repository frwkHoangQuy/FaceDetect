package com.example.facedetect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.facedetect.UI.SCFaceGraphicOverlay;
import com.example.facedetect.databinding.ActivityMainBinding;
import com.example.facedetect.utils.YuvUtils;
import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding viewBinding;
    private SCFaceGraphicOverlay mFaceOverlay;
    private ExecutorService cameraExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraExecutor = Executors.newSingleThreadExecutor();
        mFaceOverlay = findViewById(R.id.scfgoFaceOverlay);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        checkAndRequestCameraPermission();
    }

    private void checkAndRequestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Camera permission granted");
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("CAMERA permission granted after request.");
                startCamera();
            } else {
                System.out.println("CAMERA permission denied.");
            }
        }
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        ResolutionSelector resolutionSelector = new ResolutionSelector.Builder()
                .setResolutionStrategy(
                        new ResolutionStrategy(
                                new Size(1280, 720),
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                        )
                )
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build();

        Preview preview = new Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(viewBinding.viewFinder.getDisplay().getRotation())
                .build();
        viewBinding.viewFinder.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
//        viewBinding.viewFinder.setScaleX(-1);
//        viewBinding.viewFinder.setScaleY(-1);
        preview.setSurfaceProvider(viewBinding.viewFinder.getSurfaceProvider());

        CameraSelector cameraSelector;
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);


        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);

    }
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {
//        try {
//            // 1️⃣ Lấy ảnh từ ImageProxy
//            Image image = imageProxy.getImage();
//            if (image == null) return;
//
//            // 2️⃣ Chuyển YUV → Bitmap RGB
//            Mat rgbBitmap = YuvUtils.yuvToRgbMat(image);
//
//            // 3️⃣ Xử lý orientation
//            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
//            Bitmap rotatedBitmap = rotateBitmap(rgbBitmap, rotationDegrees);
//
//            // 4️⃣ Crop theo vùng khung preview
//            Rect cropRect = getFaceBoxFrame(); // xác định vùng mà user cần đặt mặt vào
//            Bitmap croppedBitmap = Bitmap.createBitmap(
//                    rotatedBitmap,
//                    cropRect.left, cropRect.top,
//                    cropRect.width(), cropRect.height()
//            );
//
//            // 5️⃣ Detect tự động
//            if (faceDetectorReady) {
//                faceDetector.detect(croppedBitmap, result -> {
//                    if (result.success) {
//
//                    } else {
//                    }
//                });
//            }
//
//        } finally {
//            // 6️⃣ Giải phóng ImageProxy để camera tiếp tục stream
//            imageProxy.close();
//        }
    }
}