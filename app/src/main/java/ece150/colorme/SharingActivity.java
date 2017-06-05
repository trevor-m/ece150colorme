package ece150.colorme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.UUID;

public class SharingActivity extends AppCompatActivity implements View.OnClickListener {

    private Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        ImageView drawnImage = (ImageView)findViewById(R.id.DrawnImage);
        mImage = Global.shareImage;
        drawnImage.setImageBitmap(mImage);

        // Set save button listening
        Button saveBtn = (Button)findViewById(R.id.SaveGallery);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.SaveGallery) {
            saveToGallery();
            finish();
        }
    }

    void saveToGallery() {
        // Save
        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), mImage,
                UUID.randomUUID().toString()+".png", "drawing");
        // Was the save succesful?
        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Image could not be saved.", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }
    }
}
