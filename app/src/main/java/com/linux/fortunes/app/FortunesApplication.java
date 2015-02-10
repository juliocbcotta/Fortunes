package com.linux.fortunes.app;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.linux.fortunes.bean.FortuneBean;
import com.linux.fortunes.db.DBManager;
import com.linux.fortunes.model.Fortune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio on 12/12/14.
 */
public class FortunesApplication extends Application {
    public static final String FortunesInsertionFinish = "FortunesApplication.FortunesInsertionFinish";
    public static String ACTION_UPDATE_CLICK = ".appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
        // initialize intent filter of widget based on the main package
        ACTION_UPDATE_CLICK = getPackageName() + ACTION_UPDATE_CLICK;

        // initialize the DBManager
        DBManager.getInstance().init(getApplicationContext());

        // populate the database if there is not at least one fortune
        if (FortuneBean.isEmpty()) {
            populateDatabase();
        }
    }

    private void populateDatabase() {
        new AsyncTask<Void, Void, Void>() {
            public final String DASH_DASH = "--";
            private final String SPACE = " ";
            private final java.lang.String PERCENT = "%";

            @Override
            protected Void doInBackground(Void... voids) {
                List<Fortune> fortuneList = new ArrayList<Fortune>();

                InputStream in = getResources().openRawResource(R.raw.database);
                InputStreamReader is = new InputStreamReader(in);

                BufferedReader br = new BufferedReader(is);
                String read = null;
                try {
                    StringBuilder fortuneText = new StringBuilder();
                    StringBuilder authorName = new StringBuilder();
                    do {
                        read = br.readLine();
                        if (!TextUtils.isEmpty(read)) {
                            String str = read.trim();
                            if (!TextUtils.isEmpty(str)) {
                                // found an author ?
                                if (str.startsWith(DASH_DASH)) {
                                    // set the fortune author
                                    authorName.append(read);
                                    // found the end of a fortune?
                                } else if (str.startsWith(PERCENT)) {
                                    // creates and adds the fortune to the list
                                    fortuneList.add(new Fortune(fortuneText.toString(), authorName.toString()));
                                    // reset string builders
                                    fortuneText = new StringBuilder();
                                    authorName = new StringBuilder();
                                } else {
                                    fortuneText.append(read);
                                    // adds an empty space after each line of text
                                    if (!read.endsWith(SPACE)) {
                                        fortuneText.append(SPACE);
                                    }

                                }
                            }
                        }
                    } while (read != null);
                } catch (IOException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                        is.close();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FortuneBean.insertAll(fortuneList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // send a broadcast to force the Activity (if resumed) UI to update
                Intent intent = new Intent();
                intent.setAction(FortunesInsertionFinish);
                sendBroadcast(intent);
            }
        }.execute();
    }
}
