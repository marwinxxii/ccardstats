package com.marwinxxii.ccardstats.gui;

import java.util.ArrayList;

import com.marwinxxii.ccardstats.CardInfo;
import com.marwinxxii.ccardstats.DatabaseHelper;
import com.marwinxxii.ccardstats.SmsNotificationReader;
import com.marwinxxii.ccardstats.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class CardListActivity extends Activity {
    
    private Dialog mProgressDialog;
    private ListView mCardsList;
    private static ArrayList<CardInfo> mCards;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards);
        mCardsList=(ListView)findViewById(R.id.cards_list_list);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        /*if (mCards != null) {
            setCardListAdapter();
            return;
        }*/
        mProgressDialog = ProgressDialog.show(this,
                getString(R.string.read_sms_dialog_title),
                getString(R.string.read_sms_dialog_message));
        mProgressDialog.show();
        new ReadSmsTask().execute();
    }
    
    private void setCardListAdapter() {
        CardItemAdapter adapter = new CardItemAdapter(this, R.layout.card_item, mCards);
        mCardsList.setAdapter(adapter);
    }
    
    public class ReadSmsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = getApplicationContext();
            DatabaseHelper helper = new DatabaseHelper(context);
            mCards = helper.getCardsWithInfo();
            if (helper.wasCreated()) {
                SmsNotificationReader reader = SmsNotificationReader.getReader(context);
                mCards=new ArrayList<CardInfo>(helper.readCardInfoFromSMS(reader));
            }
            helper.close();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            setCardListAdapter();
            mProgressDialog.dismiss();
        }

    }
}
