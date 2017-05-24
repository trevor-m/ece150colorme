package ece150.colorme;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.Utils;

public class PreviewActivity extends AppCompatActivity {

    private Bitmap mImage, mEdges;

    private Bitmap detectEdges(Bitmap bitmap) {
        // Convert to matrix
        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        // apply Canny edge detector
        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 80, 100);

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
        mImage = getIntent().getParcelableExtra("image");
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
                //

            }
        });
    }
}
