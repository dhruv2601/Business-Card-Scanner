package businesscard.dhruv.businesscardscanner;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BillingActLib extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG = "BillingAct";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<SkuDetails> productLists;

    public static final String cards10 = "testing.bcs.1";
    public static final String cards25 = "data.pole.cards25";
    public static final String cards50 = "data.pole.50cards";
    public static final String cards100 = "data.pole.100cards";

    private boolean readyToPurchase = false;
    BillingProcessor bp;
    private ArrayList<String> price;

    private TextView txt;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_billing);

        txt = (TextView) findViewById(R.id.fetching);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);

        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.your_dialog_layout
                , null));

        settingsDialog.show();
        Toast.makeText(this, "PRESS BACK ONCE FOR PURCHASE.", Toast.LENGTH_LONG).show();

        price = new ArrayList<>();
        price.add(0, "0");
        price.add(1, "0");
        price.add(2, "0");
        price.add(3, "0");
        mRecyclerView = (RecyclerView) findViewById(R.id.bill_type_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BillingRVAdapter(price);
        mRecyclerView.setAdapter(mAdapter);

        bp = new BillingProcessor(this, getString(R.string.play_billing_license_key), this);
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        checkStatus();
        getPrice();
//        getProducts();
    }

    @Override
    public void onProductPurchased(final String productId, final TransactionDetails details) {
        checkStatus();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int cards = 0;
                switch (productId) {
                    case cards10:
                        cards = 10;
                        break;
                    case cards25:
                        cards = 25;
                        break;
                    case cards50:
                        cards = 50;
                        break;
                    case cards100:
                        cards = 100;
                        break;
                }

                ParseUser user = null;
                try {
                    user = ParseUser.getCurrentUser().fetch();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SharedPreferences pref = BillingActLib.this.getSharedPreferences("cardMng", 0);
                int cardsLeft = pref.getInt("cardsLeft", 0);
                int tempNo = pref.getInt("tempNo", 0);
                cardsLeft -= tempNo;

                cardsLeft += cards;
                SharedPreferences.Editor cardEdit = pref.edit();
                cardEdit.putInt("tempNo", 0);
                cardEdit.putInt("cardsLeft", cardsLeft);
                cardEdit.commit();

                user.put("cardsLeft", cardsLeft);
                user.saveInBackground();

                Intent i = new Intent(BillingActLib.this, ThankYouAct.class);
                startActivity(i);
                BillingActLib.this.finish();
                Toast.makeText(BillingActLib.this, "Thanks for the purchase. Now you can enjoy more cards!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(BillingActLib.this, "Unable to process purchase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                List<String> owned = bp.listOwnedProducts();
                Log.d(TAG, "prodOwnedSize: " + owned.size());
                for (int i = 0; i < owned.size(); i++) {
                    Log.d(TAG, "prodOwned: " + owned.get(i));
                }

                return owned != null && owned.size() != 0;
            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                if (b) {
//                    status.setText("Thanks for your donation!");
                }
            }
        }.execute();
    }

    private void getPrice() {

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                Log.d(TAG, "inside::: ");
                ArrayList<String> list = new ArrayList<String>();
                SkuDetails skuCards10 = bp.getPurchaseListingDetails(cards10);
                SkuDetails skuCards25 = bp.getPurchaseListingDetails(cards25);
                SkuDetails skuCards50 = bp.getPurchaseListingDetails(cards50);
                SkuDetails skuCards100 = bp.getPurchaseListingDetails(cards100);
                list.add(0, skuCards10.priceText);
                list.add(1, skuCards25.priceText);
                list.add(2, skuCards50.priceText);
                list.add(3, skuCards100.priceText);
                Log.d(TAG, "skussffuege 10: " + skuCards10);
                return list;
            }

            @Override
            protected void onPostExecute(List<String> productList) {
                super.onPostExecute(productList);

                txt.setVisibility(View.GONE);
                pBar.setVisibility(View.GONE);
                price.add(0, productList.get(0));
                price.add(1, productList.get(1));
                price.add(2, productList.get(2));
                price.add(3, productList.get(3));

                Log.d(TAG, "price: " + price.get(2));
                mAdapter = new BillingRVAdapter(price);
                mRecyclerView.setAdapter(mAdapter);
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BillingRVAdapter) mAdapter).setOnItemClickListener(new BillingRVAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (readyToPurchase) {
                    if (position == 0) {
                        bp.purchase(BillingActLib.this, cards10);
                    }
                    if (position == 1) {
                        bp.purchase(BillingActLib.this, cards25);
                    }
                    if (position == 2) {
                        bp.purchase(BillingActLib.this, cards50);
                    }
                    if (position == 3) {
                        bp.purchase(BillingActLib.this, cards100);
                    }
                } else
                    Toast.makeText(BillingActLib.this, "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
}