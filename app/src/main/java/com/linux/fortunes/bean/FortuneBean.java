package com.linux.fortunes.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.linux.fortunes.db.DBContentProvider;
import com.linux.fortunes.db.DBManager;
import com.linux.fortunes.db.util.TableBuilder;
import com.linux.fortunes.model.Fortune;

import java.util.Collection;

/**
 * Created by julio on 12/12/14.
 */
public class FortuneBean {

    public static final String TABLE_NAME = "fortunes";

    public static Fortune getRandomFortune() {
        Fortune fortune = null;
        Uri uri = DBContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(DBContentProvider.ONE_ROW_LIMIT).appendPath(TABLE_NAME).build();
        Cursor c = DBManager.getInstance().query(uri, null, null, null, "RANDOM()");
        if (c != null) {
            if (c.moveToFirst()) {
                fortune = fromCursor(c);
            }
            c.close();
        }
        return fortune;
    }

    private static Fortune fromCursor(Cursor c) {

        long id = c.getLong(c.getColumnIndex(COLUMN._id.name()));
        String fortuneText = c.getString(c.getColumnIndex(COLUMN.FORTUNE.name()));
        String authorText = c.getString(c.getColumnIndex(COLUMN.AUTHOR.name()));

        Fortune fortune = new Fortune(fortuneText, authorText);
        fortune.setId(id);

        return fortune;
    }

    public enum COLUMN {
        _id, FORTUNE, AUTHOR
    }

    public static String createTableString() throws Exception {
        TableBuilder tb = new TableBuilder(TABLE_NAME);
        tb.setPrimaryKey(COLUMN._id.name(), tb.INTEGER, true);
        tb.addColumn(COLUMN.FORTUNE.name(), tb.TEXT, true);
        tb.addColumn(COLUMN.AUTHOR.name(), tb.TEXT, false);

        return tb.toString();
    }

    public static void insertAll(Collection<Fortune> fortunes) {
        ContentValues[] values = new ContentValues[fortunes.size()];
        int index = 0;
        for (Fortune fortune : fortunes) {
            values[index++] = toContentValues(fortune);
        }
        DBManager.getInstance().bulkInsert(TABLE_NAME, values);
    }

    private static ContentValues toContentValues(Fortune fortune) {
        ContentValues values = new ContentValues();
        if (fortune.getId() > 0) {
            values.put(COLUMN._id.name(), fortune.getId());
        }
        values.put(COLUMN.FORTUNE.name(), fortune.getFortune());
        values.put(COLUMN.AUTHOR.name(), fortune.getAuthor());
        return values;
    }

    public static boolean isEmpty() {
        return DBManager.getInstance().isEmpty(TABLE_NAME);
    }
}
