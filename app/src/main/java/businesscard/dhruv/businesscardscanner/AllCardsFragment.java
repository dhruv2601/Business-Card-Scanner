package businesscard.dhruv.businesscardscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AllCardsFragment extends Fragment {

    private static final String TAG = "AllCardsFragment";
    private View view;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public FloatingActionButton openCam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.activity_all_cards_fragment, container, false);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.all_cards_list_rv);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new allCardRecyclerViewAdapter(getDataSet(), view.getContext());

            openCam = (FloatingActionButton) view.findViewById(R.id.fab_open_cam);
            openCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    SharedPreferences pref1 = v.getContext().getSharedPreferences("engDataSet", 0);
//                    String isThere = pref1.getString("downloaded", "0");
//                    if (isThere.equals("0")) {
//                        Toast.makeText(v.getContext(), "English Data is downloading, please wait...", Toast.LENGTH_LONG).show();
//                    } else {

                    SharedPreferences pref = v.getContext().getSharedPreferences("cardMng",0);
                    int cardsLeft = pref.getInt("cardsLeft",0);
                    int tempNo = pref.getInt("tempNo",0);
                    cardsLeft-=tempNo;

//                    SharedPreferences pref = v.getContext().getSharedPreferences("AllCards", 0);
//                    int totalCards = pref.getInt("CardNo", 0);
                    if (cardsLeft < 0) {
                        new AlertDialog.Builder(v.getContext()).setTitle("More cards needed").setMessage("You have exhausted the current card numbers limit.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                }).show();
                        Intent i = new Intent(getActivity(), BillingActLib.class);
                        startActivity(i);
                        // do not allow access SHOW PAYMENT DETAILS
                    } else {
                        Intent i = new Intent(getContext(), MainActivity.class);
                        startActivity(i);
                        AllCardsFragment.this.getActivity().finish();
//                    Intent i = new Intent(MainActivity1.this, BillingActLib.class);
//                    startActivity(i);
                    }
//                    }
                }
            });

            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((allCardRecyclerViewAdapter) mAdapter).setOnItemClickListener(new allCardRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // handle on item click on each card item i.e. open each card to show details

            }
        });
    }

    // Save each card info into shared prefs and build the arrayList using them every time and in case they are empty sync up to check if the user has deleted the data
    public ArrayList<CardObject1> getDataSet() {
        ArrayList results = new ArrayList<CardObject1>();
        Log.d(TAG, "insideAllCardFrag");
        SharedPreferences pref = getContext().getSharedPreferences("AllCards", 0);
        int x = pref.getInt("CardNo", 0);
        if (x == 0) {
            Log.d(TAG, "noCards");
            CardObject1 object1 = new CardObject1(0, "John Doe", "CEO", "BC Scanner");
            results.add(object1);
        } else {
            Log.d(TAG, "cardsExists");
            int totalCards = pref.getInt("CardNo", 1);
            int index = 0;

            for (int i = 1; i <= totalCards; i++) {
                int totalEnt = pref.getInt("CardEnt" + i, 0);
                String name = "";
                String company = "";

                for (int j = 0; j <= totalEnt; j++) {
                    String entType = pref.getString("Card" + String.valueOf(i) + "EntryType" + j, "");
                    if (entType.equals("Name")) {
                        name = pref.getString("Card" + String.valueOf(i) + "EntryDetail" + j, "");
                    } else if (entType.equals("Company")) {
                        company = pref.getString("Card" + String.valueOf(i) + "EntryDetail" + j, "");
                    }
                }
                CardObject1 obj = new CardObject1(0, name, "", company); // make a map of images and the service and provide that here
                results.add(index++, obj);
            }
        }
        return results;
    }
}