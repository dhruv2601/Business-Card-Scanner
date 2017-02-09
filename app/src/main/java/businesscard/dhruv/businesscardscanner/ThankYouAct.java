package businesscard.dhruv.businesscardscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ThankYouAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ThankYouAct.this.finish();
    }
}
