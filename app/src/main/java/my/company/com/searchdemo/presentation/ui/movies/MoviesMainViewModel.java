package my.company.com.searchdemo.presentation.ui.movies;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import my.company.com.searchdemo.domain.IRepository;
import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.domain.models.Movie;
import timber.log.Timber;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */

public class MoviesMainViewModel extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final IRepository repository;

    public final ObservableField<List<Genre>> genres = new ObservableField<>(new ArrayList<>());
    public final ObservableField<Map<Genre, Collection<Movie>>> movies = new ObservableField<>(new HashMap<>());
    public final ObservableBoolean loading = new ObservableBoolean();

    @Inject
    public MoviesMainViewModel(IRepository repository) {
        this.repository = repository;
        getGenres();
    }

    public void getGenres() {
        disposables.add(this.repository.getAllGenres()
                .doOnSubscribe(x -> loading.set(true))
                .doOnSuccess(x -> loading.set(false))
                .doOnError(x -> loading.set(false))
                .subscribe(this::OnFetchGenresSuccess, this::OnFetchGenresError));
    }

    private void OnFetchGenresSuccess(Collection<Genre> genres) {
        this.genres.set(new ArrayList<>(genres));
        getAllMovies();
    }

    private void OnFetchGenresError(Throwable exception) {
        Timber.e(exception, "Error occurs while fetching genres");
        //TODO: add error handler
    }

    public void getAllMovies() {
        disposables.add(this.repository.getAllMovies(this.genres.get())
                .doOnSubscribe(x -> loading.set(true))
                .doOnSuccess(x -> loading.set(false))
                .doOnError(x -> loading.set(false))
                .subscribe(this::OnFetchMoviesSuccess, this::OnFetchMoviesError));
    }

    private void OnFetchMoviesSuccess(Map<Genre, Collection<Movie>> moviesMap) {
        this.movies.set(moviesMap);
    }

    private void OnFetchMoviesError(Throwable exception) {
        Timber.e(exception, "Error occurs while fetching movies");
        //TODO: add error handler
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
