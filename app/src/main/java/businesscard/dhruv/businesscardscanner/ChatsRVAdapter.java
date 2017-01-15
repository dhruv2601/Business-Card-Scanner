package businesscard.dhruv.businesscardscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
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

public class ChatsRVAdapter extends RecyclerView
        .Adapter<ChatsRVAdapter
        .DataObjectHolder> {

    public static final String TAG = "myRecViewAdapter";
    private ArrayList<CardObjectContacts> mCardSet;
    private static ChatsRVAdapter.MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView imageDrawable;
        TextView txtName;
        TextView txtNum;
        ImageView call;
        ImageView message;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageDrawable = (ImageView) itemView.findViewById(R.id.img_card);
            txtName = (TextView) itemView.findViewById(R.id.txt_contact_name);
            txtNum = (TextView) itemView.findViewById(R.id.txt_contact_num);
            call = (ImageView) itemView.findViewById(R.id.call);
            message = (ImageView) itemView.findViewById(R.id.message);

            call.setVisibility(View.GONE);
            message.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            SharedPreferences pref;
            pref = v.getContext().getSharedPreferences("prevChat", 0);

            String recepName = pref.getString("prevUser" + "Name" + getAdapterPosition(), " ");
            String recepNum = pref.getString("prevUser" + "Num" + getAdapterPosition(), " ");
            String recepId = pref.getString("prevUser" + "Recep" + getAdapterPosition(), " ");
            Log.d(TAG, "name: " + recepName);

            Intent intent = new Intent(v.getContext(), MessagingActivity.class);
            intent.putExtra("RECIPIENT_ID", recepId);
            intent.putExtra("RECIPIENT_NAME", recepName);
            intent.putExtra("RECIPIENT_NUM", recepNum);

            v.getContext().startActivity(intent);

            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ChatsRVAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ChatsRVAdapter(ArrayList<CardObjectContacts> myCardSet, Context context) {
        mCardSet = myCardSet;
        this.context = context;
    }

    @Override
    public ChatsRVAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_card_view, parent, false);

        ChatsRVAdapter.DataObjectHolder dataObjectHolder = new ChatsRVAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ChatsRVAdapter.DataObjectHolder holder, int position) {
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