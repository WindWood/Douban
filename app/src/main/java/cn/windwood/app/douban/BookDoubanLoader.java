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

    public BookDoubanLoader(Context context, String searchKey) {
        super(
                context,
                URI,
                BooksProvider.PROJECTION,
                null,
                null,
                null
        );
        this.mContext = context;
        this.searchKey = searchKey;
    }

    @Override
    public Cursor loadInBackground() {
        try {
            getBookFromJson(fetchBooks(searchKey));

            return super.loadInBackground();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String fetchBooks(String... params) {
        if (params.length != 1) {
            return null;
        }

        Uri builtUri = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String bookJsonStr = null;

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
                bookJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                bookJsonStr = null;
            }

            bookJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Fetch Book JSON String: " + bookJsonStr);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "(Malformed URI: " + builtUri.toString(), e);
            bookJsonStr = null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
            bookJsonStr = null;
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

        return bookJsonStr;
    }

    private void getBookFromJson(String bookJsonStr) throws JSONException {
        ContentResolver cr = mContext.getContentResolver();

        JSONObject bookJson = new JSONObject(bookJsonStr);

        final String BOOK_LIST = "books";
        final String BOOK_TITLE = "title";
        final String BOOK_ISBN = "isbn13";

        JSONArray bookArray = bookJson.getJSONArray(BOOK_LIST);

        for (int i = 0; i < bookArray.length(); i++) {
            JSONObject book = bookArray.getJSONObject(i);

            String bookTitle = book.getString(BOOK_TITLE);
            String bookIsbn = book.getString(BOOK_ISBN);
            Log.v(LOG_TAG, bookTitle + ">>" + bookIsbn);

            ContentValues values = new ContentValues();
            values.put(BooksProvider.ISBN, bookIsbn);
            values.put(BooksProvider.TITLE, bookTitle);
            values.put(BooksProvider.FAVORITE, 0);

            Uri uri = cr.insert(URI, values);
            Log.v(LOG_TAG, "Insert book " + uri.toString());

//            cr.notifyChange(uri, null);
        }
    }
}

