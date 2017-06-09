package ece150.colorme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Date;
import java.util.UUID;


public class ImportFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FROM_FILESYSTEM = 2;
    static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 3;
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = UUID.randomUUID().toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.wtf("error3", "could not create camera photo");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "ece150.colorme.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public ImportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        // Create button listeners
        Button cameraButton = (Button)view.findViewById(R.id.button_camera_import);
        cameraButton.setOnClickListener(this);
        Button galleryButton = (Button)view.findViewById(R.id.button_file_import);
        galleryButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_camera_import) {
            // get new image using camera
            //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            dispatchTakePictureIntent();

            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
           // startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
        else if(v.getId() == R.id.button_file_import) {
            // Request permission to read files
            // Ask for permissions
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
            } else {
                getImageFromFilesystem();
            }
        }
    }

    public void getImageFromFilesystem() {
        // get new image from filesystem
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        /*Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});*/

        startActivityForResult(getIntent, REQUEST_IMAGE_FROM_FILESYSTEM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == MainActivity.RESULT_OK) {
            // Get the image that the camera took
            Bitmap cameraBitmap = null;
            File imgFile = new File(mCurrentPhotoPath);
            // Full resolution
            if(imgFile.exists()){
                cameraBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else {
                // Thumbnail
                Bundle extras = data.getExtras();
                cameraBitmap = (Bitmap) extras.get("data");
            }
            //rotate 90 to right
            /*Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap imageBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0,
                    cameraBitmap.getWidth(), cameraBitmap.getHeight(),
                    matrix, true);*/

            // Start preview activity
            Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
            Global.newImage = cameraBitmap;
            startActivity(previewIntent);
        }
        else if (requestCode == REQUEST_IMAGE_FROM_FILESYSTEM && resultCode == MainActivity.RESULT_OK) {
            // Get image that the user picked
            Bitmap imageBitmap = null;
            Uri uri = data.getData();//<- get Uri here from data intent
            if(uri !=null) {
                try {
                    imageBitmap = android.provider.MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (FileNotFoundException e) {
                    Log.wtf("error", e.getCause());
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    Log.wtf("error2", e.getCause());
                    throw new RuntimeException(e);
                }
            }

            // Start preview activity
            Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
            Global.newImage = imageBitmap;
            //previewIntent.putExtra("image", imageBitmap);
            startActivity(previewIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromFilesystem();
                }
                break;

            default:
                break;
        }
    }
}
