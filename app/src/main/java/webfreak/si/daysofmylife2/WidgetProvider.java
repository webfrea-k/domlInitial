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
