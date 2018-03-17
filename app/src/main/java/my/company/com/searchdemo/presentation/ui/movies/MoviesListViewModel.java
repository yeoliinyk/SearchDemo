package my.company.com.searchdemo.presentation.ui.movies;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import my.company.com.searchdemo.domain.IRepository;
import my.company.com.searchdemo.domain.models.Movie;
import timber.log.Timber;

/**
 * @author Yevgen Oliinykov on 3/17/18.
 */

public class MoviesListViewModel extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final IRepository repository;

    public final ObservableField<List<Movie>> movies = new ObservableField<>(new ArrayList<>());
    public final ObservableField<Long> genreId = new ObservableField<>();

    @Inject
    public MoviesListViewModel(IRepository repository) {
        this.repository = repository;

        this.genreId.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (genreId.get() > 0)
                    getMoviesWithGenreId(genreId.get());
                else
                {
                    //TODO: handle missing genre id
                }
            }
        });
    }

    public void getMoviesWithGenreId(long genreId) {
        disposables.add(this.repository.getAllMoviesByGenre(genreId).subscribe(this::OnFetchGenresSuccess,
                this::OnFetchGenresError));
    }

    private void OnFetchGenresSuccess(Collection<Movie> movies) {
        this.movies.set(new ArrayList<>(movies));
    }

    private void OnFetchGenresError(Throwable exception) {
        //TODO: add error handler
        Timber.e("Error while fetching movies" + exception);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
