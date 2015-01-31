package cn.windwood.app.douban;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * Created by WindWood on 2015/1/24.
 */
public class BookDbAdapter {
    static final String TAG = BookDbAdapter.class.getSimpleName();
    static final String DATABASE_NAME = "BookDB";
    static final int DATABASE_VERSION = 2;

    static final String TABLE_BOOK = "books";

    static final String COLUMN_BOOK_ROWID = "_id";
    static final String COLUMN_BOOK_ISBN = "isbn";
    static final String COLUMN_BOOK_TITLE = "title";
    static final String COLUMN_BOOK_FAVORITE = "favorite";

    static final String TABLE_BOOK_CREATE_SQL ="CREATE TABLE books (_id integer primary key autoincrement, isbn text not null, title text not null, favorite integer default 0);";

    Context mContext = null;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public BookDbAdapter(Context context) {
        this.mContext = context;
        DBHelper = new DatabaseHelper(context);
    }

    public boolean open() {
        db = DBHelper.getWritableDatabase();
        return (db == null);
    }

    public void close() {
        DBHelper.close();
    }

    public long insertBook(ContentValues values) {
        return db.insert(TABLE_BOOK, null, values);
    }

    public int deleteBook(String selection, String[] selectionArgs) {
        return db.delete(TABLE_BOOK, selection, selectionArgs);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(TABLE_BOOK);

        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = COLUMN_BOOK_TITLE;
        }

        return sqlBuilder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public int updateBook(ContentValues values, String selection, String[] selectionArgs) {
        return db.update(TABLE_BOOK, values, selection, selectionArgs);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TABLE_BOOK_CREATE_SQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS books");
            onCreate(db);
        }
    }
}
