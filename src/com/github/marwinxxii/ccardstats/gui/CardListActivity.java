package com.github.marwinxxii.ccardstats.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.marwinxxii.ccardstats.db.Card;
import com.github.marwinxxii.ccardstats.db.DBHelper;
import com.github.marwinxxii.ccardstats.helpers.DateHelper;
import com.github.marwinxxii.ccardstats.helpers.MoneyHelper;
import com.github.marwinxxii.ccardstats.notifications.NotificationReader;
import com.github.marwinxxii.ccardstats.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CardListActivity extends SimpleListActivity implements OnItemLongClickListener,
    OnItemClickListener {

    private static final int[] ids = { R.id.card_item_name,
            R.id.card_item_total_in, R.id.card_item_total_out,
            R.id.card_item_month_in, R.id.card_item_month_out,
            R.id.card_item_today_in, R.id.card_item_today_out,
            R.id.card_item_balance };

    private static List<Card> cards;
    private static Map<Integer, List<Integer>> cardYears;
    private int selectedCardIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogParams(R.string.read_sms_dialog_title,
                getString(R.string.read_sms_dialog_message));
        mItemsList.setOnItemLongClickListener(this);
        mItemsList.setOnItemClickListener(this);
        registerForContextMenu(mItemsList);
    }
    
    @Override
    public void setListTitle() {
        super.setTitleResId(R.string.cards_list_title);
    }
    
    @Override
    protected void getItems() {
        new ReadSmsTask().execute();
    }
    
    @Override
    protected int getItemLayout() {
        return R.layout.card_item;
    }
    
    @Override
    protected int[] getItemFieldsIds() {
        return ids;
    }

    public static List<String[]> prepareCardsInfo(DBHelper helper, List<Card> cards) {
        List<String[]> values = new ArrayList<String[]>();
        cardYears = new HashMap<Integer, List<Integer>>(cards.size());
        int k=0;
        for (Card c : cards) {
            String[] buf = new String[8];
            double[] vals = helper.getAllStats(c, DateHelper.year,
                    DateHelper.month, DateHelper.day);
            buf[0] = c.getAlias();
            for (int i = 0; i < vals.length; i += 2) {
                buf[i+1] = MoneyHelper.formatMoney(vals[i], true);
                buf[i+2] = MoneyHelper.formatMoney(vals[i+1], false);
            }
            buf[7] = String.format("%.2f", c.getBalance());
            values.add(buf);
            cardYears.put(k++, helper.getYears(c));
        }
        return values;
    }

    public class ReadSmsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = getApplicationContext();
            DBHelper helper = new DBHelper(context);
            cards = helper.getCards();
            if (helper.wasCreated()) {
                NotificationReader reader = new NotificationReader(context, helper);
                reader.readNotificationsToDB();
                cards = helper.getCards();
            }
            cacheValues(prepareCardsInfo(helper, cards));
            if (DBHelper.storeMonth) helper.deleteOldEntries(cards);
            helper.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter();
            progressDialog.dismiss();
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        selectedCardIndex = position;
        parent.performLongClick();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(MonthStatsActivity.getStartingIntent(this, cards.get(position).getName(),
                DateHelper.year, DateHelper.month));
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card, menu);
        menu.setHeaderTitle(R.string.card_item_menu_title);
        List<Integer> years = cardYears.get(selectedCardIndex);
        for (Integer year:years) {
            menu.add(year.toString());
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String card = cards.get(selectedCardIndex).getName();
        int year = Integer.parseInt(item.getTitle().toString());
        startActivity(YearStatsActivity.getStartingIntent(this, card, year));
        return true;
    }
}
