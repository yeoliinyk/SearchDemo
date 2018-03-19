package my.company.com.searchdemo.presentation.ui.movies;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import my.company.com.searchdemo.R;
import my.company.com.searchdemo.databinding.FragmentMoviesListBinding;
import my.company.com.searchdemo.di.Injectable;
import my.company.com.searchdemo.domain.models.Movie;
import my.company.com.searchdemo.presentation.base.MvvmFragment;
import my.company.com.searchdemo.presentation.helpers.AutoClearedValue;
import timber.log.Timber;

/**
 * @author Yevgen Oliinykov on 3/17/18.
 */

public class MoviesListFragment extends MvvmFragment<FragmentMoviesListBinding, MoviesListViewModel>
        implements Injectable {

    private static final String ARG_GENRE_ID = "genre_id";

    public static MoviesListFragment newInstance(long genreId) {
        MoviesListFragment fragment = new MoviesListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GENRE_ID, genreId);
        fragment.setArguments(args);
        return fragment;
    }

    AutoClearedValue<MoviesListAdapter> moviesAdapter;
    io.reactivex.Observable<String> searchViewObservable;
    Disposable searchViewDisposable;

    OnMoviesChangedCallback onMoviesChangedCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_movies_list);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.onMoviesChangedCallback = new OnMoviesChangedCallback();
        setupMoviesList();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viewModel.movies.addOnPropertyChangedCallback(onMoviesChangedCallback);
        subscribeToSearchView();
        initMoviesList();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.movies.removeOnPropertyChangedCallback(onMoviesChangedCallback);
        unsubscribeFromSearchView();
    }

    public void setSearchViewObservable(io.reactivex.Observable<String> observable) {
        this.searchViewObservable = observable;
    }

    private void subscribeToSearchView() {
        if (searchViewObservable != null) {
            this.searchViewDisposable = searchViewObservable.subscribe(s -> {
                Timber.i("Search value: " + s);
                if (moviesAdapter.get().getOriginalList().size() > 0)
                    moviesAdapter.get().getFilter().filter(s);
            });
        }
    }

    private void unsubscribeFromSearchView() {
        if (this.searchViewDisposable != null)
            this.searchViewDisposable.dispose();
    }

    private void initMoviesList() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_GENRE_ID)) {
            this.viewModel.genreId.set(args.getLong(ARG_GENRE_ID));
        } else {
            this.viewModel.genreId.set(-1L);
        }

        updateMoviesList(viewModel.movies.get());
    }

    private class OnMoviesChangedCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            List<Movie> movies = ((ObservableField<List<Movie>>) observable).get(); //ugly cast :(
            updateMoviesList(movies);
        }
    }

    private void updateMoviesList(List<Movie> movies) {
        this.moviesAdapter.get().setOriginalList(movies);
        List<Movie> copy = new ArrayList<>();
        copy.addAll(movies);
        this.moviesAdapter.get().replace(copy);
    }

    private void setupMoviesList() {
        MoviesListAdapter adapter = new MoviesListAdapter(movie -> {
        });
        this.moviesAdapter = new AutoClearedValue<>(this, adapter);
        this.binding.get().recyclerView.setHasFixedSize(true);
        this.binding.get().recyclerView.setAdapter(adapter);
    }
}
