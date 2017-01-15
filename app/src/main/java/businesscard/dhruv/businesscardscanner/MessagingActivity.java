package businesscard.dhruv.businesscardscanner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;

public class MessagingActivity extends Activity {
    private static final String TAG = "MessagingAct";
    public static String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private ListView messagesList;
    private MessageAdapter messageAdapter;
    public MyMessageClientListener messageClientListener;
    public static int delieverd = 0;
    private SharedPreferences pref;
    private String recepName;
    private String recepNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        bindService(new Intent(this, MessageService.class), serviceConnection, BIND_AUTO_CREATE);
        //get recipientId from the intent
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
        recepNum = intent.getStringExtra("RECIPIENT_NUM");
        recepName = intent.getStringExtra("RECIPIENT_NAME");

        currentUserId = ParseUser.getCurrentUser().getObjectId();
        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);

        messageClientListener = new MyMessageClientListener();
        //listen for a click on the send button
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send the message!

                messageBody = messageBodyField.getText().toString();
                if (messageBody.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "reciepientId: " + recipientId);
                Log.d(TAG, "reciepientText: " + messageBody);

                messageService.sendMessage(recipientId, messageBody);
                messageBodyField.setText("");
            }
        });
    }

    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        messageService.removeMessageClientListener(messageClientListener);
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "messageServiceConnected");

            Log.d(TAG, "iBinder: " + iBinder);
            messageService = (MessageService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        //Notify the user if their message failed to send
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            delieverd = -1;
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, delieverd);

            Log.d(TAG, "messageNotSent: " + failureInfo.getSinchError().toString());   // the user should login atleast once
            Toast.makeText(MessagingActivity.this, "Message failed to send.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onIncomingMessage(MessageClient client, Message message) {
            //Display an incoming message
            if (message.getSenderId().equals(recipientId)) {

                pref = MessagingActivity.this.getSharedPreferences("prevChat", 0);
                int temp = pref.getInt("numUser", 0);

                if (pref.contains("prevUser" + recipientId) == false) {
                    SharedPreferences.Editor editor = MessagingActivity.this.getSharedPreferences("prevChat", 0).edit();
                    editor.putInt("numUser", temp + 1);
                    editor.putInt("prevUser" + recipientId, 1);
                    editor.putString("prevUser" + "Recep" + temp, recipientId);    // prevUserRecep1 dega 2nd user ke recep id
                    editor.putString("prevUser" + "Name", recepName);
                    editor.putString("prevUser" + "Num", recepNum);
                    editor.commit();

                    Log.d(TAG, "nameAsInMessagingAct " + recepName + "\n" + recepNum);
                }

                WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
                messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING, delieverd);
            }
        }

        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {
            //Display the message that was just sent
            //Later, I'll show you how to store the
            //message in Parse, so you can retrieve and
            //display them every time the conversation is opened

            pref = MessagingActivity.this.getSharedPreferences("prevChat", 0);
            if (pref.contains("prevUser" + recipientId) == false) {
                int temp = pref.getInt("numUser", 0);
                SharedPreferences.Editor editor = MessagingActivity.this.getSharedPreferences("prevChat", 0).edit();
                editor.putInt("numUser", temp + 1);
                editor.putInt("prevUser" + recipientId, 1);
                editor.putString("prevUser" + "Recep" + temp, recipientId);    // prevUserRecep1 dega 2nd user ke recep id
                editor.putString("prevUser" + "Name" + temp, recepName);
                editor.putString("prevUser" + "Num" + temp, recepNum);
                editor.commit();
            }

            delieverd = 0;
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING, delieverd);
        }

        //Do you want to notify your user when the message is delivered?
        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
            delieverd = 1;
        }

        //Don't worry about this right now
        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {

            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            Log.d(TAG, "ready for push :) ");
            ParseQuery userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("objectId", writableMessage.getRecipientIds().get(0));

//            ParseQuery pushQuery = ParseInstallation.getQuery();
//            pushQuery.whereMatchesQuery("user", userQuery);
//
//            ParsePush push = new ParsePush();
//            push.setQuery(pushQuery);
//            push.setMessage("You have a new message");
//            push.sendInBackground(new SendCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.d(TAG, "push notif sent");
//                        Toast.makeText(MessagingActivity.this, "Push notif sent", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MessagingActivity.this, "unable to send notif " + e, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
    }
}
