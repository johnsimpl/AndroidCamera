package com.protectsoft.camshare.PreviewImage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.protectsoft.camshare.R;
import com.protectsoft.camshare.bucket.BucketFiles;
import com.protectsoft.camshare.editimage.EditImage;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
/**
 * Created by abraham on 17/9/2015.
 */
public class PreviewImage extends Activity {

    private TouchImageView imageView;
    private File currentFile;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewimagelayout);

        Intent intent = getIntent();
        currentFile = (File)intent.getExtras().get("file");

        imageView = (TouchImageView)findViewById(R.id.imagepreview);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(850,1200)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);

        final GestureDetector gdt = new GestureDetector(new GestureListener());

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });

        bringlayoutsToFront();


    }


    @Override
    public void onResume() {
        super.onResume();
        if(currentFile.exists()) {
            imageView = (TouchImageView)findViewById(R.id.imagepreview);
            Picasso.with(getApplicationContext())
                    .load(currentFile)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageView);
            bringlayoutsToFront();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    public void action_crop(View view) {
        Intent intent = new Intent(this,CropActivity.class).setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.putExtra("file",currentFile);
        startActivity(intent);
    }



    public void bringlayoutsToFront() {
        //this linearlayout contains take picture and record video button...at tha time
        LinearLayout ll = (LinearLayout)findViewById(R.id.right_options);
        ll.bringToFront();

        //...contains option button
        LinearLayout l2 = (LinearLayout)findViewById(R.id.optionsbuttonlayout);
        l2.bringToFront();

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                File nextPicture = BucketFiles.getNextPictureFile(currentFile);
                if(nextPicture != null && BucketFiles.isFileExists(nextPicture)) {
                    currentFile = nextPicture;

                    Bitmap bitmap = BitmapFactory.decodeFile(nextPicture.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    try {

                        Picasso.with(getApplicationContext())
                                .load(nextPicture)
                                .resize(imageView.getRootView().getWidth(),imageView.getRootView().getHeight())
                                .centerInside()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(imageView);
                    } catch (OutOfMemoryError ooe) {
                        finish();
                    }
                } else {
                    //file deleted from user or something
                    BucketFiles.removeDeletedFiles();
                }

                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                File previusPicture = BucketFiles.getPreviusPictureFile(currentFile);
                if(previusPicture != null && BucketFiles.isFileExists(previusPicture)) {
                    currentFile = previusPicture;

                    Bitmap bitmap = BitmapFactory.decodeFile(previusPicture.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    try {
                        Picasso.with(getApplicationContext())
                                .load(previusPicture)
                                .resize(imageView.getRootView().getWidth(),imageView.getRootView().getHeight())
                                .centerInside()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(imageView);
                    } catch (OutOfMemoryError ooe) {

                    }

                } else {
                    BucketFiles.removeDeletedFiles();
                }
                return false; // Left to right
            }

            //not used yet up and down swipe
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }


    public void editimage(View view) {

        if(currentFile != null && currentFile.exists()) {
            Intent intent = new Intent(this, EditImage.class);
            intent.putExtra("file", currentFile);
            startActivity(intent);
            finish();
        }

    }





}
