package businesscard.dhruv.businesscardscanner;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton btnLogin;
    private TextView createAccount;
    private EditText edtMobile;
    private EditText edtPass;
    private String phoneNo;
    private String password;
    public static SharedPreferences sref;
//    public BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);


//        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
//                .applicationId("Mh9fvVwsWq5DcPVk3lGo9sKixRUuux0d6hqyDY1Y")
//                .server("https://parseapi.back4app.com/")
//                .clientKey("REWqvRAhPWnuLJajk6XQq58vzQtPBdeESEhWZsO6")
//                .build());

        sref = LoginActivity.this.getSharedPreferences("entered", 0);
        SharedPreferences.Editor editor = sref.edit();
        if (sref.getBoolean("entered", false) == false) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent i = new Intent(LoginActivity.this, ActivityIntro.class);
                startActivity(i);
            }
        }

        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
//        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        createAccount = (TextView) findViewById(R.id.link_signup);
        edtMobile = (EditText) findViewById(R.id.input_p_number);
        edtPass = (EditText) findViewById(R.id.input_password);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //start next activity
            //start sinch service
//            final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
//            startService(serviceIntent);

//            receiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    Boolean success = intent.getBooleanExtra("success", false);
////                progressDialog.dismiss();
//                    //show a toast message if the Sinch
//                    //service failed to start
//                    if (!success) {
//                        Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
//                    }
//                }
//            };
//
//            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("businesscard.dhruv.businesscardscanner.LoginActivity"));

            Intent i = new Intent(LoginActivity.this, MainActivity1.class);
            LoginActivity.this.finish();
            startActivity(i);
        } else {
//            final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
//            startService(serviceIntent);

//            receiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    Boolean success = intent.getBooleanExtra("success", false);
////                progressDialog.dismiss();
//                    //show a toast message if the Sinch
//                    //service failed to start
//                    if (!success) {
//                        Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
//                    }
//                }
//            };

//            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("businesscard.dhruv.businesscardscanner.LoginActivity"));

            createAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, CreateAccount.class);
                    startActivity(i);
                }
            });


            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneNo = edtMobile.getText().toString();
                    password = edtPass.getText().toString();

                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging In...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    ParseUser.logInInBackground(phoneNo, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, MainActivity1.class);
                                LoginActivity.this.finish();
                                startActivity(i);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "There was an error logging in, please check credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}