package com.protectsoft.camshare.PreviewImage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.View;

import com.isseiaoki.simplecropview.CropImageView;
import com.protectsoft.camshare.Constants;
import com.protectsoft.camshare.R;
import com.protectsoft.camshare.Utils.MediaFileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * Created by abraham on 5/10/2015.
 * @see //https://android-arsenal.com/details/1/2366
 */
public class CropActivity extends Activity {

    private File currentFile;
    private CropImageView cropImageView;
    private Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.croplayout);

        Intent intent = getIntent();
        currentFile = (File)intent.getExtras().get("file");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        currentBitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath(),options);
        cropImageView = (CropImageView)findViewById(R.id.cropImageView);
        cropImageView.setHandleColor(getResources().getColor(R.color.material_deep_teal_200));

        cropImageView.setImageBitmap(currentBitmap);

    }


    public void crop(View view) {

        Bitmap bitmap = cropImageView.getCroppedBitmap();
        File file = MediaFileUtils.getOutputFileForS3Download(currentFile.getName());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.jpegquality, stream);
        byte[] bitmapdata = stream.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, null);
        finish();

    }


    public void rotateLeft(View view) {
        cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D);
    }


    public void rotateRight(View view) {
        cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
    }




}
