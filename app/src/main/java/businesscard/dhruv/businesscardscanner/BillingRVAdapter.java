package businesscard.dhruv.businesscardscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
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

public class BillingRVAdapter extends RecyclerView
        .Adapter<BillingRVAdapter
        .DataObjectHolder> {

    public static final String TAG = "myRecViewAdapter";
    private ArrayList<String> mCardSet;
    private static BillingRVAdapter.MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView txtNumCards;
        AppCompatTextView btnCost;

        public DataObjectHolder(View itemView) {
            super(itemView);

            txtNumCards = (TextView) itemView.findViewById(R.id.txt_num_cards);
            btnCost = (AppCompatTextView) itemView.findViewById(R.id.card_rates);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(BillingRVAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public BillingRVAdapter(ArrayList<String> myCardSet) {
        mCardSet = myCardSet;
        this.context = context;
    }

    @Override
    public BillingRVAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_billing, parent, false);

        BillingRVAdapter.DataObjectHolder dataObjectHolder = new BillingRVAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(BillingRVAdapter.DataObjectHolder holder, int position) {
        if (position == 0) {
            holder.txtNumCards.setText("10 Cards");
            holder.btnCost.setText(mCardSet.get(0));
        } else if (position == 1) {
            holder.txtNumCards.setText("25 Cards");
            holder.btnCost.setText(mCardSet.get(1));
        } else if (position == 2) {
            holder.txtNumCards.setText("50 Cards");
            holder.btnCost.setText(mCardSet.get(2));
        } else if (position == 3) {
            holder.txtNumCards.setText("100 Cards");
            holder.btnCost.setText(mCardSet.get(3));
        }
    }
    @Override
    public int getItemCount() {
        return 4;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}