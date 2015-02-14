package cn.windwood.app.douban.util;

import android.net.Uri;
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
import java.util.ArrayList;

import cn.windwood.app.douban.data.Book;

/**
 * Created by WindWood on 2015/2/8.
 */
public class DoubanApi {
    private static final String LOG_TAG = DoubanApi.class.getSimpleName();

    private static final String JSON_BOOK_LIST = "books";
    private static final String JSON_BOOK_TITLE = "title";
    private static final String JSON_BOOK_ISBN = "isbn13";

    static final String BOOK_API_BASE_URL = "https://api.douban.com/v2/book/search";
    static final String BOOK_QUERY_PARAM = "q";

    public static final int API_VERSION = 2;

    public static String fetchBooks(String... params) {
        if (params.length != 1) {
            return null;
        }

        Uri builtUri = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String bookJsonStr = null;

        builtUri = Uri.parse(BOOK_API_BASE_URL).buildUpon()
                .appendQueryParameter(BOOK_QUERY_PARAM, params[0])
                .build();
        Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "(Malformed URI: " + builtUri.toString(), e);
            return null;
        }

        try {
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

    public static ArrayList<Book> parseResult(String bookListJsonStr) {
        ArrayList<Book> bookList = new ArrayList<>();

        try {
            JSONObject bookListJson = new JSONObject(bookListJsonStr);

            JSONArray bookArray = bookListJson.getJSONArray(JSON_BOOK_LIST);

            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject bookObject = bookArray.getJSONObject(i);

                bookList.add(parseBook(bookObject));
//                ContentValues values = new ContentValues();
//                values.put(BooksProvider.ISBN, bookIsbn);
//                values.put(BooksProvider.TITLE, bookTitle);
//                values.put(BooksProvider.FAVORITE, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookList;

    }
    public static Book parseBook(JSONObject bookObject) {
        try {
            String title = bookObject.getString(JSON_BOOK_TITLE);
            String isbn = bookObject.getString(JSON_BOOK_ISBN);

            Log.v(LOG_TAG, title + ">>" + isbn);

            return new Book(isbn, title);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
