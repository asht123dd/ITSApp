package com.sdl.dart.itsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashutosh on 6/1/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=14;
    private static final String DATABASE_NAME="mpdb";
    private static final String TABLE_QUOTES="quotes";
    private static final String TABLE_RETAIL="retailers";
    private static final String TABLE_FARM="farmers";
    private static final String COMMODITY="commod";
    private static final String QUANTITY="quant";
    private static final String PRICE="price";
    private static final String RID="rid";
    private static final String FID="fid";
    private static final String NAME="name";
    private static final String PHONE="phone";
    private static final String LID="lid";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_RETAIL_TABLE="CREATE TABLE "+TABLE_RETAIL+" ("+RID+" INTEGER,"+NAME+" VARCHAR(30),"+LID+" INTEGER)";
        String CREATE_QUOTES_TABLE = "CREATE TABLE "+TABLE_QUOTES+" ("+COMMODITY+" VARCHAR(15),"+QUANTITY+" INTEGER,"+PRICE+" REAL,"+RID+" INTEGER references "+TABLE_RETAIL+")";
        String CREATE_FARM_TABLE="CREATE TABLE "+TABLE_FARM+" ("+FID+" INTEGER,"+NAME+" VARCHAR(30),"+PHONE+" VARCHAR(15),"+LID+" INTEGER)";
        String INSERT_RETAIL="INSERT INTO "+TABLE_RETAIL+" values(1,\"Alankar Foods\",1),(2,\"Shri Ram Foods\",3),(3,\"Sandeep Foods\",4)";
        String INSERT_QUOTES="INSERT INTO "+TABLE_QUOTES+" VALUES(\"WHEAT\",1,12230.08,1),(\"WHEAT\",2,12230,2),(\"WHEAT\",3,12000,3)";
        String INSERT_FARM="INSERT INTO "+TABLE_FARM+" VALUES(1,\"Ashutosh Singh\",\"+917018074728\",2),(2,\"Ashirvad Singh\",\"+919309829236\",5)";
        sqLiteDatabase.execSQL(CREATE_RETAIL_TABLE);
        sqLiteDatabase.execSQL(CREATE_QUOTES_TABLE);
        sqLiteDatabase.execSQL(CREATE_FARM_TABLE);
        sqLiteDatabase.execSQL(INSERT_RETAIL);
        sqLiteDatabase.execSQL(INSERT_QUOTES);
        sqLiteDatabase.execSQL(INSERT_FARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RETAIL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FARM);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
    public List<Quotes> getAllQuotes(String cname)
    {
        List<Quotes> quoteList = new ArrayList<Quotes>();
        String selectQuery = "SELECT  * FROM " + TABLE_QUOTES + " where "+COMMODITY+"="+"\""+cname+"\";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
        {
            do {
                Quotes quote=new Quotes();
                quote.setCommod(cursor.getString(0));
                quote.setQuant(cursor.getInt(1));
                quote.setPrice(cursor.getFloat(2));
                quote.setRid(cursor.getInt(3));
                quoteList.add(quote);
            }while(cursor.moveToNext());
        }
        return quoteList;
    }
    public int getLIDFarm(String phone)
    {
        String selectQuery="SELECT "+LID+" FROM "+TABLE_FARM+" WHERE "+PHONE+"=\""+phone+"\";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
        {
            return cursor.getInt(0);
        }
        else
            return 0;
    }
    public int getLIDRetail(int rid)
    {
        String selectQuery="SELECT "+LID+" FROM "+TABLE_RETAIL+" WHERE "+RID+"="+rid+";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
        {
            return cursor.getInt(0);
        }
        else
            return 0;
    }

}


