package com.protectsoft.camshare.PreviewImage;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.protectsoft.camshare.R;
import com.protectsoft.camshare.bucket.BucketFiles;

import java.io.File;
import java.util.ArrayList;
/**
 * Created by abraham on 19/9/2015.
 */
public class PreviewImageGridview extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.gridview);

        if(BucketFiles.getPictureFileSize() > 0) {
            ArrayList<PreviewImageModel> models = PreviewImageModel.fromFiles(BucketFiles.getPicturesFileList());
            PreviewImageAdapter adapter = new PreviewImageAdapter(this,models);
            final GridView gridView = (GridView)findViewById(R.id.gridview);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PreviewImageModel pm = (PreviewImageModel)gridView.getItemAtPosition(position);
                    new LoadBitmapPreview().execute(pm.getFile());
                }
            });
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(BucketFiles.getPictureFileSize() > 0) {
            ArrayList<PreviewImageModel> models = PreviewImageModel.fromFiles(BucketFiles.getPicturesFileList());
            PreviewImageAdapter adapter = new PreviewImageAdapter(this,models);
            final GridView gridView = (GridView)findViewById(R.id.gridview);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PreviewImageModel pm = (PreviewImageModel)gridView.getItemAtPosition(position);
                    new LoadBitmapPreview().execute(pm.getFile());
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //to do staff here
    }



    private class LoadBitmapPreview extends AsyncTask<File,Void,Void> {


        @Override
        protected Void doInBackground(File... params) {

            File f = params[0];
            if(BucketFiles.isFileExists(f)) {
                Intent intent = new Intent(PreviewImageGridview.this,PreviewImage.class);
                intent.putExtra("file",f);
                startActivity(intent);
            }

            return null;
        }
    }




}
