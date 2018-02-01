package webfreak.si.daysofmylife2;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by simon.hocevar on 20.04.2017.
 */

public class RemoteFetchService extends Service
{

    //private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AQuery aquery;
    private String remoteJsonUrl = "https://webfreak.si/daysofmylifequotes.json";

    public static ArrayList<String> listItemList;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Retrieve appwidget id from intent it is needed to update widget later
     * initialize our AQuery class
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DateTime dt = new DateTime();
        if(Utils.getPrefInt("WIDGET_UPDATE",getApplicationContext()) != dt.getDayOfMonth())
        {
            Utils.putPrefInt("WIDGET_UPDATE",dt.getDayOfMonth(), getApplicationContext());
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            {

                if(!Utils.isAnybodyHavingBirthday(getApplicationContext()))
                {
                    //appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                    aquery = new AQuery(getBaseContext());
                    fetchDataFromWeb();
                }
                else
                {
                    processResult("NO_UPDATE");
                }
            }
        }
        else
        {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            {
                //appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
            processResult("NO_UPDATE");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * method which fetches data(json) from web aquery takes params
     * remoteJsonUrl = from where data to be fetched String.class = return
     * format of data once fetched i.e. in which format the fetched data be
     * returned AjaxCallback = class to notify with data once it is fetched
     */
    private void fetchDataFromWeb()
    {
        aquery.ajax(remoteJsonUrl, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                processResult(result);
                super.callback(url, result, status);
            }
        });
    }

    /**
     * Json parsing of result and populating ArrayList<ListItem> as per json
     * data retrieved from the string
     */
    private void processResult(String result)
    {

        listItemList = new ArrayList<String>();
        if(result.equals("NO_UPDATE"))
        {
            listItemList.add(Utils.getPref("DAILY_QUOTE",getApplicationContext()));
        }
        else
        {
            try
            {
                JSONObject jsnobject = new JSONObject(result);
                JSONArray jsonArray = jsnobject.getJSONArray("quote");
                //JSONArray jsonArray = new JSONArray(result);
                int length = jsonArray.length();
                int picked = new Random().nextInt(length-1);
                JSONObject jsonObject = jsonArray.getJSONObject(picked);
                listItemList.add(jsonObject.getString("name"));
                Utils.putPref("DAILY_QUOTE",jsnobject.getString("name"),getApplicationContext());

            }
            catch (JSONException e)
            {
                e.printStackTrace();

            }
        }
        populateWidget();
    }

    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == WidgetProvider.DATA_FETCHED
     */
    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
        //widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }
}

