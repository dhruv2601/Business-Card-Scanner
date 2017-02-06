package businesscard.dhruv.businesscardscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import businesscard.dhruv.businesscardscanner.util.IabHelper;
import businesscard.dhruv.businesscardscanner.util.IabResult;
import businesscard.dhruv.businesscardscanner.util.Inventory;
import businesscard.dhruv.businesscardscanner.util.Purchase;

public class BillingActivity extends AppCompatActivity {

    private ArrayList<String> price;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String TAG = "BillingActivity";
    static final String ITEM_SKU = "android.test.purchased";
    IabHelper mHelper;
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz0T9NH/mKrGTt98opiAoEkje/2/D11V4cs0rMU6RGQBKGB8mHZExaz+d8yST89gdfsuZ/zhgboIde5/PLP9IpHu5H0IXOxK2E5aR9JdjUjv50f+jlc4gU7uJWe9FVmfkVxlrawOx8W3K6MCOYZRDaKNYt9+ZkKx+surgfOiiF95N4ghpVMnw6FQbLXn3uRuN+Pkay6snUhppmei7gIoYkFY3Vd22j7sCPPvLUzLP7bEufjo1Crxy+2M+mOny1wQsBxjwr6P4ZCjjHWnoua/Ou9HByBHtr6Jh/Q3+G4qsHOzuxHzL3fZ08wQSwfcxyWEDNReWeDrEIszCGTl539LYQwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        mRecyclerView = (RecyclerView) findViewById(R.id.bill_type_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BillingRVAdapter(price);
        mRecyclerView.setAdapter(mAdapter);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BillingRVAdapter) mAdapter).setOnItemClickListener(new BillingRVAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                try {
                    mHelper.launchPurchaseFlow(BillingActivity.this, ITEM_SKU, 10001,
                            mPurchaseFinishedListener, String.valueOf(position));
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                // handle on item click on each card item i.e. open each card to show details
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {

                // INCREASE THE NUMBERS OF CARDS ALLOWED IN ACCORDANCE HERE

                consumeItem();
//                buyButton.setEnabled(false);
            }

        }
    };

    public void consumeItem() {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        Toast.makeText(BillingActivity.this, "PAYMENT SUCCESSFULL", Toast.LENGTH_SHORT).show();
//                        clickButton.setEnabled(true);
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

}
