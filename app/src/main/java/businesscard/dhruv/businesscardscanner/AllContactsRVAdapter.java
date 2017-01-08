package businesscard.dhruv.businesscardscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dhruv on 3/1/17.
 */

public class AllContactsRVAdapter extends RecyclerView
        .Adapter<AllContactsRVAdapter
        .DataObjectHolder> {

    public static final String TAG = "myRecViewAdapter";
    private ArrayList<CardObjectContacts> mCardSet;
    private static AllContactsRVAdapter.MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView imageDrawable;
        TextView txtName;
        TextView txtNum;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageDrawable = (ImageView) itemView.findViewById(R.id.img_card);
            txtName = (TextView) itemView.findViewById(R.id.txt_contact_name);
            txtNum = (TextView) itemView.findViewById(R.id.txt_contact_num);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {

            Log.d(TAG, "contactTouched: " + MainActivity1.contactsName.get(getAdapterPosition()) + "\n" + MainActivity1.contactsNum.get(getAdapterPosition()));

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            final boolean[] b = {false};
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> userList, com.parse.ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < userList.size(); i++) {
                            boolean areSame = PhoneNumberUtils.compare(userList.get(i).getUsername().toString(), MainActivity1.contactsNum.get(getAdapterPosition()));
                            if (areSame == true) {

                                Intent intent = new Intent(v.getContext(), MessagingActivity.class);
                                intent.putExtra("RECIPIENT_ID", userList.get(i).getObjectId());
                                v.getContext().startActivity(intent);

                                Toast.makeText(v.getContext(), MainActivity1.contactsName.get(getAdapterPosition()) + "ABHI AA RHA HAI THAMA RHE", Toast.LENGTH_SHORT).show();
                                b[0] = true;
                                break;
                            }

                            Log.d(TAG, "list: " + userList.get(i).getUsername().toString());
                        }

                        if (b[0] == false) {
//                            Toast.makeText(v.getContext(), MainActivity1.contactsName.get(getAdapterPosition()) + "is not using the app, BULAO USKO", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(v.getContext()).setTitle("").setMessage(MainActivity1.contactsName.get(getAdapterPosition()) + " is currently not using BC Scanner, send them an invite for a quick chat.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    }).show();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);

                            shareIntent.setType("text/html");
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, MainActivity1.contactsName.get(getAdapterPosition()) + " have you tried the BC Scanner app?" + "\n");   // instead send the description here

                            shareIntent.putExtra(Intent.EXTRA_TEXT, MainActivity1.contactsName.get(getAdapterPosition()) + " have you tried the BC Scanner app?" + "\n" + "I invite you to a quick chat on BC Scanner. Scan all the your business cards and keep a sync and never loose your cards. " + "\n" + "Here will be the download link........I AM BATMAN");
                            v.getContext().startActivity(Intent.createChooser(shareIntent, "Invite to chat"));
                        }
                        else if(e==null)
                        {
                            Log.d(TAG,"exception: "+e);
                        }
                    }
                }
            });

            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(AllContactsRVAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public AllContactsRVAdapter(ArrayList<CardObjectContacts> myCardSet, Context context) {
        mCardSet = myCardSet;
        this.context = context;
    }

    @Override
    public AllContactsRVAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_card_view, parent, false);

        AllContactsRVAdapter.DataObjectHolder dataObjectHolder = new AllContactsRVAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(AllContactsRVAdapter.DataObjectHolder holder, int position) {
        holder.txtName.setText(mCardSet.get(position).getTxtName());
        char ch = mCardSet.get(position).getTxtName().charAt(0);

        Random r = new Random();
        int i = r.nextInt(6);

        switch (i) {
            case 0:
                i = Color.DKGRAY;
                break;
            case 1:
                i = Color.RED;
                break;
            case 2:
                i = Color.CYAN;
                break;
            case 3:
                i = Color.GRAY;
                break;
            case 4:
                i = Color.DKGRAY;
                break;
            default:
                i = Color.BLACK;
                break;
        }

        TextDrawable drawable = TextDrawable.builder()
                .buildRoundRect(String.valueOf(ch).toUpperCase(), i, 100);

        holder.imageDrawable.setImageDrawable(drawable);
        holder.txtNum.setText(mCardSet.get(position).getTxtNum());
    }

    public void addItem(CardObjectContacts cardObject, int index) {
        mCardSet.add(index, cardObject);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mCardSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mCardSet.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}