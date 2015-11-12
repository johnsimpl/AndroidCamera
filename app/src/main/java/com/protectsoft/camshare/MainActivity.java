package com.protectsoft.camshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.protectsoft.camshare.PreviewImage.PreviewImageGridview;
import com.protectsoft.camshare.Utils.BitmapUtils;
import com.protectsoft.camshare.Utils.MediaFileUtils;
import com.protectsoft.camshare.Utils.OrientationUtils;
import com.protectsoft.camshare.bucket.BucketFiles;
import com.protectsoft.camshare.drawer.NavDrawerItem;
import com.protectsoft.camshare.drawer.NavDrawerListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private Thread takePictureThread;

    //--boolean--------------------------
    public static boolean isRecording = false;
    public static boolean frontcameraswitch;
    public static boolean backcameraswitch;
    //-----------------------------------

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;
    private Camera.PictureCallback picture;
    private int mOrientation = -1;

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;
    private TypedArray navMenuIcons;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    //TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private OrientationEventListener orientationEventListener;



    //init create the proper folders if not exists at app startup
    {
        MediaFileUtils.initializeFolder();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        camera = getCameraInstance();

        if(frontcameraswitch == true || backcameraswitch == true) {
            CameraFeatures.params = camera.getParameters();
            frontcameraswitch = false;
            backcameraswitch = false;
        } else {
            //get/set camera parameters
            if (CameraFeatures.params == null) {
                CameraFeatures.params = camera.getParameters();
            } else {
                try {

                    // Nexus 5 is giving preview which is too dark without this
                    if (Build.MODEL.contains("Nexus 5"))
                    {
                        CameraFeatures.params.setPreviewFpsRange(7000, 30000);
                    }

                    camera.setParameters(CameraFeatures.params);
                } catch (Exception ex) {

                }
            }
        }
        //set device hardware support
        if(camera != null) {
            PackageManager pm = getPackageManager();
            CameraFeatures.setDevicehardwareSupport(CameraFeatures.params,pm);
        }

        // Create our Preview view and set it as the content of our activity
        cameraPreview = new CameraPreview(this,camera);

        FrameLayout preview = (FrameLayout)findViewById(R.id.camerapreview);

        preview.addView(cameraPreview);

        bringlayoutsToFront();
        picturecallbackSetup();


        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                //Log.v("MainActivity","orientation changed to: "+orientation);
                mOrientation = orientation;

                //for Debug only
                //--
                TextView text = (TextView)findViewById(R.id.txt);
                TextView text2 = (TextView)findViewById(R.id.txt2);

                Display dis = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                String rot = String.valueOf(dis.getRotation());
                String rot2 = String.valueOf(orientation);
                text.setText(rot);
                text2.setText(rot2);
                //--
            }
        };

        if(orientationEventListener.canDetectOrientation() == true) {
            Log.v("MainActivity","Can detect Orientation ");
            orientationEventListener.enable();
        } else {
            Log.v("MainActivity", "Cannot detect Orientation ");
            orientationEventListener.disable();
        }

        CameraFeatures.isTakingpicture = false;

        ImageView img = (ImageView)findViewById(R.id.imageview);
        if(Constants.bitmap != null) {
            BitmapDrawable bitmap = new BitmapDrawable(Constants.bitmap);
            img.setImageDrawable(bitmap);
            img.refreshDrawableState();
            Constants.bitmap = null;
        } else {
            img.setEnabled(false);
        }


        setUpDefaultDrawer();

    }//>-------------------------END onCreate-------------------




    //camera settings is default drawer
    private void setUpDefaultDrawer() {
        //set up drawer menu
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = new String[6];
        navMenuTitles[0] = "Resolutions";
        navMenuTitles[1] = "Effects";
        navMenuTitles[2] = "Scenes";
        navMenuTitles[3] = " ";

        // nav drawer icons from resources
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0],navMenuIcons.getResourceId(0,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],navMenuIcons.getResourceId(1,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));

        View header = getLayoutInflater().inflate(R.layout.drawerheader,null);

        ImageView imageView = (ImageView)header.findViewById(R.id.profile_image);
        int resId = getResources().getIdentifier("ic_menu_camera","drawable",getPackageName());
        imageView.setImageResource(resId);

        TextView username = (TextView)header.findViewById(R.id.lowtext);
        username.setText("settings");
        TextView email = (TextView)header.findViewById(R.id.uppertext);
        email.setText("Camera");

        mDrawerList.addHeaderView(header);
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        )

        {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                System.out.println("-------------onDrawerClosed---------------");
                setUpDefaultDrawerAdapter();
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                System.out.println("-------------onDrawerOpened---------------");
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new DrawerDefaultItemClicklistener());


    }


    public void setUpDefaultDrawerAdapter() {

        //set up drawer menu
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = new String[6];
        navMenuTitles[0] = "Resolutions";
        navMenuTitles[1] = "Effects";
        navMenuTitles[2] = "Scenes";
        navMenuTitles[3] = " ";

        // nav drawer icons from resources
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        )
        {
            public void onDrawerClosed(View view) {
            getActionBar().setTitle(mTitle);
            // calling onPrepareOptionsMenu() to show action bar icons
            System.out.println("-------------onDrawerClosed---------------");
            setUpDefaultDrawerAdapter();
            invalidateOptionsMenu();
        }

        public void onDrawerOpened(View drawerView) {
            getActionBar().setTitle(mDrawerTitle);
            // calling onPrepareOptionsMenu() to hide action bar icons
            System.out.println("-------------onDrawerOpened---------------");
            invalidateOptionsMenu();
        }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new DrawerDefaultItemClicklistener());

    }


    private class DrawerResolutionClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int pos = --position;
            if(pos >= 0 && pos <= navDrawerItems.size()) {
                final CharSequence[] camerasizes = CameraFeatures.getSupportedPictureSizes();
                if(pos >= 0 && pos < camerasizes.length) {

                    System.out.println(camerasizes[pos].toString());
                    String[] camsizes = camerasizes[pos].toString().split("x");
                    int w = Integer.valueOf(camsizes[0]);
                    int h = Integer.valueOf(camsizes[1]);

                    CameraFeatures.params.setPictureSize(w, h);
                    camera.setParameters(CameraFeatures.params);

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    setUpDefaultDrawerAdapter();
                }
            }
        }
    }

    private class DrawerEffectsClicklistener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int pos = --position;
            if(pos >= 0 && pos <= navDrawerItems.size()) {

                final CharSequence effects[] = CameraFeatures.getSupportedCameraEffects();

                if(pos >= 0 && pos < effects.length) {

                    System.out.println(effects[pos].toString());

                    if (effects[pos].equals("aqua")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_AQUA);
                        }
                    } else if (effects[pos].equals("blackboard")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_BLACKBOARD);
                        }
                    } else if (effects[pos].equals("negative")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_NEGATIVE);
                        }
                    } else if (effects[pos].equals("mono")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_MONO);
                        }
                    } else if (effects[pos].equals("none")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_NONE);
                        }
                    } else if (effects[pos].equals("posterize")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_POSTERIZE);
                        }
                    } else if (effects[pos].equals("sepia")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_SEPIA);
                        }
                    } else if (effects[pos].equals("solarize")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_SOLARIZE);
                        }
                    } else if (effects[pos].equals("whiteboard")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraColorEffectsChange().execute(Camera.Parameters.EFFECT_WHITEBOARD);
                        }
                    }


                }
            }
        }

    }


    private class DrawerScenesItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int pos = --position;
            if(pos >= 0 && pos <= navDrawerItems.size()) {

                final CharSequence scenes[] = CameraFeatures.getSupportedCameraScenes();
                if(pos >= 0 && pos < scenes.length) {

                    System.out.println(scenes[pos].toString());

                    if (scenes[pos].equals("action")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_ACTION);
                            //old way
                            //CameraFeatures.params.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
                            //startActivity(new Intent(MainActivity.this, MainActivity.class));
                            //finish();
                        }
                    } else if (scenes[pos].equals("auto")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_AUTO);
                        }
                    } else if (scenes[pos].equals("beach")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_BEACH);
                        }
                    } else if (scenes[pos].equals("candlelight")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_CANDLELIGHT);
                        }
                    } else if (scenes[pos].equals("fireworks")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_FIREWORKS);
                        }
                    } else if (scenes[pos].equals("HDR")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_HDR);
                        }
                    } else if (scenes[pos].equals("landscape")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_LANDSCAPE);
                        }
                    } else if (scenes[pos].equals("night")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_NIGHT);
                        }
                    } else if (scenes[pos].equals("night portrait")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT);
                        }
                    } else if (scenes[pos].equals("party")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_PARTY);
                        }
                    } else if (scenes[pos].equals("portrait")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_PORTRAIT);
                        }
                    } else if (scenes[pos].equals("snow")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_SNOW);
                        }
                    } else if (scenes[pos].equals("sports")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_SPORTS);
                        }
                    } else if (scenes[pos].equals("steady photo")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
                        }
                    } else if (scenes[pos].equals("sunset")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_SUNSET);
                        }
                    } else if (scenes[pos].equals("theatre")) {
                        releaseCamera();
                        if (camera == null) {
                            new CameraSceneChange().execute(Camera.Parameters.SCENE_MODE_THEATRE);
                        }
                    }


                }

            }

        }
    }

    private class DrawerFlashModesClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int pos = --position;
            if(pos >= 0 && pos <= navDrawerItems.size()) {

                final CharSequence flashmodes[] = CameraFeatures.getSupportedFlashoptions();
                if (pos >= 0 && pos < flashmodes.length) {

                    if (flashmodes[pos].equals("flash auto")) {
                        if (CameraFeatures.hasFlash) {
                            if (camera != null) {
                                CameraFeatures.params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                                camera.setParameters(CameraFeatures.params);
                            }
                        }
                    } else if (flashmodes[pos].equals("flash off")) {
                        if (CameraFeatures.hasFlash) {
                            if (camera != null) {
                                CameraFeatures.params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(CameraFeatures.params);
                            }
                        }
                    } else if (flashmodes[pos].equals("flash on")) {
                        if (CameraFeatures.hasFlash) {
                            if (camera != null) {
                                CameraFeatures.params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                                camera.setParameters(CameraFeatures.params);
                            }
                        }
                    } else if (flashmodes[pos].equals("flash red-eye reduction")) {
                        if (CameraFeatures.hasFlash) {
                            if (camera != null) {
                                CameraFeatures.params.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
                                camera.setParameters(CameraFeatures.params);
                            }
                        }
                    } else if (flashmodes[pos].equals("flashlight")) {
                        if (CameraFeatures.hasFlash) {
                            if (camera != null) {
                                CameraFeatures.params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(CameraFeatures.params);
                            }
                        }
                    }

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    setUpDefaultDrawerAdapter();
                }
            }

        }

    }

    private class DrawerDefaultItemClicklistener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(!isRecording) {

                int pos = --position;
                if (pos >= 0 && pos <= navDrawerItems.size()) {
                    if (pos == 0) {

                        final CharSequence[] camerasizes = CameraFeatures.getSupportedPictureSizes();
                        if (camerasizes != null) {

                            // load slide menu items
                            navMenuTitles = new String[camerasizes.length];

                            for (int i = 0; i < navMenuTitles.length; i++) {
                                navMenuTitles[i] = camerasizes[i].toString();
                            }

                            navDrawerItems = new ArrayList<NavDrawerItem>();

                            for (int i = 0; i < navMenuTitles.length; i++) {
                                String[] camsizes = navMenuTitles[i].toString().split("x");
                                int w = Integer.valueOf(camsizes[0]);
                                int h = Integer.valueOf(camsizes[1]);
                                String sizeInMp = CameraFeatures.getMegaPixels(w, h);
                                navDrawerItems.add(new NavDrawerItem(sizeInMp + "  " + navMenuTitles[i], navMenuIcons.getResourceId(0, -1)));
                            }

                            adapter = new NavDrawerListAdapter(getApplicationContext(),
                                    navDrawerItems);
                            mDrawerList.setAdapter(adapter);

                            mDrawerList.setOnItemClickListener(new DrawerResolutionClickListener());


                        }

                    } else if (pos == 1) {

                        final CharSequence effects[] = CameraFeatures.getSupportedCameraEffects();
                        if (effects != null) {

                            navMenuTitles = new String[effects.length];

                            for (int i = 0; i < navMenuTitles.length; i++) {
                                navMenuTitles[i] = effects[i].toString();
                            }

                            navDrawerItems = new ArrayList<NavDrawerItem>();

                            for (int i = 0; i < navMenuTitles.length; i++) {
                                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(1, -1)));
                            }

                            adapter = new NavDrawerListAdapter(getApplicationContext(),
                                    navDrawerItems);
                            mDrawerList.setAdapter(adapter);

                            mDrawerList.setOnItemClickListener(new DrawerEffectsClicklistener());


                        } else {
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast.makeText(context, "no effects support", duration).show();
                        }

                    } else if (pos == 2) {

                        final CharSequence scenes[] = CameraFeatures.getSupportedCameraScenes();

                        if (scenes != null) {

                            navMenuTitles = new String[scenes.length];

                            for (int i = 0; i < navMenuTitles.length; i++) {
                                navMenuTitles[i] = scenes[i].toString();
                            }

                            navDrawerItems = new ArrayList<NavDrawerItem>();

                            for (int i = 0; i < navMenuTitles.length; i++) {
                                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(2, -1)));
                            }

                            adapter = new NavDrawerListAdapter(getApplicationContext(),
                                    navDrawerItems);
                            mDrawerList.setAdapter(adapter);

                            mDrawerList.setOnItemClickListener(new DrawerScenesItemClickListener());

                        } else {
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast.makeText(context, "no scenes support", duration).show();
                        }

                    } else if (pos == 3) {

                    } else if(pos == 4) {

                    } else if(pos == 5) {
                        //contacts

                    }
                }
            }
        }
    }



    public void imagecapture(View view) {

            CameraFeatures.isTakingpicture = true;
            Button imagebutton = (Button) findViewById(R.id.capturebutton);
            Button recbutton = (Button) findViewById(R.id.recbutton);

            try {
                imagebutton.setEnabled(false);
                recbutton.setEnabled(false);

                camera.setPreviewCallback(null);

                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                    }
                }, null, picture);



            } catch (Exception ex) {
                ex.printStackTrace();
                releaseCamera();
                camera = null;
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            } finally {
                //imagebutton.setEnabled(true);
                //recbutton.setEnabled(true);
            }

    }

    public void recvideo(View view) {

        if(isRecording == true) {
            //stop recording and releash camera
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException ex) {

            }
            releaseMediaRecorder();

            //enable image capture button
            Button imgbutton = (Button)findViewById(R.id.capturebutton);
            imgbutton.setEnabled(true);

            setRecordVideoButtomImage(false);

            releaseCamera();
            camera = null;
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
            isRecording = false;

        } else {
            //initialize video camera
            if(prepareVideoRecorder() == true) {
                isRecording = true;
                //camera is available and unlocked,mediaRecorder is prepared
                //start recording
                try {
                    mediaRecorder.start();
                    //disable image capture button
                    Button imgbutton = (Button)findViewById(R.id.capturebutton);
                    imgbutton.setEnabled(false);
                    setRecordVideoButtomImage(true);
                } catch (IllegalStateException ex) {
                    releaseMediaRecorder();
                    isRecording = false;
                }

            } else {
                //prepare didnt work releashe camera
                releaseMediaRecorder();
            }
        }
    }



    private void setRecordVideoButtomImage(boolean isrec) {
        final Button recbutton = (Button)findViewById(R.id.recbutton);
        if(isrec) {

            Drawable draw = getResources().getDrawable(R.drawable.presence_video_busy);
            recbutton.setBackgroundDrawable(draw);
            recbutton.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           recbutton.setEnabled(true);
                       }
                   });
                }
            },3000);

        } else {
            Drawable draw = getResources().getDrawable(R.drawable.presence_video_online);
            recbutton.setBackgroundDrawable(draw);
            recbutton.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recbutton.setEnabled(true);
                        }
                    });
                }
            }, 3000);
        }

    }


    private boolean prepareVideoRecorder() {

        releaseCamera();

        camera = getCameraInstance();
        mediaRecorder = OrientationUtils.getMediaRecorder(mOrientation);

        //done in getCameraInstance();
        //camera.open();

        //unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);

        //set sources and encoding format
        //encoding format set prior to android 2.2(api lvl 8)
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        //set camcorder profile api lvl8 or higher
        if(CameraFeatures.cameraId == 1) {
            mediaRecorder.setProfile(CamcorderProfile.get(CameraFeatures.cameraId,CamcorderProfile.QUALITY_LOW));
        } else {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }

        //set outputfile
        mediaRecorder.setOutputFile(MediaFileUtils.getOutputMediaFile(MediaFileUtils.MEDIA_TYPE_VIDEO).toString());

        //set preview output
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        //prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();

        } catch(IllegalStateException ex) {
          releaseMediaRecorder();
            ex.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }


        return true;
    }


    public  Camera getCameraInstance() {
        Camera c = null;
        try {
            if(Camera.getNumberOfCameras() == 1) {
                c = Camera.open();
                //// TODO: 21/9/2015 check if no back camera exists open front
                if(c == null) {
                    c = Camera.open(1);
                }
            } else {
                try {
                    c = Camera.open(CameraFeatures.cameraId);
                } catch (Exception ex) {
                    c = Camera.open();
                }
            }
            Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            OrientationUtils.setCameraDisplayOrientation(CameraFeatures.cameraId,c,display);

        } catch(Exception ex) {
           ex.printStackTrace();
            releaseMediaRecorder();
            releaseCamera();
        }

        return c;
    }


    private void picturecallbackSetup() {
        picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data,Camera camera) {
                    if (MediaFileUtils.isFreeSpaceAvailable(data)) {
                        try {
                        final File picturefile = MediaFileUtils.getOutputMediaFile(MediaFileUtils.MEDIA_TYPE_IMAGE);

                        if (picturefile == null) {
                            return;
                        }
                        //set picture rotation
                        //cameraid == 0 => back camera in use
                        final int rotation = OrientationUtils.getRotationForPictureTakeCallBack(mOrientation);
                        final Context context = getApplicationContext();

                            try {
                                Constants.bitmap = BitmapUtils.decodeSampledBitmapFromByteArray(data, 50, 50);
                            } catch (Throwable th) {
                                if(th instanceof OutOfMemoryError) {
                                    System.gc();
                                }
                            }

                            takePictureThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        MediaFileUtils.jnirotateImageAndWriteToFile(rotation, data, picturefile, context);
                                    } catch (Throwable th) {

                                        if(th instanceof OutOfMemoryError) {
                                            System.gc();
                                        } else {
                                        }
                                        //throwable will be thrown only if cpp/so library fails to load
                                        //in that case process image with java
                                        MediaFileUtils.rotateImageAndWriteToFile(rotation, data, picturefile, context);
                                    }
                                }
                            });

                            takePictureThread.setPriority(Thread.MAX_PRIORITY - 1);
                            takePictureThread.start();

                    } finally{
                        //releashing camera
                        camera.release();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        //finishing this activity and starting a new one
                        finish();
                    }
                } else {
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast.makeText(context, "no free space", duration).show();

                        //releashing camera
                        camera.release();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        //finishing this activity and starting a new one
                        finish();
                    }
            }
        };
    }


    private void releaseCamera() {
        if(camera != null) {
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.release();
            camera = null;
        }
    }

    private void releaseMediaRecorder() {
        if(mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }


    //---Begin Override Methods--------------------------------------------

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        System.out.println("drawer action-----onPostCreate----");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("drawer action-----onConfigurationChanged----");
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        releaseCamera();
        orientationEventListener.disable();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(CameraFeatures.params != null && CameraFeatures.hasFlash) {
            if(CameraFeatures.params.getFlashMode() != null) {
                if (CameraFeatures.params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    CameraFeatures.params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    if (camera != null) {
                        camera.setParameters(CameraFeatures.params);
                    }
                }
            }
        }
        if(isRecording == true) {
            //stop recording and releash camera
            mediaRecorder.stop();
            releaseMediaRecorder();

            //enable image capture button
            Button imgbutton = (Button)findViewById(R.id.capturebutton);
            imgbutton.setEnabled(true);

            setRecordVideoButtomImage(false);
            isRecording = false;

            releaseCamera();
            camera = null;
            finish();

        }
        releaseCamera();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(camera == null) {
            camera = getCameraInstance();
            setContentView(R.layout.activity_main);

            cameraPreview = new CameraPreview(this,camera);
            FrameLayout preview = (FrameLayout)findViewById(R.id.camerapreview);
            preview.addView(cameraPreview);

            bringlayoutsToFront();
            setUpDefaultDrawer();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    //|-----END OVERRIDE METHODS--------------------------------


    /**
     * bring Linear layout tha contains buttons to front to be visible in surfaceview-Framelayout
     */
    public void bringlayoutsToFront() {

        //this linearlayout contains take picture and record video button...at tha time
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearbottom);
        ll.bringToFront();

        //...contains option button
        LinearLayout l2 = (LinearLayout)findViewById(R.id.optionsbuttonlayout);
        l2.bringToFront();

        // top right corner
        //LinearLayout l3 = (LinearLayout)findViewById(R.id.right_options);
        //l3.bringToFront();

        //bottom right
        LinearLayout l4 = (LinearLayout)findViewById(R.id.linearimagepreview);
        l4.bringToFront();


    }


    public void optionclick(View v) {
        if(!isRecording) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }


    public void imagepreview(View v) {

        if(!isRecording) {
            if(!CameraFeatures.isTakingpicture) {
                if (Constants.lastpicturefiletaken != null) {
                    if (BucketFiles.getPictureFileSize() > 0) {
                        startActivity(new Intent(MainActivity.this, PreviewImageGridview.class).setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP));
                    }
                }
            }
        }
    }


    private class CameraColorEffectsChange extends AsyncTask<String,Void,Camera> {


        @Override
        protected Camera doInBackground(String... params) {

            String effect = params[0];
            CameraFeatures.params.setColorEffect(effect);
            camera = getCameraInstance();
            return camera;
        }

        protected void onPostExecute(Camera camera) {
            setContentView(R.layout.activity_main);
            cameraPreview = new CameraPreview(MainActivity.this,camera);
            FrameLayout preview = (FrameLayout)findViewById(R.id.camerapreview);
            preview.addView(cameraPreview);
            bringlayoutsToFront();

            mDrawerLayout.closeDrawer(Gravity.LEFT);
            setUpDefaultDrawer();
        }


    }


    private class CameraSceneChange extends AsyncTask<String,Void,Camera> {

        @Override
        protected Camera doInBackground(String... params) {

            String scene = params[0];
            CameraFeatures.params.setSceneMode(scene);
            camera = getCameraInstance();
            return camera;
        }

        protected void onPostExecute(Camera camera) {
            setContentView(R.layout.activity_main);
            cameraPreview = new CameraPreview(MainActivity.this,camera);
            FrameLayout preview = (FrameLayout)findViewById(R.id.camerapreview);
            preview.addView(cameraPreview);
            bringlayoutsToFront();

            mDrawerLayout.closeDrawer(Gravity.LEFT);
            setUpDefaultDrawer();
        }

    }



    public void showFlashOptions(View v) {

        if (CameraFeatures.hasFlash == true) {
            if (CameraFeatures.params.getFlashMode() != null) {

                final CharSequence flashmodes[] = CameraFeatures.getSupportedFlashoptions();

                if (flashmodes != null) {
                    navMenuTitles = new String[flashmodes.length];

                    for(int i =0; i< navMenuTitles.length; i++) {
                        navMenuTitles[i] = flashmodes[i].toString();
                    }

                    navDrawerItems = new ArrayList<NavDrawerItem>();

                    for(int i =0; i < navMenuTitles.length; i++) {
                        if(navMenuTitles[i].equals("flash auto")) {
                            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(3, -1)));
                        } else if(navMenuTitles[i].equals("flash off")) {
                            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(4, -1)));
                        } else if(navMenuTitles[i].equals("flash on")) {
                            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(5, -1)));
                        } else if(navMenuTitles[i].equals("flashlight")) {
                            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(6, -1)));
                        }
                    }

                    adapter = new NavDrawerListAdapter(getApplicationContext(),
                            navDrawerItems);
                    mDrawerList.setAdapter(adapter);

                    mDrawerList.setOnItemClickListener(new DrawerFlashModesClickListener());

                    mDrawerLayout.openDrawer(Gravity.LEFT);

                }

            } else {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, "no flash support!maybe front camera is in use?", duration).show();
            }
        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, "no flash", duration).show();
        }

    }

    public void changeCamera(View v) {

        if(CameraFeatures.frontcam && CameraFeatures.rearcam) {

            if (CameraFeatures.cameraId == 0) {
                CameraFeatures.cameraId = 1;
                releaseCamera();
                if(camera == null) {
                    frontcameraswitch = true;
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                    finish();
                }
            } else if (CameraFeatures.cameraId == 1) {
                CameraFeatures.cameraId = 0;
                releaseCamera();
                if(camera == null) {
                    backcameraswitch = true;
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                    finish();
                }
            }

        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, "only one camera", duration).show();
        }

    }




}
