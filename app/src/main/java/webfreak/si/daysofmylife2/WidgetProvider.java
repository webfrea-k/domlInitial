package webfreak.si.daysofmylife2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static webfreak.si.daysofmylife2.R.layout.widget;

/**
 * Created by simon.hocevar on 18.03.2017.
 */
public class WidgetProvider extends AppWidgetProvider
{

    // String to be sent on Broadcast as soon as Data is Fetched
    // should be included on WidgetProvider manifest intent action
    // to be recognized by this WidgetProvider to receive broadcast
    public static final String DATA_FETCHED = "com.wordpress.laaptu.DATA_FETCHED";
    public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "com.wordpress.laaptu.EXTRA_LIST_VIEW_ROW_NUMBER";

    /**
     * this method is called every 30 mins as specified on widgetinfo.xml this
     * method is also called on every phone reboot from this method nothing is
     * updated right now but instead RetmoteFetchService class is called this
     * service will fetch data,and send broadcast to WidgetProvider this
     * broadcast will be received by WidgetProvider onReceive which in turn
     * updates the widget
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++)
        {
            Intent serviceIntent = new Intent(context, RemoteFetchService.class);
            context.startService(serviceIntent);

            long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY", context);
            final DateTime user_birthday = new DateTime(user_birthday_long);
            final DateTime current_date = new DateTime().withHourOfDay(0).withMinuteOfHour(1);
            long diff = current_date.getMillis() - user_birthday.getMillis();
            RemoteViews remoteViews = updateWidgetListView(context);
            remoteViews.setTextViewText(R.id.constantText, "Enjoy your " + String.format(Locale.GERMANY, "%,d", (int) TimeUnit.MILLISECONDS.toDays(diff)) + " day on earth");


            final Intent activityIntent = new Intent(context, MainActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.heading, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        final ComponentName cn = new ComponentName(context, WidgetProvider.class);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn),R.id.words);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context)
    {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), widget);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.words, svcIntent);
        return remoteViews;
    }

    /**
     * It receives the broadcast as per the action set on intent filters on
     * Manifest.xml once data is fetched from RemotePostService,it sends
     * broadcast and WidgetProvider notifies to change the data the data change
     * right now happens on ListProvider as it takes RemoteFetchService
     * listItemList as data
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(DATA_FETCHED))
        {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = updateWidgetListView(context);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        if (intent.getAction().equals(EXTRA_LIST_VIEW_ROW_NUMBER)) {
            Toast.makeText(context, "Clicked on position :", Toast.LENGTH_SHORT).show();
        }

    }
}

/*public class WidgetProvider extends AppWidgetProvider implements LoadJSONTaskQuote.Listener{
    public static String EXTRA_WORD="webfreak.si.daysofmylife2.WORD";
    Context context;
    public static int[] widgetIds;
    public static AppWidgetManager AppWidgetManager;
    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.d("WIDGET CALLED SIMA22","WIDGET CALLED SIMA22");
        context = ctxt;
        widgetIds = appWidgetIds;
        AppWidgetManager = appWidgetManager;
        new LoadJSONTaskQuote(this).execute();
        for (int i=0; i<appWidgetIds.length; i++) {
            Intent svcIntent=new Intent(ctxt, WidgetService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget=new RemoteViews(ctxt.getPackageName(),
                    R.layout.widget);

            widget.setRemoteAdapter(appWidgetIds[i], R.id.words,
                    svcIntent);

            Intent clickIntent=new Intent(ctxt, MainActivity.class);
            PendingIntent clickPI=PendingIntent
                    .getActivity(ctxt, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.words, clickPI);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onLoaded(List<Quote> androidList)
    {
        int totalQuotesCount = androidList.size();
        if(totalQuotesCount > 0)
        {
            Quote quote = androidList.get(new Random().nextInt(totalQuotesCount));
            Utils.putPref("DAILY_QUOTE",quote.getName(),context);
            for (int widgetId : widgetIds) {
                RemoteViews mView = initViews(context, AppWidgetManager, widgetId);
                AppWidgetManager.updateAppWidget(widgetId, mView);
            }
        }
    }
    @Override
    public void onError()
    {
        Utils.putPref("DAILY_QUOTE","No quote...something is wrong/fishy. :/",context);

    }

    private RemoteViews initViews(Context context,
                                  AppWidgetManager widgetManager, int widgetId) {

        RemoteViews mView = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.words, intent);

        return mView;
    }
}



public class WidgetProvider extends AppWidgetProvider implements LoadJSONTaskQuote.Listener
{
    public int count;
    public int[] widgetIds;
    public Context ctx;
    public static String EXTRA_WORD= "webfreak.si.daysofmylife2.WidgetProvider.WORD";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {

        Log.d("WIDGET CALLED SIMA","WIDGET CALLED SIMA");
        count = appWidgetIds.length;
        widgetIds = appWidgetIds;
        DateTime dt = new DateTime();
        ctx = context;
        boolean shouldLoadDailyQuote = true;
        TinyDB db = new TinyDB(context);
        if(db.getListString("PEOPLES").size() > 0)
        {
            for(int i=0;i<db.getListString("PEOPLES").size();i++)
            {
                if(Utils.getPref("WHOS_NEXT_"+db.getListString("PEOPLES").get(i),context).contains("just outlived"))
                {
                    Utils.putPref("DAILY_QUOTE",Utils.getPref("WHOS_NEXT_"+db.getListString("PEOPLES").get(i),context), context);
                }
            }
        }
        if(Utils.getPref("WHOS_NEXT",context).contains("just outlived"))
        {
            Utils.putPref("DAILY_QUOTE", Utils.getPref("WHOS_NEXT",context), context);
        }

        if(Utils.getPrefInt("WIDGET_UPDATE",context) == dt.getDayOfMonth())
        {
            new LoadJSONTaskQuote(this).execute();
            Utils.putPrefInt("WIDGET_UPDATE",dt.getDayOfMonth(), context);
            shouldLoadDailyQuote = false;
        }

        if(shouldLoadDailyQuote)
        {
            updateWidget(ctx, appWidgetIds);
        }
        super.onUpdate(ctx, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onLoaded(List<Quote> androidList)
    {
        int totalQuotesCount = androidList.size();
        if(totalQuotesCount > 0)
        {
            Quote quote = androidList.get(new Random().nextInt(totalQuotesCount));
            Utils.putPref("DAILY_QUOTE",quote.getName(),ctx);
            updateWidget(ctx, widgetIds);
        }
    }

    @Override
    public void onError()
    {
        Utils.putPref("DAILY_QUOTE","No quote...something is wrong/fishy. :/",ctx);
        updateWidget(ctx,widgetIds);
    }
    public void updateWidget(Context context, int[] appWidgetIds)
    {
        int count = appWidgetIds.length;
        for(int i=0; i<count; i++)
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);
            long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY", ctx);
            final DateTime user_birthday = new DateTime(user_birthday_long);
            final DateTime current_date = new DateTime().withHourOfDay(0).withMinuteOfHour(1);
            long diff = current_date.getMillis() - user_birthday.getMillis();
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
            remoteViews.setTextViewText(R.id.constantText, "Enjoy your " + String.format(Locale.GERMANY, "%,d", (int) TimeUnit.MILLISECONDS.toDays(diff)) + " day on earth");
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

            Intent startActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.listViewWidget, startActivityPendingIntent);

            AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(ctx);
            appWidgetManager1.updateAppWidget(widgetIds[i], remoteViews);
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId)
    {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), widget);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_list_item);
        return remoteViews;
    }
}*/