package com.protectsoft.camshare.editimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.protectsoft.camshare.Constants;
import com.protectsoft.camshare.R;
import com.protectsoft.camshare.Utils.MediaFileUtils;
import com.protectsoft.camshare.bucket.BucketFiles;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by abraham on 16/10/2015.
 */
public class EditImage extends Activity {

    private File currentFile;

    private ImageView imageView;
    private ImageView defaultImageview;

    private ImageView imageViewinvert;
    private ImageView imageViewGreyscale;
    private ImageView imageViewSepia1;
    private ImageView imageViewSepia2;
    private ImageView imageViewSepia3;
    private ImageView imageViewContrast;
    private ImageView imageViewBrightness1;
    private ImageView imageViewBrightness2;
    private ImageView imageViewBrightness3;
    private ImageView imageViewEmboss;
    private ImageView imageViewEngrave;
    private ImageView imageViewBoost1;
    private ImageView imageViewBoost2;
    private ImageView imageViewBoost3;
    private ImageView imageViewSnoweffect;
    private ImageView imageViewShade;
    private ImageView imageViewReflection;

    private ImageView imageViewRedlight;
    private ImageView imageViewReddark;
    private ImageView imageViewMagentalight;
    private ImageView imageViewMagentadark;
    private ImageView imageViewBluelight;
    private ImageView imageViewBluedark;
    private ImageView imageViewCyanlight;
    private ImageView imageViewCyandark;
    private ImageView imageViewGreenlight;
    private ImageView imageViewGreendark;
    private ImageView imageViewYellowlight;
    private ImageView imageViewYellowdark;
    
    private List<ImageView> images = new ArrayList<>();


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.editimagelayout);

        Intent intent = getIntent();
        currentFile = (File)intent.getExtras().get("file");

        imageView = (ImageView)findViewById(R.id.mainImage);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(imageView);


            defaultImageview = (ImageView)findViewById(R.id.original);

            Picasso.with(getApplicationContext())
                    .load(currentFile)
                    .resize(100,100)
                    .centerCrop()
                    .into(defaultImageview);

        {

            imageViewinvert = (ImageView) findViewById(R.id.invert);
            images.add(imageViewinvert);
            imageViewGreyscale = (ImageView) findViewById(R.id.greyscale);
            images.add(imageViewGreyscale);
            imageViewSepia1 = (ImageView) findViewById(R.id.sepia1);
            images.add(imageViewSepia1);
            imageViewSepia2 = (ImageView) findViewById(R.id.sepia2);
            images.add(imageViewSepia2);
            imageViewSepia3 = (ImageView) findViewById(R.id.sepia3);
            images.add(imageViewSepia3);
            imageViewContrast = (ImageView) findViewById(R.id.contrast);
            images.add(imageViewContrast);
            imageViewBrightness1 = (ImageView) findViewById(R.id.brightness1);
            images.add(imageViewBrightness1);
            imageViewBrightness2 = (ImageView) findViewById(R.id.brightness2);
            images.add(imageViewBrightness2);
            imageViewBrightness3 = (ImageView) findViewById(R.id.brightness3);
            images.add(imageViewBrightness3);
            imageViewEmboss = (ImageView) findViewById(R.id.emboss);
            images.add(imageViewEmboss);
            imageViewEngrave = (ImageView) findViewById(R.id.engrave);
            images.add(imageViewEngrave);
            imageViewBoost1 = (ImageView) findViewById(R.id.boost1);
            images.add(imageViewBoost1);
            imageViewBoost2 = (ImageView) findViewById(R.id.boost2);
            images.add(imageViewBoost2);
            imageViewBoost3 = (ImageView) findViewById(R.id.boost3);
            images.add(imageViewBoost3);
            imageViewSnoweffect = (ImageView) findViewById(R.id.snoweffect);
            images.add(imageViewSnoweffect);
            imageViewShade = (ImageView) findViewById(R.id.shade);
            images.add(imageViewShade);
            imageViewReflection = (ImageView) findViewById(R.id.reflection);
            images.add(imageViewReflection);
            imageViewRedlight = (ImageView) findViewById(R.id.redlight);
            images.add(imageViewRedlight);
            imageViewReddark = (ImageView) findViewById(R.id.reddark);
            images.add(imageViewReddark);
            imageViewMagentalight = (ImageView) findViewById(R.id.magentalight);
            images.add(imageViewMagentalight);
            imageViewMagentadark = (ImageView) findViewById(R.id.magentadark);
            images.add(imageViewMagentadark);
            imageViewBluelight = (ImageView) findViewById(R.id.bluelight);
            images.add(imageViewBluelight);
            imageViewBluedark = (ImageView) findViewById(R.id.bluedark);
            images.add(imageViewBluedark);
            imageViewCyanlight = (ImageView) findViewById(R.id.cyanlight);
            images.add(imageViewCyanlight);
            imageViewCyandark = (ImageView) findViewById(R.id.cyandark);
            images.add(imageViewCyandark);
            imageViewGreenlight = (ImageView) findViewById(R.id.greenlight);
            images.add(imageViewGreenlight);
            imageViewGreendark = (ImageView) findViewById(R.id.greendark);
            images.add(imageViewGreendark);
            imageViewYellowlight = (ImageView) findViewById(R.id.yellowlight);
            images.add(imageViewYellowlight);
            imageViewYellowdark = (ImageView) findViewById(R.id.yellowdark);
            images.add(imageViewYellowdark);

            new LoadImageEffects().execute(currentFile);

        }


        LinearLayout save = (LinearLayout)findViewById(R.id.save);
        save.bringToFront();

    }


    public void saveImage(View v) {

        if(currentFile.exists()) {

                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                File file = MediaFileUtils.getOutputFileForS3Download(("edited" + currentFile.getName()));
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

                if(!BucketFiles.isFileExists(file)) {
                    BucketFiles.addPictureFile(file);
                }
                finish();
            }

    }

    public void loadDefault(View v) {
        ColorFilters.colorDefault(imageView);
        defaultImageview.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        defaultImageview.setClickable(true);
                    }
                });
            }
        },1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(imageView);
    }

    public void invert(View view) {
        ColorFilters.colorDefault(imageView);
        imageViewinvert.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewinvert.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.doInvert(((BitmapDrawable) imageView.getDrawable()).getBitmap());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void greyscale(View view) {
        ColorFilters.colorDefault(imageView);
        imageViewGreyscale.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewGreyscale.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.doGreyscale(((BitmapDrawable) imageView.getDrawable()).getBitmap());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void sepia1(View view) {
        ColorFilters.colorDefault(imageView);
        imageViewSepia1.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewSepia1.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.createSepiaToningEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 150, .7, 0.3, 0.12);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void sepia2(View view) {
        ColorFilters.colorDefault(imageView);
        imageViewSepia2.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewSepia2.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.createSepiaToningEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 95, .2, 0.8, 0.50);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void sepia3(View view) {
        ColorFilters.colorDefault(imageView);
        imageViewSepia3.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewSepia3.setClickable(true);
                    }
                });
            }
        }, 1500);


        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.createSepiaToningEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 150, .1, 0.4, 0.9);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void contrast(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewContrast.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewContrast.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.createContrast(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 0.5);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void brightness1(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBrightness1.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBrightness1.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.doBrightness(((BitmapDrawable) imageView.getDrawable()).getBitmap(), -60);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void brightness2(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBrightness2.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBrightness2.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.doBrightness(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 50);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void brightness3(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBrightness3.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBrightness3.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.doBrightness(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 75);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void emboss(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewEmboss.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewEmboss.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.emboss(((BitmapDrawable) imageView.getDrawable()).getBitmap());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void engrave(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewEngrave.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewEngrave.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.engrave(((BitmapDrawable) imageView.getDrawable()).getBitmap());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void boost1(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBoost1.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBoost1.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.boost(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 1, 0.5f);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void boost2(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBoost2.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBoost2.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.boost(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 2, 0.5f);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    public void boost3(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBoost3.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBoost3.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.boost(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 3, 0.5f);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void snoweffect(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewSnoweffect.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewSnoweffect.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.applySnowEffect(((BitmapDrawable) imageView.getDrawable()).getBitmap());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void shade(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewShade.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewShade.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.applyShadingFilter(((BitmapDrawable) imageView.getDrawable()).getBitmap(), -15000);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void reflection(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewReflection.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewReflection.setClickable(true);
                    }
                });
            }
        }, 1500);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final Bitmap bitmap = Effects.applyReflection(((BitmapDrawable) imageView.getDrawable()).getBitmap());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void redlight(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewRedlight.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewRedlight.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterRedLight(imageView,bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


    }

    public void reddark(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewReddark.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewReddark.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterRedDark(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void magentalight(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewMagentalight.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewMagentalight.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterMagentaLight(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void magentadark(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewMagentadark.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewMagentadark.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterMagentaDark(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void bluelight(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBluelight.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBluelight.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterBlueLight(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void bluedark(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewBluedark.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewBluedark.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterBlueDark(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void cyanlight(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewCyanlight.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewCyanlight.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterCyanLight(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void cyandark(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewCyandark.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewCyandark.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterCyanDark(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void greenlight(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewGreenlight.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewGreenlight.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterGreenLight(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void greendark(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewGreendark.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewGreendark.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterGreenDark(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void yellowlight(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewYellowlight.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewYellowlight.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterYellowLight(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    public void yellowdark(View v) {
        ColorFilters.colorDefault(imageView);
        imageViewYellowdark.setClickable(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewYellowdark.setClickable(true);
                    }
                });
            }
        }, 1000);

        Picasso.with(getApplicationContext())
                .load(currentFile)
                .resize(350, 450)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        ColorFilters.doColorFilterYellowDark(imageView,bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }




    private class LoadImageEffects extends AsyncTask<File, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(File... params) {
            File file = params[0];
            return null;
        }

        protected  void onPostExecute(Bitmap result) {

            for(int i =0; i < images.size(); i++) {

                final int finalI = i;
                Picasso.with(getApplicationContext())
                        .load(currentFile)
                        .resize(100, 100)
                        .centerCrop()
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if(finalI == 0) {
                                    images.get(0).setImageBitmap(Effects.doInvert(bitmap));
                                } else if(finalI == 1) {
                                    images.get(1).setImageBitmap(Effects.doGreyscale(bitmap));
                                } else if(finalI == 2) {
                                    images.get(2).setImageBitmap(Effects.createSepiaToningEffect(bitmap, 150, .7, 0.3, 0.12));
                                } else if(finalI == 3) {
                                    images.get(3).setImageBitmap(Effects.createSepiaToningEffect(bitmap, 95, .2, 0.8, 0.50));
                                } else if(finalI == 4) {
                                    images.get(4).setImageBitmap(Effects.createSepiaToningEffect(bitmap, 150, .1, 0.4, 0.9));
                                } else if(finalI == 5) {
                                    images.get(5).setImageBitmap(Effects.createContrast(bitmap, 0.5));
                                } else if(finalI == 6) {
                                    images.get(6).setImageBitmap(Effects.doBrightness(bitmap, -60));
                                } else if(finalI == 7) {
                                    images.get(7).setImageBitmap(Effects.doBrightness(bitmap, 50));
                                } else if(finalI == 8) {
                                    images.get(8).setImageBitmap(Effects.doBrightness(bitmap, 75));
                                } else if(finalI == 9) {
                                    images.get(9).setImageBitmap(Effects.emboss(bitmap));
                                } else if(finalI == 10) {
                                    images.get(10).setImageBitmap(Effects.engrave(bitmap));
                                } else if(finalI == 11) {
                                    images.get(11).setImageBitmap(Effects.boost(bitmap, 1, 0.5f));
                                } else if(finalI == 12) {
                                    images.get(12).setImageBitmap(Effects.boost(bitmap, 2, 0.5f));
                                } else if(finalI == 13) {
                                    images.get(13).setImageBitmap(Effects.boost(bitmap, 3, 0.5f));
                                } else if(finalI == 14) {
                                    images.get(14).setImageBitmap(Effects.applySnowEffect(bitmap));
                                } else if(finalI == 15) {
                                    images.get(15).setImageBitmap(Effects.applyShadingFilter(bitmap, -15000));
                                } else if(finalI == 16) {
                                    images.get(16).setImageBitmap(Effects.applyReflection(bitmap));
                                } else if(finalI == 17) {
                                    images.get(17).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterRedLight(images.get(17),bitmap);
                                } else if(finalI == 18) {
                                    images.get(18).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterRedDark(images.get(18),bitmap);
                                } else if(finalI == 19) {
                                    images.get(19).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterMagentaLight(images.get(19),bitmap);
                                } else if(finalI == 20) {
                                    images.get(20).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterMagentaDark(images.get(20),bitmap);
                                } else if(finalI == 21) {
                                    images.get(21).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterBlueLight(images.get(21),bitmap);
                                } else if(finalI == 22) {
                                    images.get(22).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterBlueDark(images.get(22),bitmap);
                                } else if(finalI == 23) {
                                    images.get(23).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterCyanLight(images.get(23),bitmap);
                                } else if(finalI == 24) {
                                    images.get(24).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterCyanDark(images.get(24),bitmap);
                                } else if(finalI == 25) {
                                    images.get(25).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterGreenLight(images.get(25),bitmap);
                                } else if(finalI == 26) {
                                    images.get(26).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterGreenDark(images.get(26),bitmap);
                                } else if(finalI == 27) {
                                    images.get(27).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterYellowLight(images.get(27),bitmap);
                                } else if(finalI == 28) {
                                    images.get(28).setImageBitmap(bitmap);
                                    ColorFilters.doColorFilterYellowDark(images.get(28),bitmap);
                                }

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }

                        });
            }


        }

    }



}
