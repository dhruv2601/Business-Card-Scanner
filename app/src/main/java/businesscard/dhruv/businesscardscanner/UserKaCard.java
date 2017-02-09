package businesscard.dhruv.businesscardscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class UserKaCard extends Fragment {

    private View view;
    private FloatingActionButton scanOwnCard;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    SharedPreferences pref = getActivity().getSharedPreferences("OwnCard", 0);
                    SharedPreferences.Editor editor = pref.edit();

                    // lets keep three states of prefs such that:
                    // 1) not involved(0)
                    // 2) initialised:::(1)
                    // 3) finalised:::(2)
                    editor.putInt("ownCardStatus", 1);
                    editor.commit();
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
        CardObject1 obj = new CardObject1(0, name, "", company); // make a map of images and the service and provide that here
        results.add(obj);
        return results;
    }
}