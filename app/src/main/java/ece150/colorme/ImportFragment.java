package ece150.colorme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Bitmap;

import java.io.IOException;
import java.io.FileNotFoundException;


public class ImportFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FROM_FILESYSTEM = 2;
    static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 3;

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
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Store image in MainActivity
            //((MainActivity)getActivity()).setNewImageBitmap(imageBitmap);

            // Start preview activity
            Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
            //previewIntent.putExtra("image", imageBitmap);
            Global.newImage = imageBitmap;
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
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
                    //imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

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
