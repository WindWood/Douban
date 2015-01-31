package cn.windwood.app.douban;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookSearchFragment extends Fragment {
    private static final String LOG_TAG = BookSearchFragment.class.getSimpleName();

    private EditText searchText;
    private Button searchButton;

    private SearchListener searchListener;

    public BookSearchFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            searchListener = (SearchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_search, container, false);

        searchText = (EditText) rootView.findViewById(R.id.searchKey);
        searchButton = (Button) rootView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchText.getText().toString().isEmpty()) {
                    Log.w(LOG_TAG, "Search key is empty or not valid!");
                    return ;
                }
                Log.v(LOG_TAG, "Searching " + searchText.getText().toString());
                searchListener.onSearchButtonClick(searchText.getText().toString());
            }
        });

        return rootView;
    }

    public interface SearchListener {
        public void onSearchButtonClick(String searchKey);
    }

}
