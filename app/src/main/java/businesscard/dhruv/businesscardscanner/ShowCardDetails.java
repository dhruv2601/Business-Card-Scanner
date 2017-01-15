package businesscard.dhruv.businesscardscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class ShowCardDetails extends AppCompatActivity {

    public static final String TAG = "ShowCardFragment";
    private RecyclerView rvEntryDetails;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imgScanned;
    public static int imageResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card_details);
        imgScanned = (ImageView) findViewById(R.id.img_scanned_card);
        rvEntryDetails = (RecyclerView) findViewById(R.id.rv_entry_details);
        rvEntryDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvEntryDetails.setLayoutManager(layoutManager);
        adapter = new EntryDetailsRVAdapter(getDataSet(),ShowCardDetails.this);
        rvEntryDetails.setAdapter(adapter);

        imgScanned.setImageResource(R.drawable.call);       // add the scanned image here

        imgScanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowCardDetails.this, FullImage.class);
                startActivity(i);
            }
        });
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

    private ArrayList<DataObjectCardEntry> getDataSet()
    {
        ArrayList result = new ArrayList<DataObjectCardEntry>();
        // fill the result list properly

        for(int i=0;i<10;i++)
        {
            Log.d(TAG,"dataBeingFilledInCArdDetails");

            DataObjectCardEntry data = new DataObjectCardEntry("Dhruv Rathi"," CEO Bitch");
            result.add(data);
        }
        return result;
    }
}
