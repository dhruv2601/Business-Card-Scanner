package businesscard.dhruv.businesscardscanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dhruv on 27/1/17.
 */

public class ShowDetailsRVAdapter extends RecyclerView
        .Adapter<ShowDetailsRVAdapter
        .DataObjectHolder> {
    public static final String TAG = "ShowDetailsRVAdapter";
    public static ArrayList<DataObjectCardEntry> mCardSet;
    private static ShowDetailsRVAdapter.MyClickListener myClickListener;
    public Context context;

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView imageEntryType;
        public TextView txtEntryType;
        public TextView txtEntryDetails;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageEntryType = (ImageView) itemView.findViewById(R.id.entry_img);
            txtEntryType = (TextView) itemView.findViewById(R.id.entry_type);
            txtEntryDetails = (TextView) itemView.findViewById(R.id.entry_detail);

            Log.d(TAG, "insideDataObjectHolder");

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(final View v) {
            // yahan jab alag alag things pe alag alag onClickListener

            if (mCardSet.get(getAdapterPosition()).getEntryType().equals("Phone")) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                imageEntryType.setImageResource(R.drawable.call);
                i.setData(Uri.parse("tel:" + mCardSet.get(getAdapterPosition()).getEntryDetails()));
                v.getContext().startActivity(i);
            }
            if (mCardSet.get(getAdapterPosition()).getEntryType().equals("Email")) {
                imageEntryType.setImageResource(R.drawable.email);
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mCardSet.get(getAdapterPosition()).getEntryDetails(), null));
                v.getContext().startActivity(Intent.createChooser(i, "Send Email..."));
            }
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ShowDetailsRVAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ShowDetailsRVAdapter(ArrayList<DataObjectCardEntry> myCardSet, Context context) {
        mCardSet = myCardSet;
        Log.d(TAG, "ShowDetailsRVAdapter");
        this.context = context;
    }

    @Override
    public ShowDetailsRVAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_show_entry, parent, false);

        Log.d(TAG, "insideEntryPnCreateViewHolder");

        ShowDetailsRVAdapter.DataObjectHolder dataObjectHolder = new ShowDetailsRVAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final ShowDetailsRVAdapter.DataObjectHolder holder, final int position) {
        holder.txtEntryType.setText(mCardSet.get(position).getEntryType());
        holder.txtEntryDetails.setText(mCardSet.get(position).getEntryDetails());

        if (mCardSet.get(position).getEntryType().equals("Phone")) {
            holder.imageEntryType.setImageResource(R.drawable.call);
        }
        if (mCardSet.get(position).getEntryType().equals("Email")) {
            holder.imageEntryType.setImageResource(R.drawable.email);
        }
        Log.d(TAG, "entryDetails: " + mCardSet.get(position).getEntryType() + "\n" + mCardSet.get(position).getEntryDetails());
    }

    public void addItem(DataObjectCardEntry cardObject, int index) {
        mCardSet.add(index, cardObject);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mCardSet.remove(index);
        notifyItemRemoved(index);
    }

    public DataObjectCardEntry getData(int position) {
        return mCardSet.get(position);
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "itemsSize: " + mCardSet.size());
        return mCardSet.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
