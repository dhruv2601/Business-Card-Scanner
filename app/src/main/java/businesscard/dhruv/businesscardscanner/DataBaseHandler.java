package businesscard.dhruv.businesscardscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhruv on 7/1/17.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "contactsManager";   //database name
    public static final String TABLE_CONTACTS = "contacts";         //table name

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone_number";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_NAME + " TEXT," +
                KEY_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // drops table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // create tables again
        onCreate(db);
    }

    public void clearDataBase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS,null,null);
//        db.execSQL("DELETE FROM "+TABLE_CONTACTS);
        db.close();
    }


    public void addContact(CardObjectContacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getTxtName());
        values.put(KEY_PH_NO, contact.getTxtNum());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    public CardObjectContacts getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID, KEY_NAME, KEY_PH_NO},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        CardObjectContacts contact = new CardObjectContacts(cursor.getString(1), cursor.getString(2));
        return contact;
    }

    public ArrayList<CardObjectContacts> getAllContacts() {
        ArrayList<CardObjectContacts> contactList = new ArrayList<CardObjectContacts>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CardObjectContacts contact = new CardObjectContacts(cursor.getString(1), cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateContact(CardObjectContacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        int id = 1; // we dont have a method to get the number at which it is to be updated yet, vo mil jaye to table mn insertion ho jayega aaram se
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getTxtNum());
        values.put(KEY_PH_NO, contact.getTxtNum());

        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteContact(CardObjectContacts contact)
    {
        int id =1; // we dont have a method to get the number at which it is to be updated yet, vo mil jaye to table mn deletion ho jayega aaram se
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS,KEY_ID+" = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }


}
