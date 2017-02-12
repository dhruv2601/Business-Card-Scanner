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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Html;
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

//import opennlp.tools.namefind.NameFinderME;
//import opennlp.tools.namefind.TokenNameFinderModel;
//import opennlp.tools.tokenize.Tokenizer;
//import opennlp.tools.tokenize.TokenizerME;
//import opennlp.tools.tokenize.TokenizerModel;
//import opennlp.tools.util.InvalidFormatException;
//import opennlp.tools.util.Span;

public class MainActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    public boolean b;
    public int totalCardNum;
    private DownloadManager dm;
    private long enqueue;
    public static String urlTrainedDataSet;
    public ViewPager viewPager;
    public int contactPerm = 0;
    public int cameraPerm = 0;

    public ActionBarDrawerToggle toggle;

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

        SharedPreferences pref1 = this.getSharedPreferences("cardMng", 0);
        SharedPreferences.Editor editor1 = pref1.edit();
        int tempNo1 = pref1.getInt("tempNo", 0);
        int cardsLeft1 = pref1.getInt("cardsLeft", 0);
        Log.d(TAG,"cardInfo: "+tempNo1+"\n"+cardsLeft1);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        b = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
        if (b) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();

            //this is a pointer to the user

            ParseUser user = null;
            try {
                user = ParseUser.getCurrentUser().fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            installation.put("user", user);
            installation.saveInBackground();

//            int cardsLeft = user.getInt("cardsLeft");
            SharedPreferences pref = this.getSharedPreferences("cardMng", 0);
            SharedPreferences.Editor editor = pref.edit();
            int tempNo = pref.getInt("tempNo", 0);
            int cardsLeft = pref.getInt("cardsLeft", 0);
            cardsLeft -= tempNo;
            Log.d(TAG,"cardsLeft: "+cardsLeft+"\n"+tempNo);
            tempNo = 0;
            editor.putInt("tempNo", tempNo);
            editor.putInt("cardsLeft", cardsLeft);
            editor.commit();

            user.put("cardsLeft", cardsLeft);
            user.saveInBackground();
            // doing the cloud backup every time app connected to net
        }

        SharedPreferences pref2 = this.getSharedPreferences("engDataSet", 0);
        SharedPreferences.Editor edit = pref2.edit();
        edit.putString("dataSetUrl", "file:///storage/emulated/0/mounted/tessdata/eng.traineddata");
        edit.commit();

//        file:///storage/emulated/0/mounted/tessdata/eng.traineddata

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
                    .setDescription("To operate OCR on english language.")
                    .setDestinationInExternalPublicDir(Environment.getExternalStorageState(),
                            "tessdata/eng.traineddata");
            enqueue = dm.enqueue(request);

            hasEntered = false;
            editor.putBoolean("entered", true);
            editor.apply();

//            Intent i = new Intent(MainActivity1.this, ActivityIntro.class);
//            startActivity(i);
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
                            edit.putString("downloaded", "1");
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
//        if (aCamera == 0 || aContact == 0) {
//            PageAdapterTillPerm adapterTillPerm = new PageAdapterTillPerm(getSupportFragmentManager(), tabLayout.getTabCount());
//            viewPager.setAdapter(adapterTillPerm);
//            tabLayout.setupWithViewPager(viewPager);
//        } else {
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        }

        Log.d(TAG, "setAdapter");

        viewPager.setCurrentItem(1);
        tabLayout.getTabAt(0).setText("MY CARD");
        tabLayout.getTabAt(1).setText("CARDS");
        tabLayout.getTabAt(2).setText("SETTINGS");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        toggle.setHomeAsUpIndicator(drawable);

//        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
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
//                    final PageAdapter adapter = new PageAdapter
//                            (getSupportFragmentManager(), tabLayout.getTabCount());
//                    viewPager.setAdapter(adapter);
//                    tabLayout.setupWithViewPager(viewPager);
//                    viewPager.setCurrentItem(1);
//                    editor.putInt("contact", 1);
//                    contactPerm = 1;
//                    tabLayout.getTabAt(0).setText("CHATS");
//                    tabLayout.getTabAt(1).setText("CARDS");
//                    tabLayout.getTabAt(2).setText("CONTACTS");

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if (id == R.id.usingTheApp) {
//            Log.d(TAG, "onOptionsItemSelected: action_settings");
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
            //Add an activity
        } else if (id == R.id.buy_cards) {
            if (b) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                b = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
                if (b) {
                    Intent i = new Intent(this, BillingActLib.class);
                    startActivity(i);
                } else {
                    startActivityForResult(new Intent(
                            Settings.ACTION_WIFI_SETTINGS), 0);
                }
            } else {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        } else if (id == R.id.invite) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/html");
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, ("Have you tried the Business Card Scanner app?" + "\n"));   // instead send the description here

            shareIntent.putExtra(Intent.EXTRA_TEXT, " Have you tried the BC Scanner app?" + "\n" + "Scan all the your business cards digitally and never loose your cards. " + "\n" + "https://play.google.com/store/apps/details?id=businesscard.dhruv.businesscardscanner");
            this.startActivity(Intent.createChooser(shareIntent, "Invite to use BC Scanner"));
        } else if (id == R.id.support) {
            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "dhruvrathi15@gmail.com", null));
            startActivity(Intent.createChooser(i, "Send Email..."));
        } else if (id == R.id.aboutMe) {
            Intent i = new Intent(MainActivity1.this, AboutDeveloper.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                Intent i = new Intent(this, BillingActLib.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "No internet connection available.", Toast.LENGTH_SHORT).show();
                //write your code for any kind of network calling to fetch data
            }
        }
    }
}