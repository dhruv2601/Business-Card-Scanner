package businesscard.dhruv.businesscardscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class UserKaCard extends Fragment {

    private static final String TAG = "USerCard";
    private View view;
    private FloatingActionButton scanOwnCard;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppCompatButton txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_user_ka_card, container, false);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.own_card_rv);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            final SharedPreferences VSR = getActivity().getSharedPreferences("OwnCard", 0);
            int cardThere = VSR.getInt("cardThere", 0);
//            if (cardThere == 0) {
//                mAdapter = new allCardRecyclerViewAdapter(getDataSetEmpty(), view.getContext());
//            } else {
            mAdapter = new allCardRecyclerViewAdapter(getDataSet(), view.getContext());
//            }
            scanOwnCard = (FloatingActionButton) view.findViewById(R.id.scan_own_card);
            scanOwnCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences pref1 = view.getContext().getSharedPreferences("engDataSet", 0);
                    String isThere = pref1.getString("downloaded", "0");
                    if (isThere.equals("0")) {
                        Toast.makeText(view.getContext(), "English Data is downloading, please wait...", Toast.LENGTH_LONG).show();
                    } else {

                        SharedPreferences pref = view.getContext().getSharedPreferences("cardMng", 0);
                        int cardsLeft = pref.getInt("cardsLeft", 0);
                        int tempNo = pref.getInt("tempNo", 0);
                        cardsLeft -= tempNo;

//                    SharedPreferences pref = v.getContext().getSharedPreferences("AllCards", 0);
//                    int totalCards = pref.getInt("CardNo", 0);
                        if (cardsLeft < 0) {
                            new AlertDialog.Builder(view.getContext()).setTitle("More cards needed").setMessage("You have exhausted the current card numbers limit.")
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
                            UserKaCard.this.getActivity().finish();
//                    Intent i = new Intent(MainActivity1.this, BillingActLib.class);
//                    startActivity(i);
                        }
                    }
                }
            });

            txt = (AppCompatButton) view.findViewById(R.id.view_card);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ShowCardDetails.class);
                    startActivity(i);
                }
            });

            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    private ArrayList<CardObject1> getDataSetEmpty() {
        return null;
    }

    private ArrayList<CardObject1> getDataSet() {
        String name = "";
        String company = "";
        ArrayList results = new ArrayList<CardObject1>();

        final SharedPreferences VSR = getActivity().getSharedPreferences("OwnCard", 0);
        int entries = VSR.getInt("CardEnt", 0);
        for (int i = 0; i <= entries; i++) {
            String entType = VSR.getString("Card" + "EntryType" + i, "");
            if (entType.equals("Name")) {
                name = VSR.getString("Card" + "EntryDetail" + i, "");
            } else if (entType.equals("Company")) {
                company = VSR.getString("Card" + "EntryDetail" + i, "");
            }
        }
        Log.d(TAG, "name+company : " + name + "\n" + company);

        CardObject1 obj = new CardObject1(0, name, "", company); // make a map of images and the service and provide that here
        results.add(obj);
        return results;
    }
}