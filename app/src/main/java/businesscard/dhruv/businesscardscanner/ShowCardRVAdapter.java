package businesscard.dhruv.businesscardscanner;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by dhruv on 29/12/16.
 */

public class ShowCardRVAdapter extends RecyclerView
        .Adapter<ShowCardRVAdapter
        .DataObjectHolder> {

    public static final String TAG = "myRecViewAdapter";
    private ArrayList<CardObject> mCardSet;
    private Activity act;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView imageCard;
        TextView txtEntry;
        TextView txtEntryTitle;

        public DataObjectHolder(View itemView) {
            super(itemView);

            txtEntryTitle = (TextView) itemView.findViewById(R.id.txt_entry_title);
            imageCard = (ImageView) itemView.findViewById(R.id.img_icon);
            txtEntry = (TextView) itemView.findViewById(R.id.txt_entry);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {

        this.myClickListener = myClickListener;
    }

    public ShowCardRVAdapter(ArrayList<CardObject> myCardSet, Activity activity) {
        mCardSet = myCardSet;
        this.act = activity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.imageCard.setImageResource(mCardSet.get(position).getmDrawableImage());
        holder.txtEntry.setText(mCardSet.get(position).getTxtEntry());
        holder.txtEntryTitle.setText(mCardSet.get(position).getTxtEntryTitle());
    }

    public void addItem(CardObject cardObject, int index) {
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
