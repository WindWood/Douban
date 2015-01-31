package cn.windwood.app.douban;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class ActivityBookSearch extends ActionBarActivity implements BookSearchFragment.SearchListener {
    private static final String LOG_TAG = ActivityBookSearch.class.getSimpleName();

    private BookListFragment bookListFragment = new BookListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BookSearchFragment())
                    .add(R.id.container, bookListFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchButtonClick(String searchKey) {
        Bundle searchBundle = new Bundle();
        searchBundle.putString("searchKey", searchKey);
        getSupportLoaderManager().restartLoader(BookListFragment.BOOK_DOUBAN_LOADER, searchBundle, bookListFragment);
    }
}
