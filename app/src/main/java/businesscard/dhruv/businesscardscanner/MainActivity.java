package businesscard.dhruv.businesscardscanner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import io.github.memfis19.annca.internal.ui.preview.PreviewActivity;
import io.github.memfis19.annca.internal.ui.view.CameraControlPanel;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static final String PACKAGE_NAME = "businesscard.dhruv.businesscardscanner";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/BusinessCardScanner/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list

    public static final String lang = "eng";

    private static final String TAG = "MainActivity.java";

    public static String _path;
    protected boolean _taken;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int CAPTURE_MEDIA = 368;
    private Activity activity;
    public int screenH;
    public int screenW;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;
        String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenW = size.x;
        screenH = size.y;

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
        // _image = (ImageView) findViewById(R.id.image);
        _path = DATA_PATH + "/ocr.jpg";
        startCameraActivity();
    }

//    public class ButtonClickHandler implements View.OnClickListener {
//        public void onClick(View view) {
//            Log.v(TAG, "Starting Camera app");
//        }
//    }

    // Simple android photo capture:
    // http://labs.makemachine.net/2010/03/simple-android-photo-capture/

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

//        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        AnncaConfiguration.Builder photo1 = new AnncaConfiguration.Builder(activity, CAPTURE_MEDIA);
        photo1.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
        photo1.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_HIGHEST);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        new Annca(photo1.build()).launchCamera();
//        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_MEDIA && resultCode == RESULT_OK) {
            Log.d(TAG, "resultCodeActiRes:  " + requestCode);
            String filePath = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
            Log.d(TAG,"filePathInMainActivity: "+filePath);
            Intent i = new Intent(MainActivity.this, CropImage.class);
            onPhotoTaken();
            MainActivity.this.finish();
            startActivity(i);
        } else {
            Log.v(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    protected void onPhotoTaken() {
        _taken = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        _path = PreviewActivity.previewFilePath;
        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);
            if (rotate != 0) {
                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                Log.d(TAG, "w:::" + w);
                // Setting pre rotate
//                Matrix mtx = new Matrix();
//                mtx.preRotate(rotate);
//
//                // Rotating Bitmap
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
//                rotatedImg.setImageBitmap(bitmap);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);   // current final BITMAP
            Log.d(TAG, "CameraControl_x_y" + CameraControlPanel.x1 + "\n" + CameraControlPanel.y1);

            int y2 = screenH * (CameraControlPanel.y1 / (CameraControlPanel.y2 - CameraControlPanel.y1));
            int x2 = screenW * (CameraControlPanel.x1);

            Log.d(TAG, "screenH" + screenH);
            Log.d(TAG, "screenW" + screenW);

            Log.d(TAG, "y2" + y2);
            Log.d(TAG, "x2" + x2);

            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, CameraControlPanel.x1, CameraControlPanel.y1, 38, 46);
//            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, CameraControlPanel.x1, CameraControlPanel.y1, x2, y2);
//            rotatedImg.setImageBitmap(cropBitmap);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

//        rotatedImg.setImageBitmap(bitmap);

        Log.v(TAG, "Before baseApi");

//        TessBaseAPI baseApi = new TessBaseAPI();
//        baseApi.setDebug(true);
//        baseApi.init(DATA_PATH, lang);
//        baseApi.setImage(bitmap);
//
//        Pix test = baseApi.getThresholdedImage();
//        float a = Skew.findSkew(test);
//        Log.d(TAG, "SkewAngle: " + a);
//
//        Bitmap binarizedBitmap = GetBinaryBitmap(bitmap);
//        TessBaseAPI baseApi1 = new TessBaseAPI();
//        baseApi1.setDebug(true);
//        baseApi1.init(DATA_PATH, lang);
//        baseApi1.setImage(binarizedBitmap);
//        String binaryText = baseApi1.getUTF8Text();
//
//        String recognizedText = baseApi.getUTF8Text();
//        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

//        Log.v(TAG, "OCRED TEXT: " + recognizedText);
//        Log.v(TAG, "OCRED TEXT LATER: " + binaryText);
//
//        if (lang.equalsIgnoreCase("eng")) {
//            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
//        }
//
//        recognizedText = recognizedText.trim();
//
//        if (recognizedText.length() != 0) {
//            _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
//            _field.setSelection(_field.getText().toString().length());
    }
    // Cycle done.
//}

    private Bitmap GetBinaryBitmap(Bitmap bitmap_src) {
        Bitmap bitmap_new = bitmap_src.copy(bitmap_src.getConfig(), true);

        for (int x = 0; x < bitmap_new.getWidth(); x++) {
            for (int y = 0; y < bitmap_new.getHeight(); y++) {
                int color = bitmap_new.getPixel(x, y);
                color = GetNewColor(color);
                bitmap_new.setPixel(x, y, color);
            }
        }

        return bitmap_new;
    }


    private double GetColorDistance(int c1, int c2) {
        int db = Color.blue(c1) - Color.blue(c2);
        int dg = Color.green(c1) - Color.green(c2);
        int dr = Color.red(c1) - Color.red(c2);

        double d = Math.sqrt(Math.pow(db, 2) + Math.pow(dg, 2) + Math.pow(dr, 2));
        return d;
    }

    private int GetNewColor(int c) {
        double dwhite = GetColorDistance(c, Color.WHITE);
        double dblack = GetColorDistance(c, Color.BLACK);

        if (dwhite <= dblack) {
            return Color.WHITE;

        } else {
            return Color.BLACK;
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}