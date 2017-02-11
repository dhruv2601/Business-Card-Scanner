package businesscard.dhruv.businesscardscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditCardActivity extends AppCompatActivity {

    private static final String TAG = "EditCardAct";
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

    public Handler mHandler;
    private de.hdodenhof.circleimageview.CircleImageView imgPersonImg;
    private Button addAnotherField;

    public static final int PICK_IMAGE_REQUEST = 2601;
    private ArrayList<DataObjectCardEntry> result;
    public static String type[] = new String[30];
    public static String desc[] = new String[30];
    private String imageUri;
    private CoordinatorLayout cord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_card);

        cord = (CoordinatorLayout) findViewById(R.id.cord1);
        Intent intent = new Intent();
        intent = getIntent();
        cardNo = intent.getIntExtra("CardNo", 0);

        addAnotherField = (Button) findViewById(R.id.btn_add_detail);
        imgPersonImg = (CircleImageView) findViewById(R.id.img_profile);
        imgScanned = (ImageView) findViewById(R.id.img_scanned_card);
        rvEntryDetails = (RecyclerView) findViewById(R.id.rv_entry_details);
        rvEntryDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvEntryDetails.setLayoutManager(layoutManager);
        rvEntryDetails.setNestedScrollingEnabled(false);
        adapter = new EntryDetailsRVAdapter(getDataSet(), EditCardActivity.this);
        rvEntryDetails.setAdapter(adapter);

        fabSaveContact = (FloatingActionButton) findViewById(R.id.fab_save_contact);
//        Toast.makeText(this, "Click on TICKS on the right to save entries.", Toast.LENGTH_SHORT).show();

        Snackbar snackbar = Snackbar
                .make(cord, "Select the ticks on right to save entries", Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();

        fabSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // edit text mn jo data edit hua hai save that

                int i = 0;
                SharedPreferences pref = EditCardActivity.this.getSharedPreferences("AllCards", 0);
                SharedPreferences.Editor editor = pref.edit();
                int totalCards;
                totalCards = cardNo;

                for (i = 0; i < result.size(); i++) {
                    editor.putString("Card" + String.valueOf(totalCards) + "EntryType" + i, result.get(i).getEntryType());
                    editor.putString("Card" + String.valueOf(totalCards) + "EntryDetail" + i, result.get(i).getEntryDetails());
                }

//                ++totalCards;
                editor.putInt("CardEnt" + totalCards, result.size() - 1);
                editor.putInt("CardNo", totalCards);
                editor.commit();
                Log.d(TAG, "pref:: " + pref.getString("Card" + String.valueOf(totalCards) + "EntryType" + 1, "null hai bc"));

                Toast.makeText(EditCardActivity.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), MainActivity1.class);
                intent.putExtra("CardPosition", totalCards);
                startActivity(intent);
                EditCardActivity.this.finish();
            }
        });

        addAnotherField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(cord, "Select the ticks on right to save entries", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                int yet = 0;

                DataObjectCardEntry cardEntry = new DataObjectCardEntry("", "");

                result.add(cardEntry);
                for (int i = 0; i < result.size(); i++) {
                    Log.d(TAG, "resultArray: " + result.get(i).getEntryDetails());
                }

                adapter.notifyDataSetChanged();
            }
        });
        SharedPreferences preferences = this.getSharedPreferences("SavedCards", 0);
        imageUri = preferences.getString("ImageUri" + cardNo, "");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        cardBitmap = BitmapFactory.decodeFile(String.valueOf(imageUri), options);

        imgScanned.setImageBitmap(cardBitmap);

    }

    private ArrayList<DataObjectCardEntry> getDataSet() {
        ArrayList<DataObjectCardEntry> result = new ArrayList<DataObjectCardEntry>();
        SharedPreferences pref = EditCardActivity.this.getSharedPreferences("AllCards", 0);
        int size = pref.getInt("CardEnt" + cardNo, 0);
        for (int i = 0; i <= size; i++) {
            String type = pref.getString("Card" + String.valueOf(cardNo) + "EntryType" + i, "");
            String det = pref.getString("Card" + String.valueOf(cardNo) + "EntryDetail" + i, "");

            DataObjectCardEntry data = new DataObjectCardEntry(type, det);
            result.add(data);
        }
        this.result = result;
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((EntryDetailsRVAdapter) adapter).setOnItemClickListener(new EntryDetailsRVAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });
    }
}
