package ece150.colorme;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PreviewActivity extends AppCompatActivity {

    private Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Get input image
        mImage = getIntent().getParcelableExtra("image");

        // Display
        ImageView previewImage = (ImageView)findViewById(R.id.imagePreview);
        previewImage.setImageBitmap(mImage);

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
