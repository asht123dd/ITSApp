package com.sdl.dart.itsapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
//Horray...

public class MainActivity extends AppCompatActivity {
    DatabaseHandler db;
    TextView sendtv;
    private int mMessageSentParts;
    private int mMessageSentTotalParts;
    private int mMessageSentCount;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED", redundant="";
    int i;
    ArrayList<String> strlist=new ArrayList<>();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendtv=findViewById(R.id.viewmess);
        db = new DatabaseHandler(this);
        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                final String sender=intent.getStringExtra("sender");

                TextView tv = (TextView) findViewById(R.id.txtview);
                tv.setText(sender+message);
                String sms_send ="";
                if(redundant.equalsIgnoreCase(sender))
                    return;
                redundant=sender;

                if(message.equalsIgnoreCase("QUOTE WHEAT"))
                {
                    //sms_send="N/A";
                    i=0;
                    RefinedQuotes ref;
                    List<Quotes> quotesList = db.getAllQuotes("WHEAT");
                    for(Quotes quote:quotesList)
                    {

                        ref=new RefinedQuotes(quote,context,sender);
                        strlist.add(ref.getQuant()+" ton(s) of wheat can be sold for ₹ "+ref.getPrice()+" per ton.");
                        sms_send+=ref.getQuant()+" ton(s) of wheat can be sold for ₹ "+ref.getPrice()+" per ton, ";
                    }

                }
                else if(message.equalsIgnoreCase("QUOTE POTATOES"))
                {
                    //sms_send="N/A";
                   i=0;
                    RefinedQuotes ref;
                    List<Quotes> quotesList = db.getAllQuotes("POTATO");
                    for(Quotes quote:quotesList)
                    {

                        ref=new RefinedQuotes(quote,context,sender);
                        strlist.add(ref.getQuant()+" ton(s) of potatoes can be sold for ₹ "+ref.getPrice()+" per ton.");
                        sms_send+=ref.getQuant()+" ton(s) of potatoes can be sold for ₹ "+ref.getPrice()+" per ton. ";
                    }

                }
                sendtv.setText(sms_send);
                for(String str:strlist)
                {

                    sendSMS(sender,str);
                }
               // sendSMS(sender,sms_send.toString());
            }
        }
    };
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }
    //---sends an SMS message to another device---
    @SuppressWarnings("deprecation")
    private void sendSMS(String phoneNumber, String message)
    {
        Log.v("phoneNumber",phoneNumber);
        Log.v("message",message);
        Log.v("i",Integer.toString(i));
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this,Dummy.class), 0);
        SmsManager sms = SmsManager.getDefault();
        if(i>2)
            return;
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        ++i;
    }

  /*  @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }*/
}
