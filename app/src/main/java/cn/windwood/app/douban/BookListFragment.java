package cn.windwood.app.douban;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class BookListFragment extends Fragment implements AbsListView.OnItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = BookListFragment.class.getSimpleName();

    static final int BOOK_DOUBAN_LOADER = 1;

//    private static List<Map<String, String>> bookList = new ArrayList<>();

    private String[] showColumns = {
            BooksProvider.TITLE,
            BooksProvider.ISBN
    };
    private int[] mToField = {
            android.R.id.text1,
            android.R.id.text2
    };

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleCursorAdapter mAdapter;
//    private SimpleAdapter mSimpleAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookListFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.two_line_list_item,
                null,
                showColumns,
                mToField,
                0
        );

/*        mSimpleAdapter = new SimpleAdapter(
                getActivity(),
                bookList,
                android.R.layout.two_line_list_item,
                showColumns,
                mToField
        );*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
//        ((AdapterView<ListAdapter>) mListView).setAdapter(mSimpleAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        setEmptyText(getString(R.string.list_empty_hints));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ActivityBookDetail.class)
                .putExtra(Intent.EXTRA_TEXT, id);
        startActivity(intent);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case BOOK_DOUBAN_LOADER:
                String filterString;
                if (bundle != null) {
                    filterString = bundle.getString("searchKey");
                    if (!filterString.isEmpty()) {
                        return new BookDoubanLoader(
                                getActivity(),
                                filterString
                        );
                    }
                }

                Log.w(LOG_TAG, "Search key is empty or not valid!");
                return null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        Toast.makeText(
                getActivity(),
                "Searched " + mAdapter.getCount() + " books.",
                Toast.LENGTH_LONG
        ).show();
        Log.v(LOG_TAG, "Loader (" + loader.toString() + ") return data: " + mAdapter.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

//    public void addBook(Book book) {
//        Map<String, String> bookItem = new HashMap<>();
//        bookItem.put(BooksProvider.ISBN, book.isbn);
//        bookItem.put(BooksProvider.TITLE, book.title);
//
//        bookList.add(bookItem);
//    }
//
//    public void clearBook() {
//        bookList.clear();
//    }

}
