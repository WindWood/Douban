package cn.windwood.app.douban;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by WindWood on 2015/1/24.
 */
public class BookDoubanLoader extends CursorLoader {
    private final String LOG_TAG = BookDoubanLoader.class.getSimpleName();

    private static final Uri URI = BooksProvider.CONTENT_URI;

    private String searchKey;

    private Context mContext;
    private ContentResolver mContentResolver;

    public BookDoubanLoader(Context context, String searchKey) {
        super(
                context,
                BooksProvider.CONTENT_URI,
                BooksProvider.PROJECTION,
                BooksProvider.TITLE + " like '%" + searchKey + "%'",
                null,
                null
        );
        notify(BooksProvider.TITLE + " like '%" + searchKey + "%'");
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.searchKey = searchKey;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;

        deleteCache();

        try {
            getBookFromJson(fetchBooks(searchKey));

            cursor = super.loadInBackground();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        if (null == cursor) {
            notify("Network connecting error!");
        } else {
            notify("load " + cursor.getCount() + " data.");
        }
        return cursor;
    }

    private String fetchBooks(String... params) {
        if (params.length != 1) {
            return null;
        }

        Uri builtUri = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJsonStr = null;

        try {
            final String BOOK_BASE_URL = "https://api.douban.com/v2/book/search";
            final String QUERY_PARAM = "q";

            builtUri = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .build();
            Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

            url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                resultJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                resultJsonStr = null;
            }

            resultJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Fetch Book JSON String: " + resultJsonStr);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "(Malformed URI: " + builtUri.toString(), e);
            resultJsonStr = null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
            resultJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.v(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        if (resultJsonStr == null) {
            notify("Network connecting error!");
        }
        return resultJsonStr;
    }

    private void getBookFromJson(String resultJsonStr) throws JSONException {
        final String JSON_COUNT = "count";
        final String JSON_TOTAL = "total";
        final String JSON_BOOK_LIST = "books";
        final String JSON_BOOK_TITLE = "title";
        final String JSON_BOOK_ISBN = "isbn13";

        if (resultJsonStr == null) {
            return ;
        }

        JSONObject resultObject = new JSONObject(resultJsonStr);

        int count = resultObject.getInt(JSON_COUNT);
        int total = resultObject.getInt(JSON_TOTAL);
        notify("Got " + count + " of " + total + " books.");

        JSONArray bookArray = resultObject.getJSONArray(JSON_BOOK_LIST);

        for (int i = 0; i < bookArray.length(); i++) {
            JSONObject book = bookArray.getJSONObject(i);

            String bookTitle = book.getString(JSON_BOOK_TITLE);
            String bookIsbn = book.getString(JSON_BOOK_ISBN);

            insertBookNotExist(bookIsbn, bookTitle);
        }
    }

    private void deleteCache() {
        int count = mContentResolver.delete(
                URI,
                BooksProvider.FAVORITE + " = 0",
                null
        );
        notify(BooksProvider.FAVORITE + " = 0");
        notify("Delete cached item: " + count);
    }

    private void insertBookNotExist(String isbn, String title) {
        ContentValues values = new ContentValues();
        values.put(BooksProvider.ISBN, isbn);
        values.put(BooksProvider.TITLE, title);
        values.put(BooksProvider.FAVORITE, 0);

        Uri uri = mContentResolver.insert(URI, values);
//        mContentResolver.notifyChange(URI, null);
        Log.v(LOG_TAG, title + "(" + isbn + ") had inserted.");
    }

    private void notify(String msg) {
//        Toast.makeText(
//                mContext,
//                msg,
//                Toast.LENGTH_SHORT
//        ).show();
        Log.v(LOG_TAG, msg);
    }
}

