package my.company.com.searchdemo.presentation.ui.movies;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.disposables.Disposable;
import my.company.com.searchdemo.R;
import my.company.com.searchdemo.databinding.FragmentMoviesListBinding;
import my.company.com.searchdemo.di.Injectable;
import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.domain.models.Movie;
import my.company.com.searchdemo.presentation.base.MvvmFragment;
import my.company.com.searchdemo.presentation.helpers.AutoClearedValue;

/**
 * @author Yevgen Oliinykov on 3/17/18.
 */

public class MoviesListFragment extends MvvmFragment<FragmentMoviesListBinding, MoviesListViewModel>
        implements Injectable {

    public static final String ARG_GENRE = "genre";
    public static final String ARG_MOVIES = "movies";

    public static MoviesListFragment newInstance(Genre genre) {
        MoviesListFragment fragment = new MoviesListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GENRE, Parcels.wrap(genre));
        fragment.setArguments(args);
        return fragment;
    }

    public static MoviesListFragment newInstance(Genre genre, Collection<Movie> movies) {
        MoviesListFragment fragment = new MoviesListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GENRE, Parcels.wrap(genre));
        args.putParcelable(ARG_MOVIES, Parcels.wrap(new ArrayList<>(movies)));
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
        initMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viewModel.movies.addOnPropertyChangedCallback(onMoviesChangedCallback);
        updateMoviesList(this.viewModel.movies.get());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.movies.removeOnPropertyChangedCallback(onMoviesChangedCallback);
    }

    public void updateMoviesList(List<Movie> movies) {
        if (moviesAdapter != null)
            this.moviesAdapter.get().replace(movies);
    }

    private void initMovies() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_GENRE)) {
            this.viewModel.genre.set(Parcels.unwrap(args.getParcelable(ARG_GENRE)));
        }
        if (args != null && args.containsKey(ARG_MOVIES)) {
            List<Movie> movies = Parcels.unwrap(args.getParcelable(ARG_MOVIES));
            this.viewModel.movies.set(movies);
        }
    }

    private class OnMoviesChangedCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            updateMoviesList(MoviesListFragment.this.viewModel.movies.get());
        }
    }

    private void setupMoviesList() {
        MoviesListAdapter adapter = new MoviesListAdapter(movie -> { /*noop*/ });
        this.moviesAdapter = new AutoClearedValue<>(this, adapter);
        this.binding.get().recyclerView.setHasFixedSize(true);
        this.binding.get().recyclerView.setAdapter(adapter);
    }

    public void setSearchViewObservable(io.reactivex.Observable<String> observable) {
        this.searchViewObservable = observable;
    }

    private void subscribeToSearchView() {
        if (searchViewObservable != null) {
            this.searchViewDisposable = searchViewObservable.subscribe(s -> {
                if (moviesAdapter.get().getOriginalList().size() > 0)
                    moviesAdapter.get().getFilter().filter(s);
            });
        }
    }

    private void unsubscribeFromSearchView() {
        if (this.searchViewDisposable != null)
            this.searchViewDisposable.dispose();
    }
}
