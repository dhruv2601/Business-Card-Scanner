package businesscard.dhruv.businesscardscanner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SaveCardActivity extends AppCompatActivity {

    public static final String TAG = "SaveCardActivity";
    private CoordinatorLayout cord;
    private RecyclerView rvEntryDetails;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imgScanned;
    public int cardNo;
    public Bitmap cardBitmap;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/BusinessCardScanner/";
    public HashMap<String, String> entities;
    private FloatingActionButton fabSaveContact;

    public Handler mHandler;
    private de.hdodenhof.circleimageview.CircleImageView imgPersonImg;
    private Button addAnotherField;

    public static final int PICK_IMAGE_REQUEST = 2601;
    private ArrayList<DataObjectCardEntry> result;
    public static String type[] = new String[30];
    public static String desc[] = new String[30];
    private String imageUri;
    public String dataSetUrl = "";
    private String name = "";
    private String email = "";
    private String website = "";
    private String no[] = new String[10];
    private int phNo;
    private String company = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_card);

//        Toast.makeText(this, "Click on TICKS on the right to save entries.", Toast.LENGTH_SHORT).show();

        mHandler = new Handler();
        entities = new HashMap<>();

        addAnotherField = (Button) findViewById(R.id.btn_add_detail);
        imgPersonImg = (CircleImageView) findViewById(R.id.img_profile);
        imgScanned = (ImageView) findViewById(R.id.img_scanned_card);
        rvEntryDetails = (RecyclerView) findViewById(R.id.rv_entry_details);
        cord = (CoordinatorLayout) findViewById(R.id.cord1);
        rvEntryDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvEntryDetails.setLayoutManager(layoutManager);
        rvEntryDetails.setNestedScrollingEnabled(false);
        adapter = new EntryDetailsRVAdapter(getDataSet1(), SaveCardActivity.this);
        rvEntryDetails.setAdapter(adapter);

        final SharedPreferences VSR = this.getSharedPreferences("OwnCard", 0);
        final int isMyCard = VSR.getInt("ownCardStatus", 0);

        fabSaveContact = (FloatingActionButton) findViewById(R.id.fab_save_contact);
        fabSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // edit text mn jo data edit hua hai save that

                SharedPreferences cardsMng = view.getContext().getSharedPreferences("cardMng",0);
                SharedPreferences.Editor cardEdit = cardsMng.edit();
                int tempNo = cardsMng.getInt("tempNo",0);
                cardEdit.putInt("tempNo",++tempNo);
                cardEdit.commit();

                if (isMyCard == 1) {
                    SharedPreferences.Editor editor = VSR.edit();

                    SharedPreferences pref = SaveCardActivity.this.getSharedPreferences("AllCards", 0);
                    int totalCards = pref.getInt("CardNo", 0);

                    SharedPreferences preferences = SaveCardActivity.this.getSharedPreferences("SavedCards", 0);
                    cardNo = preferences.getInt("CardNo", 1);
                    imageUri = preferences.getString("ImageUri" + cardNo, "");

                    for (int i = 0; i < result.size(); i++) {
                        String ty = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryType" + i, result.get(i).getEntryType());
                        switch (ty) {
                            case "Name":
                                name = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Company":
                                company = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Phone":
                                no[phNo++] = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Email":
                                email = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Website":
                                website = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                        }

                        editor.putString("Card" + "EntryType" + i, result.get(i).getEntryType());
                        editor.putString("Card" + "EntryDetail" + i, result.get(i).getEntryDetails());
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    String convertByte = "";
                    byte[] data = stream.toByteArray();

                    convertByte = Base64.encodeToString(data, Base64.DEFAULT);
                    editor.putString("Card" + totalCards + 1 + "Photo", convertByte);
                    editor.putInt("CardEnt", result.size() - 1);
                    editor.putString("CardBitmap" + totalCards, convertByte);
                    editor.putInt("cardThere", 1);
                    editor.commit();

                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data1 = stream.toByteArray();
                    String convertByte1 = "";

                    int i = 0;
                    SharedPreferences pref1 = SaveCardActivity.this.getSharedPreferences("AllCards", 0);
                    SharedPreferences.Editor editor1 = pref.edit();
                    int totalCards1 = pref.getInt("CardNo", 0);
                    int numEntities = entities.size();
                    for (i = 0; i < result.size(); i++) {
                        String ty = pref1.getString("Card" + String.valueOf(totalCards1 + 1) + "EntryType" + i, result.get(i).getEntryType());
                        switch (ty) {
                            case "Name":
                                name = pref1.getString("Card" + String.valueOf(totalCards1 + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Company":
                                company = pref1.getString("Card" + String.valueOf(totalCards1 + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Phone":
                                no[phNo++] = pref1.getString("Card" + String.valueOf(totalCards1 + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Email":
                                email = pref1.getString("Card" + String.valueOf(totalCards1 + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Website":
                                website = pref1.getString("Card" + String.valueOf(totalCards1 + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                        }

                        editor1.putString("Card" + String.valueOf(totalCards1 + 1) + "EntryType" + i, result.get(i).getEntryType());
                        editor1.putString("Card" + String.valueOf(totalCards1 + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                    }

                    convertByte1 = Base64.encodeToString(data, Base64.DEFAULT);
                    editor1.putString("Card" + (totalCards1 + 1) + "Photo", convertByte1);

                    ++totalCards1;
                    editor1.putInt("CardEnt" + totalCards1, result.size() - 1);
                    editor1.putInt("CardNo", totalCards1);
                    editor1.putString("CardBitmap" + totalCards1, convertByte1);
                    editor1.commit();
                    Log.d(TAG, "pref:: " + pref.getString("Card" + String.valueOf(totalCards) + "EntryType" + 1, "null hai bc"));

                    ArrayList<ContentProviderOperation> ops =
                            new ArrayList<ContentProviderOperation>();

                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build()
                    );

                    if (name != null) {
                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                .withValue(
                                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                        name).build()
                        );
                    }

                    for (int j = 0; j < phNo; j++) {
                        if (no[j] != null && j == 0) {
                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no[0])
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build()
                            );
                        } else if (no[j] != null && j == 1) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no[1])
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                    .build());
                        } else if (no[j] != null && j == 2) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no[2])
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                    .build());
                        }
                    }
                    if (email != null) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .build());
                    }

                    try {
                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar snackbar = Snackbar
                                .make(cord, "Please enable permission to save contacts", Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
//                    Toast.makeText(SaveCardActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(view.getContext(), ShowCardDetails.class);
                    intent.putExtra("CardPosition", totalCards - 1);
                    intent.putExtra("nobodyDIB", 1);
                    startActivity(intent);
                    SaveCardActivity.this.finish();
                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data = stream.toByteArray();
                    String convertByte = "";

                    int i = 0;
                    SharedPreferences pref = SaveCardActivity.this.getSharedPreferences("AllCards", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    int totalCards = pref.getInt("CardNo", 0);
                    int numEntities = entities.size();
                    for (i = 0; i < result.size(); i++) {
                        String ty = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryType" + i, result.get(i).getEntryType());
                        switch (ty) {
                            case "Name":
                                name = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Company":
                                company = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Phone":
                                no[phNo++] = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Email":
                                email = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                                break;
                            case "Website":
                                website = pref.getString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                        }

                        editor.putString("Card" + String.valueOf(totalCards + 1) + "EntryType" + i, result.get(i).getEntryType());
                        editor.putString("Card" + String.valueOf(totalCards + 1) + "EntryDetail" + i, result.get(i).getEntryDetails());
                    }

                    convertByte = Base64.encodeToString(data, Base64.DEFAULT);
                    editor.putString("Card" + (totalCards + 1) + "Photo", convertByte);

                    ++totalCards;
                    editor.putInt("CardEnt" + totalCards, result.size() - 1);
                    editor.putInt("CardNo", totalCards);
                    editor.putString("CardBitmap" + totalCards, convertByte);
                    editor.commit();
                    Log.d(TAG, "pref:: " + pref.getString("Card" + String.valueOf(totalCards) + "EntryType" + 1, "null hai bc"));

                    ArrayList<ContentProviderOperation> ops =
                            new ArrayList<ContentProviderOperation>();

                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build()
                    );

                    if (name != null) {
                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                .withValue(
                                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                        name).build()
                        );
                    }

                    for (int j = 0; j < phNo; j++) {
                        if (no[j] != null && j == 0) {
                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no[0])
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build()
                            );
                        } else if (no[j] != null && j == 1) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no[1])
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                    .build());
                        } else if (no[j] != null && j == 2) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no[2])
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                    .build());
                        }
                    }
                    if (email != null) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .build());
                    }

                    try {
                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar snackbar = Snackbar
                                .make(cord, "Please enable permission to save contacts", Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
//                    Toast.makeText(SaveCardActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(view.getContext(), ShowCardDetails.class);
                    intent.putExtra("CardPosition", totalCards - 1);
                    intent.putExtra("nobodyDIB", 1);
                    startActivity(intent);
                    SaveCardActivity.this.finish();
                }
            }
        });

        imgPersonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass intent to add image from gallery and then set here and save and update n the shared preference and the server also
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        addAnotherField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SaveCardActivity.this, "Touch the TICKS on right after done.", Toast.LENGTH_SHORT).show();
                int yet = 0;
                if (entities.containsKey("NewNo")) {
                    yet = Integer.parseInt(entities.get("NewNo"));
                }

                entities.put("NewField" + (++yet), "");
                entities.put("NewNo", String.valueOf(yet));

                DataObjectCardEntry cardEntry = new DataObjectCardEntry("", "");

                result.add(cardEntry);
                for (int i = 0; i < result.size(); i++) {
                    Log.d(TAG, "resultArray: " + result.get(i).getEntryDetails());
                }

                adapter.notifyDataSetChanged();
            }
        });

        SharedPreferences preferences = this.getSharedPreferences("SavedCards", 0);
        cardNo = preferences.getInt("CardNo", 1);
        imageUri = preferences.getString("ImageUri" + cardNo, "");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        cardBitmap = BitmapFactory.decodeFile(String.valueOf(imageUri), options);

        imgScanned.setImageBitmap(cardBitmap);

        SharedPreferences.Editor edit = VSR.edit();
        edit.putInt("ownCardStatus", 0);
        edit.commit();

        SharedPreferences pref = this.getSharedPreferences("engDataSet", 0);
        dataSetUrl = pref.getString("dataSetUrl", "");
        if (dataSetUrl.equals("")) {
            Snackbar snackbar = Snackbar
                    .make(cord, "Please go to settings and download for English Language.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        new extractOCR().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
//                imgPersonImg.setImageBitmap(bitmap);

                // --------------->>>>>>>>>>>>>>>>>>>>      SAVE IN SHARED PREFERENCES AND LOCALLY ONLY   <<<<<<<<<--------

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((EntryDetailsRVAdapter) adapter).setOnItemClickListener(new EntryDetailsRVAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<DataObjectCardEntry> getDataSet1() {
        ArrayList<DataObjectCardEntry> result = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DataObjectCardEntry data = new DataObjectCardEntry("Fetching...", "Fetching...");
            result.add(data);
        }
        return result;
    }

    private ArrayList<DataObjectCardEntry> getDataSet() {
        ArrayList<DataObjectCardEntry> result = new ArrayList<DataObjectCardEntry>();
        int size = entities.size();
        Log.d(TAG, "entitiesSize: " + size);
        int x = 0;
        int i = 0;
        DataObjectCardEntry dataName = new DataObjectCardEntry("Name", "");
        result.add(dataName);
        DataObjectCardEntry dataComp = new DataObjectCardEntry("Company", "");
        result.add(dataComp);

        if (entities.containsKey("Phone")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Phone", entities.get("Phone"));
            result.add(data);
        }
        while (x != 1) {
            if (entities.containsKey("Phone" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Phone", entities.get("Phone" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("Website")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Website", entities.get("Website"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("Website" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Website", entities.get("Website" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("Email")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Email", entities.get("Email"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("Email" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Email", entities.get("Email" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("Name")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Name", entities.get("Name"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("Name" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Name", entities.get("Name" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("CompanyName")) {
            DataObjectCardEntry data = new DataObjectCardEntry("Company", entities.get("CompanyName"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("CompanyName" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Company", entities.get("CompanyName" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

        if (entities.containsKey("CompanyAdd")) {
            DataObjectCardEntry data = new DataObjectCardEntry("CompanyAdd", entities.get("CompanyAdd"));
            result.add(data);
        }
        x = 0;
        while (x != 1) {
            if (entities.containsKey("CompanyAdd" + i)) {
                DataObjectCardEntry data = new DataObjectCardEntry("Address", entities.get("CompanyAdd" + i));
                result.add(data);
                i++;
            } else {
                x = 1;
                i = 0;
            }
        }

//        if(entities.containsKey("NewNo"))
        {
//            int yet = Integer.parseInt(entities.get("NewNo"));
//            EntryDetailsRVAdapter entryDetailsRVAdapter = new EntryDetailsRVAdapter(result,this);
//            DataObjectCardEntry cardEntry = new DataObjectCardEntry("","");
//            entryDetailsRVAdapter.addItem(cardEntry,0);
        }
        this.result = result;
        return result;
    }

    private Bitmap GetBinaryBitmap(Bitmap bitmap_src) {
        Bitmap bitmap_new = bitmap_src.copy(bitmap_src.getConfig(), true);

        for (int x = 0; x < bitmap_new.getWidth(); x++) {
            for (int y = 0; y < bitmap_new.getHeight(); y++) {
                int color = bitmap_new.getPixel(x, y);
                color = GetNewColor(color);
                bitmap_new.setPixel(x, y, color);
            }
        }

        return bitmap_new;
    }

    private double GetColorDistance(int c1, int c2) {
        int db = Color.blue(c1) - Color.blue(c2);
        int dg = Color.green(c1) - Color.green(c2);
        int dr = Color.red(c1) - Color.red(c2);

        double d = Math.sqrt(Math.pow(db, 2) + Math.pow(dg, 2) + Math.pow(dr, 2));
        return d;
    }

    private int GetNewColor(int c) {
        double dwhite = GetColorDistance(c, Color.WHITE);
        double dblack = GetColorDistance(c, Color.BLACK);

        if (dwhite <= dblack) {
            return Color.WHITE;

        } else {
            return Color.BLACK;
        }
    }

//    public class tikaOpenIntro {
//
//        public String Tokens[];
//
//        public String namefind(String cnt[]) {
//            InputStream is;
//            TokenNameFinderModel tnf;
//            NameFinderME nf;
//            String sd = "";
//            try {
//                is = new FileInputStream("/storage/emulated/0/en-ner-person.bin");
//
//                tnf = new TokenNameFinderModel(is);
//                nf = new NameFinderME(tnf);
//
//                Span sp[] = nf.find(cnt);
//                String a[] = Span.spansToStrings(sp, cnt);
//                StringBuilder fd = new StringBuilder();
//                int l = a.length;
//
//                for (int j = 0; j < l; j++) {
//                    fd = fd.append(a[j] + "\n");
//                }
//                sd = fd.toString();
//
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            } catch (InvalidFormatException e) {
//
//                e.printStackTrace();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//            return sd;
//        }
//
//        public String orgFind(String cnt[]) {
//            InputStream is;
//            TokenNameFinderModel tnf;
//            NameFinderME nf;
//            String sd = "";
//            try {
//                is = new FileInputStream(
//                        "/storage/emulated/0/en-ner-organization.bin");
//
//                tnf = new TokenNameFinderModel(is);
//                nf = new NameFinderME(tnf);
//                Span sp[] = nf.find(cnt);
//                String a[] = Span.spansToStrings(sp, cnt);
//                StringBuilder fd = new StringBuilder();
//                int l = a.length;
//
//                for (int j = 0; j < l; j++) {
//                    fd = fd.append(a[j] + "\n");
//                }
//
//                sd = fd.toString();
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            } catch (InvalidFormatException e) {
//
//                e.printStackTrace();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//            return sd;
//        }
//
//        public String locationFind(String cnt[]) {
//            InputStream is;
//            TokenNameFinderModel tnf;
//            NameFinderME nf;
//            String sd = "";
//            try {
//                is = new FileInputStream(
//                        "/storage/emulated/0/en-ner-location.bin");
//
//                tnf = new TokenNameFinderModel(is);
//                nf = new NameFinderME(tnf);
//                Span sp[] = nf.find(cnt);
//                String a[] = Span.spansToStrings(sp, cnt);
//                StringBuilder fd = new StringBuilder();
//                int l = a.length;
//
//                for (int j = 0; j < l; j++) {
//                    fd = fd.append(a[j] + "\n");
//                }
//
//                sd = fd.toString();
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            } catch (InvalidFormatException e) {
//
//                e.printStackTrace();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//            return sd;
//        }
//
//        public void tokenization(String tokens) {
//
//            InputStream is;
//            TokenizerModel tm;
//            try {
//                is = new FileInputStream("/storage/emulated/0/en-token.bin");
//                tm = new TokenizerModel(is);
//                Tokenizer tz = new TokenizerME(tm);
//                Tokens = tz.tokenize(tokens);
//
//                for (int i = 0; i < Tokens.length; i++) {
//                    Log.d(TAG, "tokens: " + Tokens[i]);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    public class extractOCR extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDial = new ProgressDialog(SaveCardActivity.this);

        @Override
        protected void onPreExecute() {
            pDial.setIcon(R.drawable.appicon);
            pDial.setMessage("Extracting Details");
            pDial.setCancelable(false);
            pDial.setTitle("Scanning Card");
            pDial.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap binarizedBitmap = GetBinaryBitmap(cardBitmap);
            TessBaseAPI baseAPI = new TessBaseAPI();
            baseAPI.setDebug(true);
            String tempUri = "";
            Log.d(TAG, "dataSetUrl: " + dataSetUrl);
            for (int i = 0; i < dataSetUrl.length(); i++) {
                if (i >= 7) {
                    if (dataSetUrl.charAt(i) == 't' && dataSetUrl.charAt(i + 1) == 'e' && dataSetUrl.charAt(i + 2) == 's' && dataSetUrl.charAt(i + 3) == 's') {
                        break;
                    } else {
                        tempUri += dataSetUrl.charAt(i);
                    }
                }
            }

            Log.d(TAG, "uri: " + tempUri);
            baseAPI.init(tempUri, "eng");            //content://downloads/my_downloads/1620
            baseAPI.setImage(binarizedBitmap);
            String binaryText = baseAPI.getUTF8Text();
            baseAPI.end();

            Log.d(TAG, "OCR text: " + binaryText);
            if (MainActivity.lang.equalsIgnoreCase("eng")) {
                String temp = binaryText.replaceAll("[^a-zA-Z0-9]+", " ");
                Log.d(TAG, "tempStr: " + temp);
            }

            // PHONE NO. EXTRACTION(PNE)

            try {
                Pattern regex = Pattern.compile("(?:\\d+\\s*)+");
                Matcher regexMatcher = regex.matcher(binaryText);

                int i = 0;
                while (regexMatcher.find()) {
                    Log.d(TAG, "phoneNo: " + regexMatcher.group() + " ,,,,,,,,regexMatcher.start: " + regexMatcher.start() + " ,,,,,,,,regexMatcher.end: " + regexMatcher.end());
                    if (regexMatcher.group().length() > 7) {
                        entities.put("Phone" + (i++), regexMatcher.group());
                        // is a potential phone no.
                    }
                }
            } catch (PatternSyntaxException ex) {
                ex.printStackTrace();
                // Syntax error
            }

            //PhoneNoExtraction ENDS

            // EMAILS EXTRACTION START

//            int i = 0;
//            Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)", Pattern.CASE_INSENSITIVE);
//            Matcher emailMatcher = ptr.matcher(binaryText);
//            while (emailMatcher.find()) {
//                Log.d(TAG, "Email: " + emailMatcher.group() + "\n" + emailMatcher.start() + "\n" + emailMatcher.end());
//                entities.put("Email" + (i++), emailMatcher.group()+".com");
//            }
//
//            Log.d(TAG, "emailOver");

            int no = 0;
            for (int i = 0; i < binaryText.length(); i++) {
                if (binaryText.charAt(i) == '@') {
                    String email = "";
                    int x = 0;
                    int y = i;
                    while (x != 1) {
                        if (binaryText.charAt(y) == ' ') {
                            x = 1;
                            for (int d = y; d <= i; d++) {
                                email += binaryText.charAt(d);
                            }
                        } else {
                            y--;
                        }
                    }

                    y = i + 1;
                    x = 0;

                    while (x != 1) {
                        if (binaryText.charAt(y - 2) == 'o' && binaryText.charAt(y - 1) == 'm') {
                            x = 1;
                        } else {
                            email += binaryText.charAt(y);
                            y++;
                        }
                    }

                    entities.put("Email" + (no++), email);
                }
            }

            // EMAILS EXTRACTION ENDS

            // WEB PAGE EXTRACTION BEGINS
            Pattern ptr1 = Pattern.compile("(http:\\/\\/|https:\\/\\/|www.)?[a-z]{5}.?([a-z]+)?(.com|.in|.edu|.ca|.usa)$");
            Matcher webpageMatcher = ptr1.matcher(binaryText);

            int j = 0;
            while (webpageMatcher.find()) {
                Log.d(TAG, "website: " + webpageMatcher.group() + "\n" + webpageMatcher.start() + "\n" + webpageMatcher.end());
                entities.put("Website" + (j++), webpageMatcher.group());
            }

            //WEB PAGE EXTRACTION ENDS

//            SaveCardActivity.tikaOpenIntro toi = new SaveCardActivity.tikaOpenIntro();
//
//            toi.tokenization(binaryText);           // try here the converted text also

//            String names = toi.namefind(toi.Tokens);
//            String org = toi.orgFind(toi.Tokens);
//            String location = toi.locationFind(toi.Tokens);
//            if (names != null) {
//                entities.put("Name", names);
//            }

//            entities.put("CompanyName", org);
//            entities.put("CompanyAdd", location);

//            Log.d(TAG, "person name is : " + names);
//            Log.d(TAG, "organization name: " + org);
//            Log.d(TAG, "location is: " + location);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new EntryDetailsRVAdapter(getDataSet(), SaveCardActivity.this);
            rvEntryDetails.setAdapter(adapter);
            pDial.dismiss();

            Snackbar snackbar = Snackbar
                    .make(cord, "Select the ticks on right to save entries", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();

            super.onPostExecute(aVoid);
        }
    }
}