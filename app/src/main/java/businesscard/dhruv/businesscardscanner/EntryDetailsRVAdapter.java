package businesscard.dhruv.businesscardscanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv on 11/1/17.
 */

public class EntryDetailsRVAdapter
        extends RecyclerView
        .Adapter<EntryDetailsRVAdapter
        .DataObjectHolder> {
    public static final String TAG = "EntryDetailsRvAdapter";
    public static ArrayList<DataObjectCardEntry> mCardSet;
    private static MyClickListener myClickListener;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView imageEntryType;
        EditText txtEntryType;
        EditText txtEntryDetails;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageEntryType = (ImageView) itemView.findViewById(R.id.entry_img);
            txtEntryType = (EditText) itemView.findViewById(R.id.entry_type);
            txtEntryDetails = (EditText) itemView.findViewById(R.id.entry_detail);

            Log.d(TAG,"insideDataObjectHolder");

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // yahan jab alag alag things pe alag alag onClickListener

            if(mCardSet.get(getAdapterPosition()).getEntryType().equals("Phone"))
            {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+mCardSet.get(getAdapterPosition()).getEntryDetails()));
            }
            if(mCardSet.get(getAdapterPosition()).getEntryType().equals("Email"))
            {
                Intent i = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",mCardSet.get(getAdapterPosition()).getEntryDetails(),null));
                v.getContext().startActivity(Intent.createChooser(i,"Send Email..."));
            }
            if(mCardSet.get(getAdapterPosition()).getEntryType().equals("Website"))
            {
                // web view
            }

            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public EntryDetailsRVAdapter(ArrayList<DataObjectCardEntry> myCardSet, Context context) {
        mCardSet = myCardSet;
        Log.d(TAG,"insideEntryDetailsRvAdapter");
        this.context = context;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_individ_entry, parent, false);

        Log.d(TAG,"insideEntryPnCreateViewHolder");

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.txtEntryType.setText(mCardSet.get(position).getEntryType());
        holder.txtEntryDetails.setText(mCardSet.get(position).getEntryDetails());

        Log.d(TAG,"entryDetails: "+mCardSet.get(position).getEntryType()+"\n"+mCardSet.get(position).getEntryDetails());

//        holder.imageEntryType.setImageResource(R.drawable.call);
//
//        // ab yahan on the basis of what the mCardSet.get(position).getTxtName() hoga us basis par image will be set
//
//        if(mCardSet.get(position).getEntryType().equals("Phone"))
//        {
//            holder.imageEntryType.setImageResource(R.drawable.call);
//        }
//        else if(mCardSet.get(position).getEntryType().equals("Email"))
//        {
//            holder.imageEntryType.setImageResource(R.drawable.email);
//        }
//        else if(mCardSet.get(position).getEntryType().equals("Company"))
//        {
//            holder.imageEntryType.setImageResource(R.drawable.bussiness);
//        }
//        else if(mCardSet.get(position).getEntryType().equals("Website"))
//        {
//            holder.imageEntryType.setImageResource(R.drawable.wifi);
//        }
//        else
//        {
//            holder.imageEntryType.setImageResource(R.drawable.bussiness);
//        }
    }

    public void addItem(DataObjectCardEntry cardObject, int index) {
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