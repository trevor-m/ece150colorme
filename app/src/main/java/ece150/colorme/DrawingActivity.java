package ece150.colorme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.graphics.Color;
import android.widget.Toast;
import android.media.ThumbnailUtils;

public class DrawingActivity extends AppCompatActivity implements OnClickListener {
    // Drawing field
    private DrawingView drawView;
    // Paint color buttons
    private ImageButton currPaint, drawBtn, finishBtn, saveBtn;
    // Various brush widths
    private float smallBrush, mediumBrush, largeBrush;
    // Edges (template)
    private Bitmap mEdges = null;

    private String paintingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        // Get drawing view
        drawView = (DrawingView)findViewById(R.id.drawing);
        drawView.mActivity = this;

        // Are we continuing a drawing?
        drawView.continueBitmap = Global.continueDrawing;

        paintingID = getIntent().getStringExtra("id");

        // Get input edges
        mEdges = makeTransparent(Global.newImage); //getIntent().getParcelableExtra("edges");

        // Set draw button listening
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        // Set erase button listening
        //eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        //eraseBtn.setOnClickListener(this);
        // Set save button listening
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        // Set finish button listening
        finishBtn = (ImageButton)findViewById(R.id.finish_btn);
        finishBtn.setOnClickListener(this);

        // Start with first paint (0)
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        // Select paint brush
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        // Get brush widths
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        // Set inital brush size
        drawView.setBrushSize(mediumBrush);
    }

    public static Bitmap makeTransparent(Bitmap bit) {
        int width =  bit.getWidth();
        int height = bit.getHeight();
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);

        for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++){
            if(allpixels[i] == Color.argb(255, 255, 255, 255))

                allpixels[i] = Color.alpha(Color.TRANSPARENT);
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }

    private boolean saveBitmap(Bitmap image, String filename, String folder) {
        try {
            // Make directory if it doesn't exist
            File dir = new File(getApplicationContext().getFilesDir(), folder);
            dir.mkdirs();
            // Create image file
            File filePath = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(filePath);

            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Drawing saved.", Toast.LENGTH_SHORT);
            savedToast.show();

            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    private void saveToInternalStorage() {
        // Get colored layer
        drawView.setDrawingCacheEnabled(true);
        Bitmap drawing = drawView.getDrawingCache();
        // Get edges layer
        Bitmap resizedEdges = ThumbnailUtils.extractThumbnail(mEdges, drawView.getWidth(), drawView.getHeight());
        // Combine with edges layers
        Bitmap combined = Bitmap.createBitmap(drawing.getWidth(), drawing.getHeight(), drawing.getConfig());
        Canvas canvas = new Canvas(combined);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(drawing, 0, 0, paint);
        canvas.drawBitmap(resizedEdges, 0, 0, paint);

        // Save all three layers
        saveBitmap(drawing, paintingID + "_draw.png", getString(R.string.inprogress_dir));
        saveBitmap(mEdges, paintingID + "_edge.png", getString(R.string.inprogress_dir));
        saveBitmap(combined, paintingID + ".png", getString(R.string.inprogress_dir));

        drawView.destroyDrawingCache();
    }

    public void setEdgeOverlay() {
        if(mEdges != null) {
            // Resize edges
            Bitmap resizedEdges = ThumbnailUtils.extractThumbnail(mEdges, drawView.getWidth(), drawView.getHeight());

            // Set ImageView
            ImageView edgeView = (ImageView) findViewById(R.id.edgeOverlay);
            //edgeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            edgeView.setImageBitmap(resizedEdges);
        }
    }

    public void paintClicked(View view){
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        // Check if user has clicked a different paint
        if(view != currPaint){
            // Get the color button
            ImageButton imgView = (ImageButton)view;
            // Extract the color string from the tag
            String color = view.getTag().toString();
            // Set current color to this color
            drawView.setColor(color);

            // Update UI to indicate which color is chosen
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.draw_btn){
            // Set up brush width selection dialog
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            // Display the dialog
            brushDialog.show();
        }
        else if(view.getId() == R.id.save_btn) {
            saveToInternalStorage();
        }
        else if(view.getId() == R.id.finish_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Finish drawing");
            saveDialog.setMessage("Finish drawing and save to the gallery? You won't be able to edit it anymore.");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    moveToGallery();
                    finish();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    public void moveToGallery() {
        // Get colored layer
        drawView.setDrawingCacheEnabled(true);
        Bitmap drawing = drawView.getDrawingCache();
        // Get edges layer
        Bitmap resizedEdges = ThumbnailUtils.extractThumbnail(mEdges, drawView.getWidth(), drawView.getHeight());
        // Combine with edges layers
        Bitmap combined = Bitmap.createBitmap(drawing.getWidth(), drawing.getHeight(), drawing.getConfig());
        Canvas canvas = new Canvas(combined);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(drawing, 0, 0, paint);
        canvas.drawBitmap(resizedEdges, 0, 0, paint);

        // Save combined to gallery
        saveBitmap(combined, paintingID + ".png", getString(R.string.gallery_dir));

        // Remove inprogress stuff
        // Make directory if it doesn't exist
        File dir = new File(getApplicationContext().getFilesDir(), getString(R.string.inprogress_dir));
        // Delete image file
        File filePath = new File(dir, paintingID + ".png");
        filePath.delete();
        filePath = new File(dir, paintingID + "_draw.png");
        filePath.delete();
        filePath = new File(dir, paintingID + "_edge.png");
        filePath.delete();

        drawView.destroyDrawingCache();
    }
}
