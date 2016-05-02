package com.protectsoft.simplecam.PreviewImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.protectsoft.simplecam.R;
import com.protectsoft.simplecam.editimage.ColorFilters;
import com.squareup.picasso.Picasso;

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
        final PreviewImageModel model = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.previewimage_gridviewlist,parent,false);
        }

        final ImageView img = (ImageView)convertView.findViewById(R.id.imageviewitem);

        //picasso library
        Picasso.with(getContext())
                .load(model.getFile())
                .resize(100,100)
                .centerCrop()
                .into(img);

        try {
            if(PreviewImageGridview.choosenFiles != null && !PreviewImageGridview.choosenFiles.isEmpty()) {
                if (PreviewImageGridview.choosenFiles.contains(model.getFile())) {
                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                    ColorFilters.doColorFilterRedDark(img, bitmap);
                }
            }
        } catch (NullPointerException npe) {

        }


        return convertView;
    }






}
