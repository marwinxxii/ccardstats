package com.marwinxxii.ccardstats.gui;

import java.util.ArrayList;
import java.util.List;

import com.marwinxxii.ccardstats.R;
import com.marwinxxii.ccardstats.db.Card;
import com.marwinxxii.ccardstats.db.DBHelper;
import com.marwinxxii.ccardstats.helpers.DateHelper;
import com.marwinxxii.ccardstats.notifications.NotificationReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class CardListActivity extends Activity {

    private static final int[] ids = { R.id.card_item_name,
            R.id.card_item_total_in, R.id.card_item_total_out,
            R.id.card_item_month_in, R.id.card_item_month_out,
            R.id.card_item_today_in, R.id.card_item_today_out,
            R.id.card_item_balance };

    private Dialog mProgressDialog;
    private ListView mCardsList;
    public static List<String[]> values;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards);
        mCardsList = (ListView) findViewById(R.id.cards_list_list);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (values != null) {
            setCardListAdapter();
            return;
        }
        mProgressDialog = ProgressDialog.show(this,
                getString(R.string.read_sms_dialog_title),
                getString(R.string.read_sms_dialog_message));
        mProgressDialog.show();
        new ReadSmsTask().execute();
    }

    private void setCardListAdapter() {
        CardItemAdapter adapter = new CardItemAdapter(this, R.layout.card_item,
                ids, values);
        mCardsList.setAdapter(adapter);
    }

    public static void prepareCardsInfo(DBHelper helper, List<Card> cards) {
        values = new ArrayList<String[]>();
        for (Card c : cards) {
            String[] buf = new String[8];
            double[] vals = helper.getAllStats(c, DateHelper.year,
                    DateHelper.month, DateHelper.day);
            buf[0] = c.getAlias();
            for (int i = 0; i < vals.length; i += 2) {
                String format = vals[i] < 0.01 ? "%.2f" : "+%.2f";
                buf[i + 1] = String.format(format, vals[i]);
                format = vals[i + 1] < 0.01 ? "%.2f" : "-%.2f";
                buf[i + 2] = String.format(format, vals[i + 1]);
            }
            buf[7] = String.format("%.2f", c.getBalance());
            values.add(buf);
        }
    }

    public class ReadSmsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = getApplicationContext();
            DBHelper helper = new DBHelper(context);
            List<Card> cards = helper.getCards();
            if (helper.wasCreated()) {
                NotificationReader reader = new NotificationReader(context,
                        helper);
                reader.readNotificationsToDB();
                cards = helper.getCards();
            }
            prepareCardsInfo(helper, cards);
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
