package businesscard.dhruv.businesscardscanner;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.soundcloud.android.crop.Crop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.github.memfis19.annca.internal.ui.preview.PreviewActivity;
//import opennlp.tools.namefind.NameFinderME;
//import opennlp.tools.namefind.TokenNameFinderModel;
//import opennlp.tools.tokenize.Tokenizer;
//import opennlp.tools.tokenize.TokenizerME;
//import opennlp.tools.tokenize.TokenizerModel;
//import opennlp.tools.util.InvalidFormatException;
//import opennlp.tools.util.Span;

public class CropImage extends Activity {

    private static final String TAG = "CropImage";
    private ImageView resultView;
    public static Uri croppedImgUri;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/BusinessCardScanner/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        resultView = (ImageView) findViewById(R.id.result_image);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
//        String p = "file://" + "/storage/sdcard1/DCIM/Camera/Screenshot_2016-12-20-16-33-28_businesscard.dhruv.testingtesseract.png";
//        Bitmap bitmap = BitmapFactory.decodeFile((p), options);
        String p;
        p = "file://" + MainActivity._path;
        Log.d(TAG, "_path: " + p);
        Uri u = Uri.parse(p);
        Crop.of(u, u).start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activiy_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            resultView.setImageDrawable(null);
            Crop.pickImage(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        Log.d("requestCodeCropImage: ", "requestCode: " + requestCode + "\n" + resultCode);
//        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            beginCrop(result.getData());
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            handleCrop(resultCode, result);
//        }
//        String p = "/storage/sdcard1/DCIM/Camera/Screenshot_2016-12-20-16-33-28_businesscard.dhruv.testingtesseract.png";
//        beginCrop(Uri.parse(p));

        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Log.d(TAG, "inside begin Crop");
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).withMaxSize(500, 500).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "inside handle Crop");
            Log.d(TAG, "result::: " + result.getData());
            croppedImgUri = result.getParcelableExtra("output");
            Log.d(TAG, "imgUri " + croppedImgUri);
            resultView.setImageURI(Crop.getOutput(result));

            String u = String.valueOf(croppedImgUri);
            String u1 = "";
            int count = 0;

            for (int i = 0; i < u.length(); i++) {
                if (count > 7) {
                    u1 += u.charAt(i);
                }
                count++;
            }

            croppedImgUri = Uri.parse(u1);
            SharedPreferences pref = this.getSharedPreferences("SavedCards",0);
            SharedPreferences.Editor editor = pref.edit();
            int cardNo = pref.getInt("CardNo",0);               // card no is 1 indexed
            editor.putInt("SavedCards",++cardNo);
            editor.putString("ImageUri"+cardNo, String.valueOf(croppedImgUri));
            editor.commit();

            Intent i = new Intent(CropImage.this, SaveCardActivity.class);
            startActivity(i);
            CropImage.this.finish();

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
//            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(croppedImgUri), options);
//            resultView.setImageBitmap(bitmap);
//
//            Bitmap binarizedBitmap = GetBinaryBitmap(bitmap);
//            resultView.setImageBitmap(binarizedBitmap);
//            TessBaseAPI baseApi1 = new TessBaseAPI();
//            baseApi1.setDebug(true);
//            baseApi1.init(DATA_PATH, "eng");
//            baseApi1.setImage(binarizedBitmap);
//            String binaryText = baseApi1.getUTF8Text();
//            baseApi1.end();
//
//            if (MainActivity.lang.equalsIgnoreCase("eng")) {
//                String temp = binaryText.replaceAll("[^a-zA-Z0-9]+", " ");
//                Log.d(TAG, "tempStr: " + temp);
//
//                CropImage.tikaOpenIntro toi = new CropImage.tikaOpenIntro();
//
//                toi.tokenization(temp);
//
////                String names = toi.namefind(toi.Tokens);
////                String org = toi.orgfind(toi.Tokens);
////                String location = toi.locationFind(toi.Tokens);
//
////                Log.d(TAG, "organization name: " + org);
////                Log.d(TAG, "person name is : " + names);
////                Log.d(TAG,"location is: "+location);
//            }
//
//            Log.d(TAG, "OCR Cropped: " + binaryText);
//
//            // PHONE NO. EXTRACTION(PNE)
//
//            try {
//                Pattern regex = Pattern.compile("(?:\\d+\\s*)+");
//                Matcher regexMatcher = regex.matcher(binaryText);
//
//                while (regexMatcher.find()) {
//                    Log.d(TAG, "phoneNo: " + regexMatcher.group() + " ,,,,,,,,regexMatcher.start: " + regexMatcher.start() + " ,,,,,,,,regexMatcher.end: " + regexMatcher.end());
//
////                    Log.d(TAG, "phoneNo: " + regexMatcher.group() + "\n" + regexMatcher.start() + "\n" + regexMatcher.end());
//                    if (regexMatcher.group().length() >= 6) {
//                        // is a potential phone no.
//                    }
//                }
//            } catch (PatternSyntaxException ex) {
//                ex.printStackTrace();
//                // Syntax error
//            }
//            //PhoneNoExtraction ENDS
//
////            boolean b = Patterns.PHONE.matcher(binaryText).matches();
////            Log.d(TAG, "b::: " + b);
////            b = Patterns.EMAIL_ADDRESS.matcher(binaryText).matches();
////            Log.d(TAG, "b::: " + b);
////            b = Patterns.WEB_URL.matcher(binaryText).matches();
////            Log.d(TAG, "b::: " + b);
//
//            // EMAILS EXTRACTION START
//
//            Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)", Pattern.CASE_INSENSITIVE);
//            Matcher emailMatcher = ptr.matcher(binaryText);
//            while (emailMatcher.find()) {
//                Log.d(TAG, "Email: " + emailMatcher.group() + "\n" + emailMatcher.start() + "\n" + emailMatcher.end());
//
//            }
//
//            Log.d(TAG, "emailOver");
//            // EMAILS EXTRACTION ENDS
//
//            // WEB PAGE EXTRACTION BEGINS
//            Pattern ptr1 = Pattern.compile("(http:\\/\\/|https:\\/\\/|www.)?[a-z]{5}.?([a-z]+)?(.com|.in|.edu|.ca|.usa)$");
//            Matcher webpageMatcher = ptr1.matcher(binaryText);
//
//            while (webpageMatcher.find()) {
//                Log.d(TAG, "website: " + webpageMatcher.group() + "\n" + webpageMatcher.start() + "\n" + webpageMatcher.end());
//            }
//
//            //WEB PAGE EXTRACTION ENDS
//        } else if (resultCode == Crop.RESULT_ERROR) {
//            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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

//    public class tikaOpenIntro {
//
//        public String Tokens[];
//
//        public String namefind(String cnt[]) {
//            InputStream is;
//            TokenNameFinderModel tnf;
//            NameFinderME nf;
//            String sd = "";
//            try {
//                is = new FileInputStream("/storage/emulated/0/en-ner-person.bin");
//
//                tnf = new TokenNameFinderModel(is);
//                nf = new NameFinderME(tnf);
//
//                Span sp[] = nf.find(cnt);
//                String a[] = Span.spansToStrings(sp, cnt);
//                StringBuilder fd = new StringBuilder();
//                int l = a.length;
//
//                for (int j = 0; j < l; j++) {
//                    fd = fd.append(a[j] + "\n");
//                }
//                sd = fd.toString();
//
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            } catch (InvalidFormatException e) {
//
//                e.printStackTrace();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//            return sd;
//        }
//
//        public String orgfind(String cnt[]) {
//            InputStream is;
//            TokenNameFinderModel tnf;
//            NameFinderME nf;
//            String sd = "";
//            try {
//                is = new FileInputStream(
//                        "/storage/emulated/0/en-ner-organization.bin");
//
//                tnf = new TokenNameFinderModel(is);
//                nf = new NameFinderME(tnf);
//                Span sp[] = nf.find(cnt);
//                String a[] = Span.spansToStrings(sp, cnt);
//                StringBuilder fd = new StringBuilder();
//                int l = a.length;
//
//                for (int j = 0; j < l; j++) {
//                    fd = fd.append(a[j] + "\n");
//                }
//
//                sd = fd.toString();
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            } catch (InvalidFormatException e) {
//
//                e.printStackTrace();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//            return sd;
//        }
//
//        public String locationFind(String cnt[]) {
//            InputStream is;
//            TokenNameFinderModel tnf;
//            NameFinderME nf;
//            String sd = "";
//            try {
//                is = new FileInputStream(
//                        "/storage/emulated/0/en-ner-location.bin");
//
//                tnf = new TokenNameFinderModel(is);
//                nf = new NameFinderME(tnf);
//                Span sp[] = nf.find(cnt);
//                String a[] = Span.spansToStrings(sp, cnt);
//                StringBuilder fd = new StringBuilder();
//                int l = a.length;
//
//                for (int j = 0; j < l; j++) {
//                    fd = fd.append(a[j] + "\n");
//                }
//
//                sd = fd.toString();
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            } catch (InvalidFormatException e) {
//
//                e.printStackTrace();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//            return sd;
//        }
//
//        public void tokenization(String tokens) {
//
//            InputStream is;
//            TokenizerModel tm;
//
//            try {
//                is = new FileInputStream("/storage/emulated/0/en-token.bin");
//                tm = new TokenizerModel(is);
//                Tokenizer tz = new TokenizerME(tm);
//                Tokens = tz.tokenize(tokens);
//
//                for (int i = 0; i < Tokens.length; i++) {
//                    Log.d(TAG, "tokens: " + Tokens[i]);
//                }
//                // System.out.println(Tokens[1]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}