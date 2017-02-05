package businesscard.dhruv.businesscardscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class ShowCardDetails extends AppCompatActivity {

    public static final String TAG = "ShowCardFragment";
    private CoordinatorLayout cord;
    private RecyclerView rvEntryDetails;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imgScanned;
    public static int imageResource;
    public int pos = 0;
    public static Bitmap bitmap;
    ImageView fullImg;
    FloatingActionButton edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card_details);

        cord = (CoordinatorLayout) findViewById(R.id.cord2);
        Intent i = getIntent();
        pos = i.getIntExtra("CardPosition", 0);
        int NDIB = i.getIntExtra("nobodyDIB", 0);
        if (NDIB == 1) {
            Snackbar snackbar = Snackbar
                    .make(cord, "Contact saved in PhoneBook", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        Log.d(TAG, "pos:: " + pos);
        edit = (FloatingActionButton) findViewById(R.id.fab_edit_contact);
        fullImg = (ImageView) findViewById(R.id.view_full);
        imgScanned = (ImageView) findViewById(R.id.img_scanned_card);
        rvEntryDetails = (RecyclerView) findViewById(R.id.rv_entry_details);
        rvEntryDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvEntryDetails.setLayoutManager(layoutManager);
        adapter = new ShowDetailsRVAdapter(getDataSet(), ShowCardDetails.this);
        rvEntryDetails.setAdapter(adapter);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowCardDetails.this, EditCardActivity.class);
                i.putExtra("CardNo", pos);
                startActivity(i);
                ShowCardDetails.this.finish();
            }
        });

        SharedPreferences preferences = this.getSharedPreferences("AllCards", 0);
        String cardBitmapString = preferences.getString("CardBitmap" + pos, "");
        byte[] encodeByte = Base64.decode(cardBitmapString, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        fullImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowCardDetails.this, FullImage.class);
                startActivity(i);
            }
        });

        imgScanned.setImageBitmap(bitmap);

        imgScanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowCardDetails.this, FullImage.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ShowCardDetails.this, MainActivity1.class);
        startActivity(i);
        ShowCardDetails.this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ShowDetailsRVAdapter) adapter).setOnItemClickListener(new ShowDetailsRVAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<DataObjectCardEntry> getDataSet() {
        ArrayList result = new ArrayList<DataObjectCardEntry>();
        // fill the result list properly

        SharedPreferences pref = this.getSharedPreferences("AllCards", 0);
        ++pos;      //check this one
        int cardEnt = pref.getInt("CardEnt" + pos, 0);
        for (int i = 0; i <= cardEnt; i++) {
            String entType = pref.getString("Card" + pos + "EntryType" + i, "");
            String entDet = pref.getString("Card" + pos + "EntryDetail" + i, "");
            DataObjectCardEntry cardEntry = new DataObjectCardEntry(entType, entDet);
            result.add(cardEntry);
        }
        return result;
    }
}