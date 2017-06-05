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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {

    private ImageAdapter mAdapter;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Set up gridvied
        GridView gridview = (GridView)view.findViewById(R.id.gridviewGallery);
        // Create image adapater
        mAdapter = new ImageAdapter(getContext());
        mAdapter.setDirectory(getString(R.string.gallery_dir));
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Get filepath
                String filepath = mAdapter.mFilePaths.get(position);

                // Start drawing activity
                Intent sharingIntent = new Intent(getActivity(), SharingActivity.class);
                Global.shareImage = BitmapFactory.decodeFile(filepath);
                startActivity(sharingIntent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setDirectory(getString(R.string.gallery_dir));
    }
}
