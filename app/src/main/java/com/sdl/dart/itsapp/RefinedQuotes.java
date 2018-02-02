package com.sdl.dart.itsapp;

import android.content.Context;

/**
 * Created by ashutosh on 6/1/18.
 */

public class RefinedQuotes implements Comparable<RefinedQuotes>{
    DatabaseHandler db;
    private String commod;
    private int quant, lid1, lid2,rid;
    private float price;
    private Context context;

    public RefinedQuotes(Quotes q, Context con,String phone) {
        super();
        this.context=con;
        db = new DatabaseHandler(con);
        commod=q.getCommod();
        quant=q.getQuant();
        rid=q.getRid();
        lid1=db.getLIDFarm(phone);
        lid2=db.getLIDRetail(q.getRid());
        price=(q.getPrice()-(Math.abs(lid2-lid1)*8));
    }

    public String getCommod() {
        return commod;
    }

    public void setCommod(String commod) {
        this.commod = commod;
    }

    public int getQuant() {
        return quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
    }

    public float getPrice() {
        return price;
    }
    public int getRid(){return rid;}

    public int compareTo(RefinedQuotes compareRefinedQuotes)
    {
        float compareQuantity=((RefinedQuotes)compareRefinedQuotes).getPrice();
        //descending order
        return (int)(compareRefinedQuotes.getPrice()-this.getPrice());
    }
}
