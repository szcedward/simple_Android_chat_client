package com.test.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "dashDBAndroid";
 
    // MessageRecords table name
    private static final String TABLE_MESSAGE_RECORDS = "messageRecords";
 
    // MessageRecords Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER_IP = "sender_ip";
    private static final String KEY_RECEIVER_IP = "receiver_ip";
    private static final String KEY_TIME = "time";
    private static final String KEY_CONTENT = "content";
    
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables When DB is Created
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d("Create", "creating table...");
        String CREATE_MESSAGE_RECORDS_TABLE = "CREATE TABLE " 
        		+ TABLE_MESSAGE_RECORDS + "("
                + "id" + " integer primary key, " 
        		+ "sender_ip" + " text not null, " 
                + "receiver_ip" + " text not null, " 
        		+ "time" + " text not null, " 
                + "content" + " text not null)";
        //String CREATE_MESSAGE_RECORDS_TABLE = "create table messageRecord(id integer primary key, sender_ip text not null, receiver_ip text not null, received_time text not null, message_content text not null);";
        db.execSQL(CREATE_MESSAGE_RECORDS_TABLE);
        
    }
    
 // Creating Tables When DB is Opened
    @Override
    public void onOpen(SQLiteDatabase db) {
    	if(!isTableExists(db, TABLE_MESSAGE_RECORDS))
    	{
    		Log.d("Create", "Creating table...");
    		String CREATE_MESSAGE_RECORDS_TABLE = "CREATE TABLE " 
    				+ TABLE_MESSAGE_RECORDS + "("
    				+ "id" + " integer primary key, " 
    				+ "sender_ip" + " text not null, " 
    				+ "receiver_ip" + " text not null, " 
    				+ "time" + " text not null, " 
    				+ "content" + " text not null)";
    		//String CREATE_MESSAGE_RECORDS_TABLE = "create table messageRecord(id integer primary key, sender_ip text not null, receiver_ip text not null, received_time text not null, message_content text not null);";
    		db.execSQL(CREATE_MESSAGE_RECORDS_TABLE);
    		Log.d("Create", "Done.");
    	}
    	else
    	{
    		Log.d("Create", "Table already exits.");
    	}
        
    }
    
    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_RECORDS);
 
        // Create tables again
        onCreate(db);
    }
    
    
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new contact
    public void addMessageRecord(MessageRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_SENDER_IP, record.getSenderIp()); // sender ip
        values.put(KEY_RECEIVER_IP, record.getReceiverIp()); // receiver ip
        values.put(KEY_TIME, record.getTime()); //time
        values.put(KEY_CONTENT, record.getContent()); //content
 
        // Inserting Row
        db.insert(TABLE_MESSAGE_RECORDS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    public MessageRecord getMessageRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_MESSAGE_RECORDS, new String[] { KEY_ID,
                KEY_SENDER_IP, KEY_RECEIVER_IP, KEY_TIME, KEY_CONTENT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        MessageRecord record = new MessageRecord(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return contact
        return record;
    }
     
    // Getting All Contacts
    public List<MessageRecord> getAllMessageRecords() {
        List<MessageRecord> recordList = new ArrayList<MessageRecord>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE_RECORDS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MessageRecord record = new MessageRecord();
                record.setId(Integer.parseInt(cursor.getString(0)));
                record.setSenderIp(cursor.getString(1));
                record.setReceiverIp(cursor.getString(2));
                record.setTime(cursor.getString(3));
                record.setContent(cursor.getString(4));
                // Adding contact to list
                recordList.add(record);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return recordList;
    }
 
    // Updating single contact
    public int updateMessageRecord(MessageRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_SENDER_IP, record.getSenderIp()); // sender ip
        values.put(KEY_RECEIVER_IP, record.getReceiverIp()); // receiver ip
        values.put(KEY_TIME, record.getTime()); //time
        values.put(KEY_CONTENT, record.getContent()); //content
 
        // updating row
        return db.update(TABLE_MESSAGE_RECORDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(record.getId()) });
    }
 
    // Deleting single contact
    public void deleteMessageRecord(MessageRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGE_RECORDS, KEY_ID + " = ?",
                new String[] { String.valueOf(record.getId()) });
        db.close();
    }
 
 
    // Getting contacts Count
    public int getMessageRecordsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGE_RECORDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
    
    public void deleteTable(String tableName)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.rawQuery("drop table if exists messageRecords", null);
    }
    
    public List<String> getColumnNames(String tableName)
    {
    	List<String> colNames = new ArrayList<String>();
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor ti = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if ( ti.moveToFirst() ) {
            do {
                //System.out.println("col: " + ti.getString(1));
                colNames.add(ti.getString(1));
            } while (ti.moveToNext());
        }
        
        return colNames;
    }
    

}
