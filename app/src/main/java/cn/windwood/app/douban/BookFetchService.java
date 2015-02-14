package cn.windwood.app.douban;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
                final String query = intent.getStringExtra(EXTRA_PARAM_QUERY);
                String start = intent.getStringExtra(EXTRA_PARAM_START);
                if (start == null || start.isEmpty()) {
                    start = "0";
                }
                handleActionQuery(query, start);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionQuery(String query, String start) {
        ArrayList<Book> searchResult = new ArrayList<>();

        notify("Searching books by " + query);

        try {
            searchResult = DoubanApi.parseResult(DoubanApi.fetchBooks(query));
        } catch (Exception e) {

        }

        notify("Got " + searchResult.size() + " books.");

        ContentResolver cr = getBaseContext().getContentResolver();

        for (Book book: searchResult) {
//            BookListFragment.addBook(book);

            ContentValues values = new ContentValues();
            values.put(BooksProvider.ISBN, book.isbn);
            values.put(BooksProvider.TITLE, book.title);
            values.put(BooksProvider.FAVORITE, 0);

            Uri uri = cr.insert(BooksProvider.CONTENT_URI, values);
            notify("Book " + book.isbn + " had updated");
        }

    }

    private void notify(String msg) {
        Log.v(LOG_TAG, msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
