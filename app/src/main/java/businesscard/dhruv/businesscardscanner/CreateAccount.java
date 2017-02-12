package businesscard.dhruv.businesscardscanner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class CreateAccount extends AppCompatActivity {

    private static final String TAG = "CreateAccount";
    TextView edtPhoneNo;
    TextView edtEmail;
    TextView edtPassword;
    AppCompatButton btnSignUp;

    public String password;
    public String phoneNo;
    public String email;
    public static SharedPreferences sref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        btnSignUp = (AppCompatButton) findViewById(R.id.btn_login);
        edtPassword = (TextView) findViewById(R.id.input_password);
        edtEmail = (TextView) findViewById(R.id.input_email);
        edtPhoneNo = (TextView) findViewById(R.id.input_p_number);

        // also send a conformation email to the user after successful sign up

        sref = CreateAccount.this.getSharedPreferences("entered", 0);
        SharedPreferences.Editor editor = sref.edit();
        if (sref.getBoolean("entered", false) == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent i = new Intent(CreateAccount.this, ActivityIntro.class);
                startActivity(i);
            }
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = edtPassword.getText().toString();
                phoneNo = edtPhoneNo.getText().toString();
                email = edtEmail.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(CreateAccount.this);
                progressDialog.setMessage("Signing Up...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                if (password.isEmpty() || phoneNo.isEmpty() || email.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccount.this, "No field can be left empty", Toast.LENGTH_LONG).show();
                } else {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> userList, ParseException e) {
                            if (e == null) {
                                progressDialog.dismiss();
                                Toast.makeText(CreateAccount.this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show();
                                int already = 0;
                                Log.d(TAG, "userListSize: " + userList.size());
                                for (int i = 0; i < userList.size(); i++) {
                                    if (email.equals(userList.get(i).getEmail().toString())) {
                                        already = 1;
                                        progressDialog.dismiss();
                                        new AlertDialog.Builder(CreateAccount.this).setTitle("Please Retry").setMessage("An Account already exists with the provided Email Address.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                    }
                                                }).show();
                                    } else if (phoneNo.equals(userList.get(i).getUsername().toString())) {

                                        progressDialog.dismiss();
                                        already = 1;
                                        new AlertDialog.Builder(CreateAccount.this).setTitle("Please Retry").setMessage("An Account already exists with the provided Phone Number.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                    }
                                                }).show();
                                    }
                                }

                                if (already == 0) {
                                    final ParseUser user = new ParseUser();
                                    user.setUsername(phoneNo);
                                    user.setEmail(email);
                                    user.setPassword(password);
                                    int cardsLeft = user.getInt("cardsLeft");
                                    Log.d(TAG, "cardsLeft: " + cardsLeft);
                                    user.put("cardsLeft", 10);

                                    SharedPreferences cardsMng = CreateAccount.this.getSharedPreferences("cardMng",0);
                                    SharedPreferences.Editor cardEdit = cardsMng.edit();
                                    cardEdit.putInt("cardsLeft",10);
                                    cardEdit.commit();

                                    user.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                                // send an email

                                                progressDialog.dismiss();
                                                Intent i = new Intent(CreateAccount.this, MainActivity1.class);
                                                startActivity(i);
                                                CreateAccount.this.finish();
                                            } else {
                                                new AlertDialog.Builder(CreateAccount.this).setTitle("Please Retry").setMessage("An error was encountered from the server. Thank you for your patience!")
                                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // continue with delete
                                                            }
                                                        }).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
