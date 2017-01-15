package businesscard.dhruv.businesscardscanner;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    private int totalChats;
    private SharedPreferences pref;
    private View view;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public String recepId[] = new String[200];
    public String recepName[] = new String[200];
    public String recepNum[] = new String[200];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_all_contacts, container, false);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.all_contacts_rv);
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new ChatsRVAdapter(getDataSet(), view.getContext());
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    private ArrayList<CardObjectContacts> getDataSet() {
        ArrayList<CardObjectContacts> result = new ArrayList<CardObjectContacts>();
        pref = getContext().getSharedPreferences("prevChat", 0);
        totalChats = pref.getInt("numUser", 0);
        for (int i = 1; i < totalChats; i++) {
            recepName[i] = pref.getString("prevUser" + "Name" + i, " ");
            recepNum[i] = pref.getString("prevUser" + "Num" + i, " ");
            recepId[i] = pref.getString("prevUser" + "Recep" + i, " ");
            Log.d(TAG,"name: "+recepName[i]);

            CardObjectContacts contacts = new CardObjectContacts(recepName[i], recepNum[i]);
            result.add(contacts);
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ChatsRVAdapter) mAdapter).setOnItemClickListener(new ChatsRVAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // handle on item click on each card item i.e. open each card to show details

            }
        });

    }

}
