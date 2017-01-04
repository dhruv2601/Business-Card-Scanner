package businesscard.dhruv.businesscardscanner;

import android.app.ProgressDialog;
import android.content.Intent;
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
    private TextView forgotPassword;
    private EditText edtMobile;
    private EditText edtPass;
    private String phoneNo;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Mh9fvVwsWq5DcPVk3lGo9sKixRUuux0d6hqyDY1Y")
                .server("https://parseapi.back4app.com/")
                .clientKey("REWqvRAhPWnuLJajk6XQq58vzQtPBdeESEhWZsO6")
                .build());

        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        createAccount = (TextView) findViewById(R.id.link_signup);
        edtMobile = (EditText) findViewById(R.id.input_p_number);
        edtPass = (EditText) findViewById(R.id.input_password);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //start next activity
            //start sinch service
            Intent i = new Intent(LoginActivity.this, MainActivity1.class);
            startActivity(i);
        } else {
            createAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, CreateAccount.class);
                    startActivity(i);
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send a new password on the mail id and set that password onto parse server
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
                                startActivity(i);
                            } else {
                                Toast.makeText(LoginActivity.this, "There was an error logging in, please check credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}