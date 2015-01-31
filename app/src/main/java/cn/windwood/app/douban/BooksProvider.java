package cn.windwood.app.douban;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class BooksProvider extends ContentProvider {
    static final String PROVIDER_NAME = "cn.windwood.app.douban.provider.books";
    static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/books");

    static final String ROWID = BookDbAdapter.COLUMN_BOOK_ROWID;
    static final String ISBN = BookDbAdapter.COLUMN_BOOK_ISBN;
    static final String TITLE = BookDbAdapter.COLUMN_BOOK_TITLE;
    static final String FAVORITE = BookDbAdapter.COLUMN_BOOK_FAVORITE;

    static final String [] PROJECTION = {
            BookDbAdapter.COLUMN_BOOK_ROWID,
            ISBN,
            TITLE,
            FAVORITE
    };

    static final int URI_BOOKS = 1;
    static final int URI_BOOK_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "books", URI_BOOKS);
        uriMatcher.addURI(PROVIDER_NAME, "books/#", URI_BOOK_ID);
    }

    BookDbAdapter dbAdapter;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbAdapter = new BookDbAdapter(context);
        return dbAdapter.open();
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                return "vnd.android.cursor.dir/vnd.windwood.books ";
            case URI_BOOK_ID:
                return "vnd.android.cursor.item/vnd.windwood.books ";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = dbAdapter.insertBook(values);

        if (rowId > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return uri;
        } else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result;
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                result = dbAdapter.deleteBook(selection, selectionArgs);
                break;
            case URI_BOOK_ID:
                String id = uri.getPathSegments().get(1);
                result = dbAdapter.deleteBook(
                        BookDbAdapter.COLUMN_BOOK_ROWID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor mCursor;

        if (uriMatcher.match(uri) == URI_BOOK_ID) {
            mCursor = dbAdapter.query(
                    projection,
                    ROWID + " = " + Long.parseLong(uri.getPathSegments().get(1)),
                    null,
                    null
            );
        } else {
            mCursor = dbAdapter.query(
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );
        }

        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int result;
        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                result = dbAdapter.updateBook(values, selection, selectionArgs);
                break;
            case URI_BOOK_ID:
                String id = uri.getPathSegments().get(1);
                result = dbAdapter.updateBook(
                        values,
                        BookDbAdapter.COLUMN_BOOK_ROWID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }
}
