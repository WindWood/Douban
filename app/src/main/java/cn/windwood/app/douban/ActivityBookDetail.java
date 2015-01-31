package cn.windwood.app.douban;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class ActivityBookDetail extends ActionBarActivity implements BookDetailFragment.DetailListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BookDetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
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
    public void onDeleteListener(long bookId) {
        Uri bookUri = ContentUris.withAppendedId(BooksProvider.CONTENT_URI, bookId);

        getContentResolver().delete(bookUri, null, null);
        getContentResolver().notifyChange(bookUri, null);

        finish();

        Toast.makeText(this, "Book deleted.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteChange(long bookId, boolean changedValue) {
        ContentValues editValues = new ContentValues();
        editValues.put(BooksProvider.FAVORITE, changedValue);

        Uri uri = ContentUris.withAppendedId(BooksProvider.CONTENT_URI, bookId);
        getContentResolver().update(
                uri,
                editValues,
                null,
                null
        );
        getContentResolver().notifyChange(uri, null);
//        finish();

        String toast = "Book had ";
        if (changedValue) {
            toast = "add to favorite list";
        } else {
            toast = "removed from favorite list";
        }
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

}
