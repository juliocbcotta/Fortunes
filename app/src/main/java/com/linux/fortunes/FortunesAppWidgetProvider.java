package com.linux.fortunes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.linux.fortunes.app.FortunesApplication;
import com.linux.fortunes.app.R;
import com.linux.fortunes.bean.FortuneBean;
import com.linux.fortunes.model.Fortune;

public class FortunesAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ((FortunesApplication.ACTION_UPDATE_CLICK).equals(intent.getAction())) {
            Fortune fortune = (Fortune) intent.getSerializableExtra(FortuneBean.TABLE_NAME);
            if (fortune == null) {
                fortune = FortuneBean.getRandomFortune();
            }
            onUpdate(context, fortune);
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Fortune fortune = FortuneBean.getRandomFortune();
        updateWidgets(context, appWidgetManager, appWidgetIds, fortune);
    }

    private void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Fortune fortune) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(FortuneBean.TABLE_NAME, fortune);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) fortune.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create an Intent to launch ExampleActivity
            Intent btnIntent = new Intent();
            btnIntent.setAction(FortunesApplication.ACTION_UPDATE_CLICK);

            PendingIntent btnPendingIntent = PendingIntent.getBroadcast(context, (int) fortune.getId(), btnIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setTextViewText(R.id.fortune_text, fortune.toString());
            views.setOnClickPendingIntent(R.id.fortune_text, pendingIntent);

            views.setOnClickPendingIntent(R.id.btn_new, btnPendingIntent);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /**
     * A general technique for calling the onUpdate method,
     * requiring only the context parameter.
     *
     * @author John Bentley, based on Android-er code.
     * @see <a href="http://android-er.blogspot.com
     * .au/2010/10/update-widget-in-onreceive-method.html">
     * Android-er > 2010-10-19 > Update Widget in onReceive() method</a>
     */
    private void onUpdate(Context context, Fortune fortune) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(), getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        updateWidgets(context, appWidgetManager, appWidgetIds, fortune);
    }
}