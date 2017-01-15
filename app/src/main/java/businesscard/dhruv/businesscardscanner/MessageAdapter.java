package businesscard.dhruv.businesscardscanner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dhruv on 2/1/17.
 */

public class MessageAdapter extends BaseAdapter {
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    private static final String TAG = "MessageAdapter";
    private List<Pair<WritableMessage, Integer>> messages;
    private LayoutInflater layoutInflater;
    private int status;
    public SharedPreferences prevChat;
    public Activity activity;

    public MessageAdapter(Activity activity) {
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<WritableMessage, Integer>>();
        if (messages.isEmpty()) {
            prevChat = activity.getSharedPreferences("prevChat", 0);

            int prefSize = prevChat.getInt("size", 0);
            for (int i = 0; i <prefSize; i++) {
                String message = prevChat.getString("prevChat" + MessagingActivity.recipientId + i, " ");
                int direction = prevChat.getInt("prevChat" + "direction" + MessagingActivity.recipientId + i, 0);
                WritableMessage writableMessage = new WritableMessage(MessagingActivity.recipientId, message);
                messages.add(new Pair(writableMessage, direction));
            }
        }
    }

    public void addMessage(WritableMessage message, int direction, int status) {
        this.status = status;
        Log.d(TAG, "message: " + message);

        prevChat = activity.getSharedPreferences("prevChat", 0);
        int prefSize = prevChat.getInt("size", 0);

        SharedPreferences.Editor editor = activity.getSharedPreferences("prevChat", 0).edit();
        editor.putString("prevChat" + MessagingActivity.recipientId + prefSize, message.getTextBody());
        editor.putInt("prevChat" + "direction" + MessagingActivity.recipientId + prefSize, direction);
        prefSize++;
        editor.putInt("size", prefSize);

        editor.commit();

        messages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);
        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_left;
            } else if (direction == DIRECTION_OUTGOING) {
                if (status == -1) {
                    res = R.layout.message_right_notsent;
                } else if (status == 0) {
                    res = R.layout.message_right_deleivered;
                } else {
                    res = R.layout.message_right_deleivered;
                }
            }

            convertView = layoutInflater.inflate(res, viewGroup, false);
        }

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        WritableMessage message = messages.get(i).first;
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtDate.setText(date.getDate() + " " + date.getMonth() + " " + date.getYear() + "   " + date.getHours() + "/" + date.getMinutes());
        txtMessage.setText(message.getTextBody());

        return convertView;
    }
}