package businesscard.dhruv.businesscardscanner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.Parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    public static int isopened=0;

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

        boolean areSame = PhoneNumberUtils.compare("+91-9871117537","987 111 7537");
        Log.d(TAG,"areSame: "+areSame);

        boolean areSame2 = PhoneNumberUtils.compare("123456789","123456789");
        Log.d(TAG,"areSame2222: "+areSame2);

        contactsName = new ArrayList<>();
        contactsNum = new ArrayList<>();

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
        PageAdapter adapter = new PageAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        Log.d(TAG, "setAdapter");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        tabLayout.getTabAt(0).setText("My Card");
        tabLayout.getTabAt(1).setText("Cards");
        tabLayout.getTabAt(2).setText("Chats");

        tikaOpenIntro toi = new tikaOpenIntro();

        String cnt;

        cnt = "John is planning to specialize in Electrical Engineering in UC Berkley and pursue a career with IBM.";

        toi.tokenization(cnt);

        String names = toi.namefind(toi.Tokens);
        String org = toi.orgfind(toi.Tokens);

        Log.d(TAG, "person name is : " + names);
        Log.d(TAG, "organization name is: " + org);

//        new getContacts().execute();
    }

    public class tikaOpenIntro {

        public String Tokens[];

//        {
//            tikaOpenIntro toi = new tikaOpenIntro();
//
//            String cnt;
//
//            cnt = "John is planning to specialize in Electrical Engineering in UC Berkley and pursue a career with IBM.";
//
//            toi.tokenization(cnt);
//
//            String names = toi.namefind(toi.Tokens);
//            String org = toi.orgfind(toi.Tokens);
//
//            Log.d(TAG,"person name is : " + names);
//            Log.d(TAG,"organization name is: " + org);
//        }

        public String namefind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
                is = new FileInputStream("/home/dhruv/Downloads/en-ner-person.bin");
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
                        "/home/rahul/opennlp/model/en-ner-organization.bin");
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
                is = new FileInputStream("/home/rahul/opennlp/model/en-token.bin");
                tm = new TokenizerModel(is);
                Tokenizer tz = new TokenizerME(tm);
                Tokens = tz.tokenize(tokens);
                // System.out.println(Tokens[1]);
            } catch (IOException e) {
                e.printStackTrace();
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
        if (back == 0) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            back++;
        } else {
            onDestroy();
        }
        super.onBackPressed();
    }
}