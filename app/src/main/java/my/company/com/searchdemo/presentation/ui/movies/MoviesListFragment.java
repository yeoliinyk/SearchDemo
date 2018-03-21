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
    public static final String ARG_SEARCH_QUERY = "search_query";

    public static class Builder {

        private Bundle args = new Bundle();

        public Builder setGenre(Genre genre) {
            args.putParcelable(ARG_GENRE, Parcels.wrap(genre));
            return this;
        }

        public Builder setMovies(Collection<Movie> movies) {
            args.putParcelable(ARG_MOVIES, Parcels.wrap(new ArrayList<>(movies)));
            return this;
        }

        public Builder setSearchQuery(String query) {
            args.putString(ARG_SEARCH_QUERY, query);
            return this;
        }

        public MoviesListFragment build() {
            MoviesListFragment fragment = new MoviesListFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    AutoClearedValue<MoviesListAdapter> moviesAdapter;
    io.reactivex.Observable<String> searchViewObservable;
    Disposable searchViewDisposable;

    String searchQuery;

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
        if (this.searchQuery != null && !this.searchQuery.isEmpty())
            updateMoviesList(this.viewModel.getMovies(), this.searchQuery);
        else
            updateMoviesList(this.viewModel.getMovies());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.movies.removeOnPropertyChangedCallback(onMoviesChangedCallback);
    }

    public void updateMoviesList(List<Movie> movies, String searchQuery) {
        if (moviesAdapter != null) {
            this.moviesAdapter.get().replace(movies, searchQuery);
            this.moviesAdapter.get().notifyDataSetChanged();
        }
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
        if (args != null && args.containsKey(ARG_SEARCH_QUERY)) {
            this.searchQuery = args.getString(ARG_SEARCH_QUERY);
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
