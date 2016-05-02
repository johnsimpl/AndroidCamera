package com.protectsoft.simplecam.PreviewImage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.protectsoft.simplecam.R;
import com.protectsoft.simplecam.bucket.BucketFiles;
import com.protectsoft.simplecam.editimage.ColorFilters;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by abraham on 19/9/2015.
 */
public class PreviewImageGridview extends Activity {


    private GridView gridView;
    private ItemClickListenerDefault itemClickListener;
    public static List<File> choosenFiles;

    private LinearLayout linearLayout;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.previewimage_gridview);

        if(BucketFiles.getPictureFileSize() > 0) {

            itemClickListener = new ItemClickListenerDefault();

            ArrayList<PreviewImageModel> models = PreviewImageModel.fromFiles(BucketFiles.getPicturesFileList());
            PreviewImageAdapter adapter = new PreviewImageAdapter(this, models);
            gridView = (GridView) findViewById(R.id.gridview);
            gridView.setAdapter(adapter);

            gridView.setOnItemLongClickListener(new ItemLongClickListener());
            gridView.setOnItemClickListener(itemClickListener);

        }


    }


    private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            choosenFiles = new ArrayList<File>();

            gridView.setOnItemClickListener(new ItemClickListenerChooseForDelete());
            gridView.setOnItemLongClickListener(null);

            linearLayout = (LinearLayout)findViewById(R.id.deletemenu);
            linearLayout.setVisibility(View.VISIBLE);

            PreviewImageModel pm = (PreviewImageModel) gridView.getItemAtPosition(position);

            try {
                final ImageView imageView = (ImageView) view.findViewById(R.id.imageviewitem);
                if(choosenFiles.contains(pm.getFile())) {

                    Picasso.with(getApplicationContext())
                            .load(pm.getFile())
                            .resize(100,100)
                            .centerCrop()
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageView.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                    choosenFiles.remove(pm.getFile());
                } else {

                    choosenFiles.add(pm.getFile());
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ColorFilters.doColorFilterRedDark(imageView, bitmap);

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return true;
        }
    }

    private class ItemClickListenerChooseForDelete implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            PreviewImageModel pm = (PreviewImageModel) gridView.getItemAtPosition(position);

            try {
                ImageView imageView = (ImageView) view.findViewById(R.id.imageviewitem);

                if(choosenFiles.contains(pm.getFile())) {

                    Picasso.with(getApplicationContext())
                            .load(pm.getFile())
                            .resize(100,100)
                            .centerCrop()
                            .into(imageView);
                    choosenFiles.remove(pm.getFile());

                } else {

                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ColorFilters.doColorFilterRedDark(imageView, bitmap);
                    choosenFiles.add(pm.getFile());

                }

            } catch (Exception ex) {

            }
        }
    }


    private class ItemClickListenerDefault implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            gridView.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setEnabled(true);
                        }
                    });
                }
            }, 1500);

            PreviewImageModel pm = (PreviewImageModel) gridView.getItemAtPosition(position);
            new LoadBitmapPreview().execute(pm.getFile());

        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        if(BucketFiles.getPictureFileSize() > 0) {

            BucketFiles.initializeAllPicturesToBucket();

            choosenFiles = new ArrayList<File>();
            itemClickListener = new ItemClickListenerDefault();

            ArrayList<PreviewImageModel> models = PreviewImageModel.fromFiles(BucketFiles.getPicturesFileList());
            PreviewImageAdapter adapter = new PreviewImageAdapter(this,models);
            final GridView gridView = (GridView)findViewById(R.id.gridview);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(itemClickListener);
            gridView.setOnItemLongClickListener(new ItemLongClickListener());

        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        choosenFiles = new ArrayList<File>();
        //to do staff here
    }


    public void cancelDelete(View view) {

        choosenFiles = new ArrayList<>();

        linearLayout = (LinearLayout)findViewById(R.id.deletemenu);
        linearLayout.setVisibility(View.GONE);

        itemClickListener = new ItemClickListenerDefault();

        gridView.setOnItemClickListener(itemClickListener);
        gridView.setOnItemLongClickListener(new ItemLongClickListener());

    }



    public void deleteFiles(View view) {


        if(choosenFiles.isEmpty()) {
            cancelDelete(view);
            return;
        }

        final Button button = (Button)findViewById(R.id.deletebutton);
        button.setEnabled(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(true);
                    }
                });
            }
        }, 1000);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setInverseBackgroundForced(true);
        builder.setTitle("Delete " + choosenFiles.size() + " pictures?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(PreviewImageGridview.this);

                progressDialog.setTitle("Deleting pictures..");
                progressDialog.setMessage("deleting...");
                progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
                progressDialog.setProgress(0);
                progressDialog.setMax(choosenFiles.size());
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < choosenFiles.size();) {

                            try {
                                Thread.sleep(150);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Context context = getApplicationContext();

                            BucketFiles.deleteFileFromFileSystem(choosenFiles.get(i), context);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.incrementProgressBy(1);
                                }
                            });

                            i++;
                            if (progressDialog.getProgress() == progressDialog.getMax() || i == choosenFiles.size()) {
                                progressDialog.dismiss();
                                break;
                            }

                        }

                        choosenFiles = new ArrayList<File>();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                linearLayout = (LinearLayout) findViewById(R.id.deletemenu);
                                linearLayout.setVisibility(View.GONE);

                                setContentView(R.layout.previewimage_gridview);
                                if (BucketFiles.getPictureFileSize() > 0) {
                                    itemClickListener = new ItemClickListenerDefault();

                                    ArrayList<PreviewImageModel> models = PreviewImageModel.fromFiles(BucketFiles.getPicturesFileList());
                                    PreviewImageAdapter adapter = new PreviewImageAdapter(PreviewImageGridview.this, models);
                                    gridView = (GridView) findViewById(R.id.gridview);
                                    gridView.setAdapter(adapter);


                                    gridView.setOnItemLongClickListener(new ItemLongClickListener());
                                    gridView.setOnItemClickListener(itemClickListener);

                                } else {
                                    finish();
                                }
                            }
                        });

                    }
                }).start();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }


    private class LoadBitmapPreview extends AsyncTask<File,Void,Void> {


        @Override
        protected Void doInBackground(File... params) {

            try {

                File f = params[0];
                if (BucketFiles.isFileExists(f)) {
                    Intent intent = new Intent(PreviewImageGridview.this, PreviewImage.class);
                    intent.putExtra("file", f);
                    startActivity(intent);
                }

                //once cached a null pointer exception from startactivity couldent reproduce!
            } catch (NullPointerException ex) {
                finish();
            }

            return null;
        }
    }






}
