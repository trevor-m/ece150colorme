package ece150.colorme;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.UUID;

import javax.microedition.khronos.opengles.GL;


public class HomeFragment extends Fragment {

    private  ImageAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up gridvied
        GridView gridview = (GridView)view.findViewById(R.id.gridviewHome);
        // Create image adapater
        mAdapter = new ImageAdapter(getContext());
        mAdapter.setDirectory(getString(R.string.inprogress_dir));
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Click on item
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Get filepath
                String filepath = mAdapter.mFilePaths.get(position);
                String drawing = filepath.replace(".png", "_draw.png");
                String edges = filepath.replace(".png", "_edge.png");
                // Get id
                String filename = mAdapter.mFileNames.get(position);
                String uuid = filename.replace(".png", "");

                // Start drawing activity
                Intent drawingIntent = new Intent(getActivity(), DrawingActivity.class);
                Global.newImage = BitmapFactory.decodeFile(edges);
                Global.continueDrawing = BitmapFactory.decodeFile(drawing);
                drawingIntent.putExtra("id", uuid);
                startActivity(drawingIntent);

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setDirectory(getString(R.string.inprogress_dir));
    }
}
