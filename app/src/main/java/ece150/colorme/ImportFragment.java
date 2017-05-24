package ece150.colorme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Bitmap;


public class ImportFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

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
            // get new image from filesystem
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == MainActivity.RESULT_OK) {
            // Get the image that the camera took
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Store image in MainActivity
            //((MainActivity)getActivity()).setNewImageBitmap(imageBitmap);

            // Start preview activity
            Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
            previewIntent.putExtra("image", imageBitmap);
            startActivity(previewIntent);
        }
    }
}
