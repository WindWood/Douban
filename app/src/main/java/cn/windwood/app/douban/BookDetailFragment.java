package cn.windwood.app.douban;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = BookDetailFragment.class.getSimpleName();
    private static final String BOOKID_KEY = "BOOKID";

    private static final int DETAIL_LOADER = 2;

    private TextView titleView;
    private TextView authorView;
    private TextView isbnView;
    private Button favoriteButton;
    private Button deleteButton;

    private static long bookId;

    private DetailListener detailListener;

    public BookDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BOOKID_KEY, bookId);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bookId > 0 ) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            bookId = intent.getLongExtra(Intent.EXTRA_TEXT, 0);
        } else {
            if (null != savedInstanceState) {
                bookId = savedInstanceState.getLong(BOOKID_KEY, 0);
            }
        }

        if (bookId == 0) {
            throw new IllegalArgumentException("Wrong bookId in Intent.EXTRA");
        }

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);

        titleView = (TextView) rootView.findViewById(R.id.bookTitle);
        authorView = (TextView) rootView.findViewById(R.id.bookAuthor);
        isbnView = (TextView) rootView.findViewById(R.id.bookISBN);
        favoriteButton = (Button) rootView.findViewById(R.id.favoriteButton);
        deleteButton = (Button) rootView.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailListener.onDeleteListener(bookId);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            detailListener = (DetailListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DetailListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detailListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = {
                BooksProvider.ROWID,
                BooksProvider.ISBN,
                BooksProvider.TITLE,
                BooksProvider.FAVORITE
        };

        Uri bookUri = ContentUris.withAppendedId(BooksProvider.CONTENT_URI, bookId);

        return new CursorLoader(
                getActivity(),
                bookUri,
                columns,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String isbn = data.getString(data.getColumnIndex(BooksProvider.ISBN));
            String title = data.getString(data.getColumnIndex(BooksProvider.TITLE));
            final boolean isFavorited = (data.getInt(data.getColumnIndex(BooksProvider.FAVORITE)) != 0);

            titleView.setText(title);
            isbnView.setText(isbn);
            favoriteButton.setText(isFavorited ? R.string.favorite_remove : R.string.favorite_add);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailListener.onFavoriteChange(bookId, !isFavorited);
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface DetailListener {
        public void onDeleteListener(long bookId);
        public void onFavoriteChange(long bookId, boolean changedValue);
    }
}
