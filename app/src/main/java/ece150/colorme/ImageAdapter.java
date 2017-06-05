package ece150.colorme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Trevor on 6/4/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    // File paths to all images in grid
    public ArrayList<String> mFilePaths = new ArrayList<String>();
    public ArrayList<String> mFileNames = new ArrayList<String>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void setDirectory(String directory) {
        mFileNames.clear();
        mFilePaths.clear();

        File dir = new File(mContext.getFilesDir(), directory);
        File[] files = dir.listFiles();
        if(files != null) {
            for (final File f : files) {
                if(!f.getName().contains("_draw.png") && !f.getName().contains("_edge.png")) {
                    mFilePaths.add(f.getAbsolutePath());
                    mFileNames.add(f.getName());
                }
            }
        }
    }

    public int getCount() {
        return mFilePaths.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(mThumbIds[position]);
        Bitmap myBitmap = BitmapFactory.decodeFile(mFilePaths.get(position));
        imageView.setImageBitmap(myBitmap);
        return imageView;
    }
}