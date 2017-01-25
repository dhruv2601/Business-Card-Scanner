package businesscard.dhruv.businesscardscanner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class SaveCardActivity extends AppCompatActivity {

    public static final String TAG = "SaveCardActivity";
    private RecyclerView rvEntryDetails;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imgScanned;
    public int cardNo;
    public Bitmap cardBitmap;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/BusinessCardScanner/";
    public HashMap<String, String> entities;
    private FloatingActionButton fabSaveContact;

//    private LoadingView mLoadingView;
//    private LoadingView mLoadViewLong;
//    private LoadingView mLoadViewNoRepeat;

    public Handler mHandler;
    //    public Dialog loading;
    private de.hdodenhof.circleimageview.CircleImageView imgPersonImg;
    private Button addAnotherField;

    public static final int PICK_IMAGE_REQUEST = 2601;
    private ArrayList<DataObjectCardEntry> result;
    public static String type[] = new String[30];
    public static String desc[] = new String[30];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_card);

        mHandler = new Handler();
        entities = new HashMap<>();

        addAnotherField = (Button) findViewById(R.id.btn_add_detail);
        imgPersonImg = (CircleImageView) findViewById(R.id.img_profile);
        imgScanned = (ImageView) findViewById(R.id.img_scanned_card);
        rvEntryDetails = (RecyclerView) findViewById(R.id.rv_entry_details);
        rvEntryDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvEntryDetails.setLayoutManager(layoutManager);
        rvEntryDetails.setNestedScrollingEnabled(false);
        adapter = new EntryDetailsRVAdapter(getDataSet1(), SaveCardActivity.this);
        rvEntryDetails.setAdapter(adapter);

        fabSaveContact = (FloatingActionButton) findViewById(R.id.fab_save_contact);
        fabSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // edit text mn jo data edit hua hai save that

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] data = stream.toByteArray();
                String convertByte = "";

                int i = 0;
                SharedPreferences pref = SaveCardActivity.this.getSharedPreferences("AllCards", 0);
                SharedPreferences.Editor editor = pref.edit();
                int totalCards = pref.getInt("CardNo", 0);
                int numEntities = entities.size();
                for (i = 0; i <= numEntities; i++) {
                    editor.putString("Card" + (totalCards + 1) + "Detail" + i, entities.get(i));
                }
                convertByte = Base64.encodeToString(data, Base64.DEFAULT);
                editor.putString("Card" + (totalCards + 1) + "Photo", convertByte);

                ++totalCards;
                editor.putInt("CardNo", totalCards);
                editor.commit();
            }
        });

        imgPersonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass intent to add image from gallery and then set here and save and update n the shared preference and the server also
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        addAnotherField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yet = 0;
                if (entities.containsKey("NewNo")) {
                    yet = Integer.parseInt(entities.get("NewNo"));
                }

                entities.put("NewField" + (++yet), "");
                entities.put("NewNo", String.valueOf(yet));

                EntryDetailsRVAdapter entryDetailsRVAdapter = new EntryDetailsRVAdapter(result, SaveCardActivity.this);
                DataObjectCardEntry cardEntry = new DataObjectCardEntry("", "");

                result.add(cardEntry);
//                entryDetailsRVAdapter.addItem(cardEntry, result.size());

                adapter.notifyDataSetChanged();
            }
        });

//        loading = new Dialog(this, R.style.MyInvisibleDialog);
//        loading.setCancelable(false);
//        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


//        mLoadingView = (LoadingView) findViewById(R.id.loading_view_repeat);
//
//        int marvel_1 = R.drawable.marvel_1;
//        int marvel_2 = R.drawable.marvel_4;
//        int marvel_3 = R.drawable.marvel_3;
//        int marvel_4 = R.drawable.marvel_2;
//
//        mLoadingView.addAnimation(Color.parseColor("#FFD200"), marvel_1,
//                LoadingView.FROM_LEFT);
//        mLoadingView.addAnimation(Color.parseColor("#2F5DA9"), marvel_2,
//                LoadingView.FROM_TOP);
//        mLoadingView.addAnimation(Color.parseColor("#FF4218"), marvel_3,
//                LoadingView.FROM_RIGHT);
//        mLoadingView.addAnimation(Color.parseColor("#C7E7FB"), marvel_4,
//                LoadingView.FROM_BOTTOM);
//
//        mLoadViewNoRepeat = (LoadingView)
//
//                findViewById(R.id.loading_view);
//
//        mLoadViewNoRepeat.addAnimation(Color.parseColor("#2F5DA9"), marvel_2, LoadingView.FROM_LEFT);
//        mLoadViewNoRepeat.addAnimation(Color.parseColor("#FF4218"), marvel_3, LoadingView.FROM_LEFT);
//        mLoadViewNoRepeat.addAnimation(Color.parseColor("#FFD200"), marvel_1, LoadingView.FROM_RIGHT);
//        mLoadViewNoRepeat.addAnimation(Color.parseColor("#C7E7FB"), marvel_4, LoadingView.FROM_RIGHT);
//
//        mLoadViewLong = (LoadingView)
//
//                findViewById(R.id.loading_view_long);
//
//        mLoadViewLong.addAnimation(Color.parseColor("#FF4218"), marvel_3, LoadingView.FROM_TOP);
//        mLoadViewLong.addAnimation(Color.parseColor("#C7E7FB"), marvel_4, LoadingView.FROM_BOTTOM);
//        mLoadViewLong.addAnimation(Color.parseColor("#FF4218"), marvel_3, LoadingView.FROM_TOP);
//        mLoadViewLong.addAnimation(Color.parseColor("#C7E7FB"), marvel_4, LoadingView.FROM_BOTTOM);

        SharedPreferences preferences = this.getSharedPreferences("SavedCards", 0);
        cardNo = preferences.getInt("CardNo", 1);
        String imageUri = preferences.getString("ImageUri" + cardNo, "");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        cardBitmap = BitmapFactory.decodeFile(String.valueOf(imageUri), options);

        imgScanned.setImageBitmap(cardBitmap);
        new extractOCR().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
//                imgPersonImg.setImageBitmap(bitmap);

                // --------------->>>>>>>>>>>>>>>>>>>>      SAVE IN SHARED PREFERENCES AND LOCALLY ONLY   <<<<<<<<<--------

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<DataObjectCardEntry> getDataSet1() {
        ArrayList<DataObjectCardEntry> result = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DataObjectCardEntry data = new DataObjectCardEntry("Fetching...", "Fetching...");
            result.add(data);
        }
        return result;
    }

    private ArrayList<DataObjectCardEntry> getDataSet() {
        ArrayList<DataObjectCardEntry> result = new ArrayList<DataObjectCardEntry>();
        int size = entities.size();
        Log.d(TAG, "entitiesSize: " + size);
        int x = 0;
        int i = 0;
        if (entities.containsKey("Phone")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Phone", entities.get("Phone"));
            result.add(data);
        }
        while (x != 1) {
            if (entities.containsKey("Phone" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Phone", entities.get("Phone" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("Website")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Website", entities.get("Website"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("Website" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Website", entities.get("Website" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("Email")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Email", entities.get("Email"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("Email" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Email", entities.get("Email" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("Name")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Name", entities.get("Name"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("Name" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Name", entities.get("Name" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }


        if (entities.containsKey("CompanyName")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Company", entities.get("CompanyName"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("CompanyName" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Company", entities.get("CompanyName" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("CompanyAdd")) {
            DataObjectCardEntry data = new DataObjectCardEntry("CompanyAdd", entities.get("CompanyAdd"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("CompanyAdd" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Address", entities.get("CompanyAdd" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

//        if(entities.containsKey("NewNo"))
        {
//            int yet = Integer.parseInt(entities.get("NewNo"));
//            EntryDetailsRVAdapter entryDetailsRVAdapter = new EntryDetailsRVAdapter(result,this);
//            DataObjectCardEntry cardEntry = new DataObjectCardEntry("","");
//            entryDetailsRVAdapter.addItem(cardEntry,0);
        }
        this.result = result;
        return result;
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


    public class tikaOpenIntro {

        public String Tokens[];

        public String namefind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
                is = new FileInputStream("/storage/emulated/0/en-ner-person.bin");

                tnf = new TokenNameFinderModel(is);
                nf = new NameFinderME(tnf);

                Span sp[] = nf.find(cnt);
                String a[] = Span.spansToStrings(sp, cnt);
                StringBuilder fd = new StringBuilder();
                int l = a.length;

                for (int j = 0; j < l; j++) {
                    fd = fd.append(a[j] + "\n");
                }
                sd = fd.toString();

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (InvalidFormatException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return sd;
        }

        public String orgFind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
                is = new FileInputStream(
                        "/storage/emulated/0/en-ner-organization.bin");

                tnf = new TokenNameFinderModel(is);
                nf = new NameFinderME(tnf);
                Span sp[] = nf.find(cnt);
                String a[] = Span.spansToStrings(sp, cnt);
                StringBuilder fd = new StringBuilder();
                int l = a.length;

                for (int j = 0; j < l; j++) {
                    fd = fd.append(a[j] + "\n");
                }

                sd = fd.toString();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (InvalidFormatException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return sd;
        }

        public String locationFind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
                is = new FileInputStream(
                        "/storage/emulated/0/en-ner-location.bin");

                tnf = new TokenNameFinderModel(is);
                nf = new NameFinderME(tnf);
                Span sp[] = nf.find(cnt);
                String a[] = Span.spansToStrings(sp, cnt);
                StringBuilder fd = new StringBuilder();
                int l = a.length;

                for (int j = 0; j < l; j++) {
                    fd = fd.append(a[j] + "\n");
                }

                sd = fd.toString();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (InvalidFormatException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return sd;
        }

        public void tokenization(String tokens) {

            InputStream is;
            TokenizerModel tm;
            try {
                is = new FileInputStream("/storage/emulated/0/en-token.bin");
                tm = new TokenizerModel(is);
                Tokenizer tz = new TokenizerME(tm);
                Tokens = tz.tokenize(tokens);

                for (int i = 0; i < Tokens.length; i++) {
                    Log.d(TAG, "tokens: " + Tokens[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class extractOCR extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDial = new ProgressDialog(SaveCardActivity.this);
        @Override
        protected void onPreExecute() {
            pDial.setIcon(R.drawable.appicon);
            pDial.setMessage("Extracting Details");
            pDial.setCancelable(false);
            pDial.setTitle("Scanning Card");
//            pDial.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);        // check if error
//            pDial.incrementProgressBy(10);
            pDial.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap binarizedBitmap = GetBinaryBitmap(cardBitmap);
            TessBaseAPI baseAPI = new TessBaseAPI();
            baseAPI.setDebug(true);
            baseAPI.init(DATA_PATH, "eng");
            baseAPI.setImage(binarizedBitmap);
            String binaryText = baseAPI.getUTF8Text();
            baseAPI.end();

            Log.d(TAG, "OCR text: " + binaryText);
            if (MainActivity.lang.equalsIgnoreCase("eng")) {
                String temp = binaryText.replaceAll("[^a-zA-Z0-9]+", " ");
                Log.d(TAG, "tempStr: " + temp);
            }

            // PHONE NO. EXTRACTION(PNE)

            try {
                Pattern regex = Pattern.compile("(?:\\d+\\s*)+");
                Matcher regexMatcher = regex.matcher(binaryText);

                int i = 0;
                while (regexMatcher.find()) {
                    Log.d(TAG, "phoneNo: " + regexMatcher.group() + " ,,,,,,,,regexMatcher.start: " + regexMatcher.start() + " ,,,,,,,,regexMatcher.end: " + regexMatcher.end());
                    if (regexMatcher.group().length() > 7) {
                        entities.put("Phone" + (i++), regexMatcher.group());
                        // is a potential phone no.
                    }
                }
            } catch (PatternSyntaxException ex) {
                ex.printStackTrace();
                // Syntax error
            }

            //PhoneNoExtraction ENDS

            // EMAILS EXTRACTION START

//            int i = 0;
//            Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)", Pattern.CASE_INSENSITIVE);
//            Matcher emailMatcher = ptr.matcher(binaryText);
//            while (emailMatcher.find()) {
//                Log.d(TAG, "Email: " + emailMatcher.group() + "\n" + emailMatcher.start() + "\n" + emailMatcher.end());
//                entities.put("Email" + (i++), emailMatcher.group()+".com");
//            }
//
//            Log.d(TAG, "emailOver");

            int no = 0;
            for (int i = 0; i < binaryText.length(); i++) {
                if (binaryText.charAt(i) == '@') {
                    String email = "";
                    int x = 0;
                    int y = i;
                    while (x != 1) {
                        if (binaryText.charAt(y) == ' ') {
                            x = 1;
                            for (int d = y; d <= i; d++) {
                                email += binaryText.charAt(d);
                            }
                        } else {
                            y--;
                        }
                    }

                    y = i + 1;
                    x = 0;

                    while (x != 1) {
                        if (binaryText.charAt(y - 2) == 'o' && binaryText.charAt(y - 1) == 'm') {
                            x = 1;
                        } else {
                            email += binaryText.charAt(y);
                            y++;
                        }
                    }

                    entities.put("Email" + (no++), email);
                }
            }

            // EMAILS EXTRACTION ENDS

            // WEB PAGE EXTRACTION BEGINS
            Pattern ptr1 = Pattern.compile("(http:\\/\\/|https:\\/\\/|www.)?[a-z]{5}.?([a-z]+)?(.com|.in|.edu|.ca|.usa)$");
            Matcher webpageMatcher = ptr1.matcher(binaryText);

            int j = 0;
            while (webpageMatcher.find()) {
                Log.d(TAG, "website: " + webpageMatcher.group() + "\n" + webpageMatcher.start() + "\n" + webpageMatcher.end());
                entities.put("Website" + (j++), webpageMatcher.group());
            }

            //WEB PAGE EXTRACTION ENDS

            SaveCardActivity.tikaOpenIntro toi = new SaveCardActivity.tikaOpenIntro();

            toi.tokenization(binaryText);           // try here the converted text also

//            String names = toi.namefind(toi.Tokens);
//            String org = toi.orgFind(toi.Tokens);
//            String location = toi.locationFind(toi.Tokens);
//            if (names != null) {
//                entities.put("Name", names);
//            }

//            entities.put("CompanyName", org);
//            entities.put("CompanyAdd", location);

//            Log.d(TAG, "person name is : " + names);
//            Log.d(TAG, "organization name: " + org);
//            Log.d(TAG, "location is: " + location);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new EntryDetailsRVAdapter(getDataSet(), SaveCardActivity.this);
            rvEntryDetails.setAdapter(adapter);
            pDial.dismiss();

            super.onPostExecute(aVoid);
        }
    }
}