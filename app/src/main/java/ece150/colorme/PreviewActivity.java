package ece150.colorme;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.Utils;
import org.opencv.core.CvType.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PreviewActivity extends AppCompatActivity {

    private Bitmap mImage, mEdges;

    private Bitmap detectEdges(Bitmap bitmap) {
        // Convert to matrix
        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        // Convert to grayscale
        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);

        // Determine threshold values
        MatOfDouble mu = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(edges, mu, stddev);
        double threshold1 = mu.get(0, 0)[0];
        double threshold2 = stddev.get(0, 0)[0];

        // apply Canny edge detector
        Imgproc.Canny(edges, edges, threshold1, threshold2);

        // Dilate to increase size of edges
        Imgproc.dilate(edges, edges, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6)));

        // invert colors
        Core.bitwise_not(edges, edges);


        // Convert to bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, resultBitmap);
        return resultBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Get input image
        mImage = Global.newImage; //getIntent().getParcelableExtra("image");
        // Find edges
        mEdges = detectEdges(mImage);

        // Display
        ImageView previewImage = (ImageView)findViewById(R.id.imagePreview);
        previewImage.setImageBitmap(mEdges);

        // Set button callbacks
        findViewById(R.id.button_preview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to import page
                finish();
            }
        });
        findViewById(R.id.button_preview_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // End this stack
                finish();

                // Start drawing activity
                Intent drawingIntent = new Intent(PreviewActivity.this, DrawingActivity.class);
                Global.newImage = mEdges;
                Global.continueDrawing = null;
                drawingIntent.putExtra("id", UUID.randomUUID().toString());
                //drawingIntent.putExtra("edges", mEdges);
                startActivity(drawingIntent);
            }
        });
    }
}
