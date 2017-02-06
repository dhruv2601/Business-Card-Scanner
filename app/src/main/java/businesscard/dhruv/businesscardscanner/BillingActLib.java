package businesscard.dhruv.businesscardscanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import businesscard.dhruv.businesscardscanner.util.IabHelper;

public class BillingActLib extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar progressBar;
    private List<SkuDetails> productLists;

    public static final String cards10 = "data.pole.10";
    public static final String cards25 = "data.pole.25";
    public static final String cards50 = "data.pole.50";
    public static final String cards100 = "data.pole.100";

    private boolean readyToPurchase = false;
    BillingProcessor bp;
    private ArrayList<String> price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bp = new BillingProcessor(this, getString(R.string.play_billing_license_key), this);
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        checkStatus();
        getProducts();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        checkStatus();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                Toast.makeText(BillingActLib.this, "Unable to process purchase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                List<String> owned = bp.listOwnedProducts();
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


    private void getProducts() {

        new AsyncTask<Void, Void, List<SkuDetails>>() {
            @Override
            protected List<SkuDetails> doInBackground(Void... voids) {

                ArrayList<String> products = new ArrayList<>();

                products.add(cards10);
                products.add(cards25);
                products.add(cards50);
                products.add(cards100);
                return bp.getPurchaseListingDetails(products);
            }

            @Override
            protected void onPostExecute(List<SkuDetails> productList) {
                super.onPostExecute(productList);

                if (productList == null)
                    return;

                productLists = productList;
                Collections.sort(productList, new Comparator<SkuDetails>() {
                    @Override
                    public int compare(SkuDetails skuDetails, SkuDetails t1) {
                        if (skuDetails.priceValue >= t1.priceValue)
                            return 1;
                        else if (skuDetails.priceValue <= t1.priceValue)
                            return -1;
                        else return 0;
                    }
                });
                for (int i = 0; i < productList.size(); i++) {
                    final SkuDetails product = productList.get(i);
//                    View rootView = LayoutInflater.from(BillingActLib.this).inflate(R.layout.activity_billing, mRecyclerView, false);

//                    AppCompatButton detail = (AppCompatButton) rootView.findViewById(R.id.card_rates);
//                    detail.setText(product.priceText);
//
                    price.add(i, product.priceText);
//                    rootView.findViewById(R.id.btn_donate).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (readyToPurchase)
//                                bp.purchase(DonateActivity.this, product.productId);
//                            else
//                                Toast.makeText(DonateActivity.this, "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                    productListView.addView(rootView);

                }
                mAdapter = new BillingRVAdapter(price);
                mRecyclerView.setAdapter(mAdapter);

                progressBar.setVisibility(View.GONE);
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
                    final SkuDetails product = productLists.get(position);
                    bp.purchase(BillingActLib.this, product.productId);
                } else
                    Toast.makeText(BillingActLib.this, "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
            }
            // handle on item click on each card item
//            }
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
