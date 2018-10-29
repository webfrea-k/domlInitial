package webfreak.si.daysofmylife2;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by simon.hocevar on 13.04.2017.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory{
    private ArrayList<String> listItemList = new ArrayList<String>();
    private Context context = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.row);
        String listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.heading, listItem);

        //onclick item listview
        Bundle extras = new Bundle();
        extras.putInt(WidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        //Intent fillInIntent = new Intent();
        //fillInIntent.putExtra(WidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER, position);
        //remoteView.setOnClickFillInIntent(R.id.heading, fillInIntent);

        Bundle infos = new Bundle();

        final Intent activityIntent = new Intent();
        activityIntent.putExtras(infos);

        remoteView.setOnClickFillInIntent(R.id.heading, activityIntent);
        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}