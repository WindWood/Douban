package cn.windwood.app.douban;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.windwood.app.douban.data.Book;
import cn.windwood.app.douban.util.DoubanApi;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class BookFetchService extends IntentService {
    private static final String LOG_TAG = BookFetchService.class.getSimpleName();

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_QUERY_BOOK = "cn.windwood.app.douban.action.QUERY_BOOK";

    public static final String EXTRA_PARAM_QUERY = "cn.windwood.app.douban.extra.Q";
    public static final String EXTRA_PARAM_START = "cn.windwood.app.douban.extra.START";

    /**
     * Starts this service to perform action Query with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionQuery(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BookFetchService.class);
        intent.setAction(ACTION_QUERY_BOOK);
        intent.putExtra(EXTRA_PARAM_QUERY, param1);
        intent.putExtra(EXTRA_PARAM_START, param2);
        context.startService(intent);
    }

    public BookFetchService() {
        super("BookFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_QUERY_BOOK.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_QUERY);
                String param2 = intent.getStringExtra(EXTRA_PARAM_START);
                if (param2 == null || param2.isEmpty()) {
                    param2 = "0";
                }
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo

        getBookFromJson(DoubanApi.fetchBooks(param1));

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getBookFromJson(String bookJsonStr) {
        ArrayList<Book> bookList = new ArrayList<>();

        try {
            JSONObject bookJson = new JSONObject(bookJsonStr);

            final String BOOK_LIST = "books";
            final String BOOK_TITLE = "title";
            final String BOOK_ISBN = "isbn13";

            JSONArray bookArray = bookJson.getJSONArray(BOOK_LIST);

            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject bookObject = bookArray.getJSONObject(i);

                String bookTitle = bookObject.getString(BOOK_TITLE);
                String bookIsbn = bookObject.getString(BOOK_ISBN);
                Log.v(LOG_TAG, bookTitle + ">>" + bookIsbn);

                bookList.add(new Book(bookIsbn, bookTitle, false));

//                ContentValues values = new ContentValues();
//                values.put(BooksProvider.ISBN, bookIsbn);
//                values.put(BooksProvider.TITLE, bookTitle);
//                values.put(BooksProvider.FAVORITE, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
