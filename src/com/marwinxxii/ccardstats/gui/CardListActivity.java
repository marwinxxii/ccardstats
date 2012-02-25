package com.marwinxxii.ccardstats.gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.marwinxxii.ccardstats.Card;
import com.marwinxxii.ccardstats.DatabaseHelper;
import com.marwinxxii.ccardstats.DateHelper;
import com.marwinxxii.ccardstats.SberbankSmsReader;
import com.marwinxxii.ccardstats.SmsNotification;
import com.marwinxxii.sberbank.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class CardListActivity extends Activity {
    
    private Dialog mProgressDialog;
    private ListView mCardsList;
    private static ArrayList<Card> mCards;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards);
        mCardsList=(ListView)findViewById(R.id.cards_list_list);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (mCards != null) return;
        Context context = getApplicationContext();
        DatabaseHelper helper = new DatabaseHelper(context);
        mCards = helper.getCards();
        if (helper.wasCreated()) {
            mProgressDialog = ProgressDialog.show(this,
                    getString(R.string.read_sms_dialog_title),
                    getString(R.string.read_sms_dialog_message));
            helper.close();
            new ReadSmsTask().execute(helper);
            mProgressDialog.show();
        } else {
            helper.close();
            setCardListAdapter();
        }
    }
    
    private void setCardListAdapter() {
        CardItemAdapter adapter = new CardItemAdapter(this,
                R.layout.card_item, mCards);
        mCardsList.setAdapter(adapter);
    }
    
    public class ReadSmsTask extends AsyncTask<DatabaseHelper, Void, Void> {

        @Override
        protected Void doInBackground(DatabaseHelper... params) {
            DatabaseHelper helper = params[0];
            SberbankSmsReader reader = new SberbankSmsReader(
                    getApplicationContext());
            try {
                helper.initForWrite();
            } catch(SQLiteException e) {
                return null;
            }
            SmsNotification notif;
            HashMap<String, Card> cards = new HashMap<String, Card>();
            while ((notif=reader.getNext()) != null) {
                Card c = cards.get(notif.getCard());
                if (c == null) {
                    //check that available > 0 ?
                    c = helper.addCard(notif.getCard(), notif.getAvailable());
                    cards.put(notif.getCard(), c);
                }
                if (notif.getAmount() < 0) {
                    c.outcome += -notif.getAmount();
                } else {
                    c.income += notif.getAmount();
                }
                c.available = notif.getAvailable();
                if (DateHelper.isInCurrentMonth(notif.getDate())) {
                    helper.insertNotification(notif);
                }
            }
            helper.close();
            mCards = new ArrayList<Card>(cards.values());
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            setCardListAdapter();
            mProgressDialog.dismiss();
            if (mCards.size() == 0) {
                Toast.makeText(getApplicationContext(), "empty", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getApplicationContext(), "non empty", Toast.LENGTH_SHORT);
            }
        }

    }
}
