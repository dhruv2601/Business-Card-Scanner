package businesscard.dhruv.businesscardscanner;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.util.ArrayList;

public class AllContacts extends Fragment {

    private static final String TAG = "AllContacts";
    private View view;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public int totalContacts;
    public ArrayList<String> contactsName;
    public ArrayList<String> contactsNum;
    public int contactsTotal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new getContacts().execute();
//        new checkContactsLen().execute();
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
            mAdapter = new AllContactsRVAdapter(getDataSetNakli(), view.getContext());
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AllContactsRVAdapter) mAdapter).setOnItemClickListener(new AllContactsRVAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // handle on item click on each card item i.e. open each card to show details
            }
        });

    }

    // Save each card info into shared prefs and build the arrayList using them every time and in case they are empty sync up to check if the user has deleted the data

    private ArrayList<CardObjectContacts> getDataSet() {
        ArrayList results = new ArrayList<CardObjectContacts>();
        for (int index = 0; index < MainActivity1.contactsTotal; index++) {
            CardObjectContacts obj = new CardObjectContacts(MainActivity1.contactsName.get(index), MainActivity1.contactsNum.get(index)); // make a map of images and the service and provide that here
//            Log.d(TAG, "index:: " + index);
            results.add(index, obj);
        }
        return results;
    }

    private ArrayList<CardObjectContacts> getDataSetNakli() {
        ArrayList results = new ArrayList<CardObjectContacts>();
        for (int index = 0; index < 1; index++) {
            CardObjectContacts obj = new CardObjectContacts("Loading....", "Loading"); // make a map of images and the service and provide that here
            Log.d(TAG, "index:: " + index);
            results.add(index, obj);
        }
        return results;
    }

    public class getContacts extends AsyncTask {

        int i = 0;

        @Override
        protected void onPreExecute() {
            contactsName = new ArrayList<>();
            contactsNum = new ArrayList<>();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            ContentResolver cr = getActivity().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            contactsTotal = cur.getCount();
            String prevName = "";
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (prevName.equals(name)) {
                                // Alexis Texas :> 00
                            } else {
                                prevName = name;
                                contactsName.add(i, name);
                                contactsNum.add(i, phoneNo);
                                i++;
                            }
                        }
                        pCur.close();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            MainActivity1.contactsName = contactsName;
            MainActivity1.contactsNum = contactsNum;
            MainActivity1.contactsTotal = i;

            mAdapter = new AllContactsRVAdapter(getDataSet(), view.getContext());
            mRecyclerView.setAdapter(mAdapter);
            super.onPostExecute(o);
        }
    }
}
