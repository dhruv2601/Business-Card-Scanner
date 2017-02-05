package businesscard.dhruv.businesscardscanner;

import android.*;
import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lapism.searchview.SearchView;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class MainActivity1 extends AppCompatActivity {
    public TabLayout tabLayout;
    private static final String TAG = "MainAct1";
    public FloatingActionButton openCam;
    public static ArrayList<String> contactsName;
    public static ArrayList<String> contactsNum;
    public static int contactsTotal;
    public static int isopened = 0;
    public BroadcastReceiver receiver;
    private EditText search;
    public static SharedPreferences sref;
    public static boolean hasEntered;
    public InputStream isPerson;
    private boolean b;
    public int totalCardNum;
    private DownloadManager dm;
    private long enqueue;
    public static String urlTrainedDataSet;
    public ViewPager viewPager;
    public int contactPerm = 0;
    public int cameraPerm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main1);

        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        5);
                Log.d(TAG, "contactsPerm");
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.INTERNET},
                        1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                        2);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        3);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        4);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                        6);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                        7);
            }
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        b = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
        if (!b) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();

            //this is a pointer to the user

            ParseUser user = ParseUser.getCurrentUser();
            installation.put("user", user);
            installation.saveInBackground();

            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("businesscard.dhruv.businesscardscanner.MainActivity1"));
            // doing the cloud backup every time app connected to net
        }

        sref = MainActivity1.this.getSharedPreferences("entered", 0);
        SharedPreferences.Editor editor = sref.edit();

        if (sref.getBoolean("entered", false) == false) {
            Log.d(TAG, "first Time installation");
            Uri uri;
            uri = Uri.parse("https://www.dropbox.com/s/sdwvyelq68q012y/eng.traineddata?dl=1");
            // download vala code here
            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Language = English")
                    .setDescription("To operate OCR on wnglish language.")
                    .setDestinationInExternalPublicDir(Environment.getExternalStorageState(),
                            "tessdata/eng.traineddata");
            enqueue = dm.enqueue(request);

            hasEntered = false;
            editor.putBoolean("entered", true);
            editor.apply();
        } else {
            hasEntered = true;
        }

        BroadcastReceiver receiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "onRecieve for downloading");
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long download = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int coloumnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(coloumnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            urlTrainedDataSet = uriString;

//                            File from = new File(uriString);
//                            File to = new File(Environment.getExternalStorageDirectory().toString() + "/BusinessCardScanner/tessdata/eng.traineddata");
////                            File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kaic2/imagem.jpg");
//                            from.renameTo(to);

                            SharedPreferences pref = context.getSharedPreferences("engDataSet", 0);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("dataSetUrl", uriString);
                            edit.commit();
                            Log.d(TAG, "uriString: " + uriString);
                            //  --------------->>>>>>>>>>>>>>>>       THIS IS THE DOWNLOADED AUDIO URI       <<<<<<<------
                        }
                    }
                }
            }
        };

        registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
//                progressDialog.dismiss();
                //show a toast message if the Sinch
                //service failed to start
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };


        contactsName = new ArrayList<>();
        contactsNum = new ArrayList<>();

//        search = (EditText) findViewById(R.id.edt_search);

        openCam = (FloatingActionButton) findViewById(R.id.fab_open_cam);
        openCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                3);
                    }
                }

                SharedPreferences pref = v.getContext().getSharedPreferences("AllCards", 0);
                int totalCards = pref.getInt("CardNo", 0);
                if (totalCards >= 10) {
                    // do not allow access SHOW PAYMENT DETAILS
                } else {
                    Intent i = new Intent(MainActivity1.this, MainActivity.class);
                    startActivity(i);
                    MainActivity1.this.finish();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Business Card Scanner");
        toolbar.setSubtitle("");

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addTab(tabLayout.newTab().setText("Chats"));    //0
        tabLayout.addTab(tabLayout.newTab().setText("Cards"));      //1
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));      //2

        Log.d(TAG, "initAdapter");
        viewPager = (ViewPager) findViewById(R.id.pager);
        Log.d(TAG, "initAdapter1");

        Log.d(TAG, "getSupport: " + this.getSupportFragmentManager() + "\n" + tabLayout.getTabCount());
        final PageAdapter adapter = new PageAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        SharedPreferences pref = this.getSharedPreferences("perms", 0);
        int aContact = pref.getInt("contact", 0);
        int aCamera = pref.getInt("camera", 0);
        if (aCamera == 0 || aContact == 0) {
            PageAdapterTillPerm adapterTillPerm = new PageAdapterTillPerm(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapterTillPerm);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
        Log.d(TAG, "setAdapter");

        viewPager.setCurrentItem(1);
        tabLayout.getTabAt(0).setText("CHATS");
        tabLayout.getTabAt(1).setText("CARDS");
        tabLayout.getTabAt(2).setText("CONTACTS");

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);
//        new getContacts().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        SharedPreferences pref = this.getSharedPreferences("perms", 0);
        SharedPreferences.Editor editor = pref.edit();

        switch (requestCode) {
            case 5: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "ContactsPermRec");
                    final PageAdapter adapter = new PageAdapter
                            (getSupportFragmentManager(), tabLayout.getTabCount());
                    viewPager.setAdapter(adapter);
                    tabLayout.setupWithViewPager(viewPager);
                    viewPager.setCurrentItem(1);
                    editor.putInt("contact", 1);
                    contactPerm = 1;
                    tabLayout.getTabAt(0).setText("CHATS");
                    tabLayout.getTabAt(1).setText("CARDS");
                    tabLayout.getTabAt(2).setText("CONTACTS");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    editor.putInt("contact", 0);
                    contactPerm = 0;

                    new AlertDialog.Builder(this).setTitle("").setMessage(("Please enable Contacts permissions in the phone settings to access the Instant messaging services"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            }).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }

            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    final PageAdapter adapter = new PageAdapter
//                            (getSupportFragmentManager(), tabLayout.getTabCount());
//                    viewPager.setAdapter(adapter);
//                    tabLayout.setupWithViewPager(viewPager);
//                    viewPager.setCurrentItem(1);
                    editor.putInt("camera", 1);
                    cameraPerm = 1;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    editor.putInt("camera", 0);
                    cameraPerm = 0;
                    new AlertDialog.Builder(this).setTitle("").setMessage(("Please enable Camera permissions in the phone settings to enable the card scanning feature"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            }).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                editor.commit();
                return;
            }

            case 4: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    final PageAdapter adapter = new PageAdapter
//                            (getSupportFragmentManager(), tabLayout.getTabCount());
//                    viewPager.setAdapter(adapter);
//                    tabLayout.setupWithViewPager(viewPager);
//                    viewPager.setCurrentItem(1);
                    editor.putInt("writeData", 1);
                    cameraPerm = 1;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    editor.putInt("writeData", 0);
                    cameraPerm = 0;
                    new AlertDialog.Builder(this).setTitle("").setMessage(("Please enable access to access to Sd Card permissions to enable the card scanning feature"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            }).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                if (contactPerm == 1) {
                    final PageAdapter adapter = new PageAdapter
                            (getSupportFragmentManager(), tabLayout.getTabCount());
                    viewPager.setAdapter(adapter);
                    tabLayout.setupWithViewPager(viewPager);
                    viewPager.setCurrentItem(1);
                    tabLayout.getTabAt(0).setText("CHATS");
                    tabLayout.getTabAt(1).setText("CARDS");
                    tabLayout.getTabAt(2).setText("CONTACTS");
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
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

        public String orgfind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
                is = new FileInputStream(
                        "/storage/emulated/0/en-ner-organization.bin");

                Log.d(TAG, "inputS: " + is);
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
                // System.out.println(Tokens[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                Log.d(TAG, "inputStream: ");
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//        Log.d(TAG,"inputStream: "+sb.toString());
            return sb.toString();
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    int back = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class cloudBackup extends AsyncTask<Void, Void, Void> {
        ParseUser user = ParseUser.getCurrentUser();
        String userName = user.getUsername();
        String userMail = user.getEmail();

        @Override
        protected Void doInBackground(Void... voids) {

            SharedPreferences pref = MainActivity1.this.getSharedPreferences("AllCards", 0);
            if (pref.contains("CardNo")) {
                totalCardNum = pref.getInt("CardNo", 0);
                String cardPhoto[] = new String[totalCardNum];

                for (int i = 1; i <= totalCardNum; i++) {
                    cardPhoto[i] = pref.getString("Card" + i + "Photo", null);
                    byte[] uploadThis = Base64.decode(cardPhoto[i], Base64.DEFAULT);
                }
            }
            return null;
        }
    }
}