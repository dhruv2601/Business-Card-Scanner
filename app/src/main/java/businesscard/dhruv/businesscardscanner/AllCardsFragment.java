package businesscard.dhruv.businesscardscanner;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private ArrayList<CardObject1> getDataSet() {
        ArrayList results = new ArrayList<CardObject>();
        for (int index = 0; index < 2; index++) {
            CardObject1 obj = new CardObject1(0, "Dhruv Rathi", "CEO Bitch", "BCScanner"); // make a map of images and the service and provide that here
            Log.d(TAG, "index:: " + index);
            results.add(index, obj);
        }
        return results;
    }

}
