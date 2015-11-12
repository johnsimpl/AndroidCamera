package com.protectsoft.camshare.PreviewImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.protectsoft.camshare.R;
import com.protectsoft.camshare.Utils.BitmapUtils;
import com.protectsoft.camshare.bucket.BucketFiles;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
/**
 * Created by abraham on 19/9/2015.
 */
public class PreviewImageAdapter extends ArrayAdapter<PreviewImageModel> {

    public PreviewImageAdapter(Context context,ArrayList<PreviewImageModel> models) {
        super(context, 0, models);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        PreviewImageModel model = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridviewlist,parent,false);
        }

        ImageView img = (ImageView)convertView.findViewById(R.id.imageviewitem);

        //picasso library
        Picasso.with(getContext())
                .load(model.getFile())
                .resize(100,100)
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(img);

        //old way of loading images from files to imageview.
        //is the inner class below
        //i replace this way with the Picasso library way above
        //new BitmapLoad(img).execute(model.getFile());
        return convertView;
    }


    //unused old pure java way
    private class BitmapLoad extends AsyncTask<File,Void,Bitmap> {

        private ImageView img;

        public BitmapLoad(ImageView iv) {
            img = iv;
        }


        @Override
        protected Bitmap doInBackground(File... params) {

            File file = params[0];
            Bitmap bitmap = null;

            if(BucketFiles.isFileExists(file)) {
                bitmap = BitmapUtils.decodeSampledBitmapFromFile(file, 80, 80);

            }

            return bitmap;
        }


        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                img.setImageBitmap(result);
            }
        }


    }



}
