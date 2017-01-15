package businesscard.dhruv.businesscardscanner;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lapism.searchview.SearchView;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main1);

        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        sref = MainActivity1.this.getSharedPreferences("entered", 0);
        SharedPreferences.Editor editor = sref.edit();

        if (sref.getBoolean("entered", false) == false) {
            hasEntered = false;
            editor.putBoolean("entered", true);
            editor.apply();
        } else {
            hasEntered = true;
        }

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

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        //this is a pointer to the user

        ParseUser user = ParseUser.getCurrentUser();
        installation.put("user", user);
        installation.saveInBackground();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("businesscard.dhruv.businesscardscanner.MainActivity1"));

        contactsName = new ArrayList<>();
        contactsNum = new ArrayList<>();

//        search = (EditText) findViewById(R.id.edt_search);

        openCam = (FloatingActionButton) findViewById(R.id.fab_open_cam);
        openCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity1.this, MainActivity.class);
                startActivity(i);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addTab(tabLayout.newTab().setText("My Card"));    //0
        tabLayout.addTab(tabLayout.newTab().setText("Cards"));      //1
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));      //2

        Log.d(TAG, "initAdapter");
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        Log.d(TAG, "initAdapter1");

        Log.d(TAG, "getSupport: " + this.getSupportFragmentManager() + "\n" + tabLayout.getTabCount());
        final PageAdapter adapter = new PageAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

//        search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////               adapter.getFilter().filter(s);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        Log.d(TAG, "setAdapter");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        tabLayout.getTabAt(0).setText("My Card");
        tabLayout.getTabAt(1).setText("Cards");
        tabLayout.getTabAt(2).setText("Chats");

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);

//        new getContacts().execute();
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
        if (back == 0) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            back++;
        } else {
            onDestroy();
        }
        super.onBackPressed();
    }
}