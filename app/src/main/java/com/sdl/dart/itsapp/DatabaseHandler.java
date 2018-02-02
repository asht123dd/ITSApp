package com.sdl.dart.itsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ashutosh on 6/1/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=30;
    private static final String DATABASE_NAME="mpdb";
    private static final String TABLE_QUOTES="quotes";
    private static final String TABLE_RETAIL="retailers";
    private static final String TABLE_FARM="farmers";
    private static final String TABLE_QUERY="querytab";
    private static final String TABLE_TRANSACTION="trans";
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
        String CREATE_QUERY_TABLE="CREATE TABLE "+TABLE_QUERY+" ("+PHONE+" VARCHAR(15),"+COMMODITY+" VARCHAR(15))";
        String CREATE_TRANSACTION_TABLE="CREATE TABLE "+TABLE_TRANSACTION+"("+RID+" integer,"+FID+" INTEGER,"+COMMODITY+" VARCHAR2(15),"+QUANTITY+" INTEGER,"+PRICE+" REAL);";
        String INSERT_RETAIL="INSERT INTO "+TABLE_RETAIL+" values(1,\"Alankar Foods\",1),(2,\"Shri Ram Foods\",3),(3,\"Sandeep Foods\",4)";
        String INSERT_QUOTES="INSERT INTO "+TABLE_QUOTES+" VALUES(\"WHEAT\",1,12230.08,1),(\"WHEAT\",2,12230,2),(\"WHEAT\",3,12000,3),(\"POTATO\",1,12226.77,1),(\"POTATO\",2,12226,2),(\"POTATO\",3,12200,3)";
        String INSERT_FARM="INSERT INTO "+TABLE_FARM+" VALUES(1,\"Ashutosh Singh\",\"+917018074728\",2),(2,\"Ashirvad Singh\",\"+919309829236\",5),(3,\"Lavkush Kumar Singh\",\"+919767681142\",6)";
       // String INSERT_QUOTES="INSERT INTO "+TABLE_QUOTES+" VALUES(\"WHEAT\",1,12230.08,1),(\"WHEAT\",2,12230,2),(\"WHEAT\",3,12000,3)";
        sqLiteDatabase.execSQL(CREATE_RETAIL_TABLE);
        sqLiteDatabase.execSQL(CREATE_QUOTES_TABLE);
        sqLiteDatabase.execSQL(CREATE_FARM_TABLE);
        sqLiteDatabase.execSQL(CREATE_QUERY_TABLE);
        sqLiteDatabase.execSQL(INSERT_RETAIL);
        sqLiteDatabase.execSQL(INSERT_QUOTES);
        sqLiteDatabase.execSQL(INSERT_FARM);
        sqLiteDatabase.execSQL(CREATE_TRANSACTION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RETAIL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FARM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUERY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

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
    public void addQuery(String phone,String commodity)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_QUERY+" where "+PHONE+"='"+phone+"';");
        ContentValues values=new ContentValues();
        values.put(PHONE,phone);
        values.put(COMMODITY,commodity);
        db.insert(TABLE_QUERY,null,values);
        Log.v("Indicator","Query added with values of phone and commodity as= "+phone+commodity);
        db.close();
    }
    public String getCommod(String phone)
    {
        String selectQuery="SELECT "+COMMODITY+" FROM "+TABLE_QUERY+" WHERE "+PHONE+"='"+phone+"';";
        Log.v("Indicator","select Query = "+selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst())
        {
            return cursor.getString(0);
        }
        else
            return "NO QUERY FOUND!";

    }
    public float executeSale(String phone,String commodity,int quantity,Context con)
    {
            SQLiteDatabase db=this.getWritableDatabase();
            Log.v("Indicator","EXECUTE SALE CALLED WITH phone, commodity and quantity="+phone+commodity+quantity);
            List<RefinedQuotes> reflist=new ArrayList<RefinedQuotes>();
            reflist=get3BestQuotes(commodity,phone,con);
        int Rid;
            Rid=reflist.get(0).getRid();


            int Fid=getFID(phone);
            float Price=reflist.get(0).getPrice();
            String sale="UPDATE "+TABLE_QUOTES+" set "+QUANTITY+"=("+QUANTITY+"-"+quantity+") where "+RID+"="+Rid+" and "+COMMODITY+"='"+commodity+"';";
            String transaction="INSERT INTO "+TABLE_TRANSACTION+" VALUES("+Rid+","+Fid+",'"+commodity+"',"+quantity+","+Price+");";
            db.execSQL(sale);
            db.execSQL(transaction);
            return Price;
    }
    public float getPrice(int Rid,String commod)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        float Price=0;
        String query="select "+PRICE+" from "+TABLE_QUOTES+" where "+RID+"="+Rid+" and "+COMMODITY+"='"+commod+"';";
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            Price=cursor.getInt(0);
        }
        return Price;
    }
    public int getFID(String phone)
    {
        String Query="Select "+FID+" from "+TABLE_FARM+" where "+PHONE+"='"+phone+"';";
        SQLiteDatabase db=this.getReadableDatabase();
        int result=0;
        Cursor cursor=db.rawQuery(Query,null);
        if(cursor.moveToFirst())
        {
            result=cursor.getInt(0);
        }
        return result;
    }
    public int getBestRid(String cname,String phone,Context con)
    {
        Log.v("Indicator","get best rid CALLED WITH phone, commodity ="+phone+cname);
        /* List<Quotes> quoteList = new ArrayList<Quotes>();
        List<RefinedQuotes> reflist=new ArrayList<RefinedQuotes>();
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
        RefinedQuotes ref;
        for(Quotes quote:quoteList)
        {

            ref=new RefinedQuotes(quote,con,phone);
            reflist.add(ref);
            //sms_send+=ref.getQuant()+" ton(s) of potatoes can be sold for ₹ "+ref.getPrice()+" per ton. ";
        }
        Collections.sort(reflist);




        Log.v("RID",Integer.toString(reflist.get(0).getRid()));
        return reflist.get(0).getRid();*/
        Log.v("Indicator","GET 3 BEST QUOTES CALLED IN DATABASE HANDLER CLASS!!!");
       List<RefinedQuotes> reflist=get3BestQuotes(cname,phone,con);
               return reflist.get(0).getRid();
    }
    public List<RefinedQuotes> get3BestQuotes(String cname,String phone,Context con)
    {
        Log.v("Indicator","get 3 best quotes CALLED WITH phone, commodity ="+phone+cname);
        List<Quotes> quoteList = new ArrayList<Quotes>();
        List<RefinedQuotes> reflist=new ArrayList<>();
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
        if(quoteList.isEmpty())
            Log.v("Indicator","QUOTE LIST IS EMPTY!!");
        RefinedQuotes ref;
        for(Quotes quote:quoteList)
        {
            Log.v("Indicator","FOR EACH EXECUTES!!");
            ref=new RefinedQuotes(quote,con,phone);
            reflist.add(ref);
            if(reflist.isEmpty())
                Log.v("Indicator","IS EMPTY INSIDE FOR EACH!!");
            //sms_send+=ref.getQuant()+" ton(s) of potatoes can be sold for ₹ "+ref.getPrice()+" per ton. ";
        }
        Collections.sort(reflist);
        if(reflist.isEmpty())
            Log.v("Indicator","IS EMPTY!!");
        RefinedQuotes obj = reflist.get(0); // remember first item
        RefinedQuotes obj2 = reflist.get(1);
        RefinedQuotes obj3 = reflist.get(2);
        reflist.clear(); // clear complete list
        reflist.add(obj);
        reflist.add(obj2);
        reflist.add(obj3);
        return reflist;
    }


}


