package my.company.com.searchdemo.presentation.ui.movies;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import my.company.com.searchdemo.domain.IRepository;
import my.company.com.searchdemo.domain.models.Genre;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */

public class MoviesMainViewModel extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final IRepository repository;

    public final ObservableField<List<Genre>> genres = new ObservableField<>(new ArrayList<>());

    @Inject
    public MoviesMainViewModel(IRepository repository) {
        this.repository = repository;

        getGenres();
    }


    public void getGenres() {
        disposables.add(this.repository.getAllGenres().subscribe(this::OnFetchGenresSuccess,
                this::OnFetchGenresError));
    }

    private void OnFetchGenresSuccess(Collection<Genre> genres) {
        this.genres.set(new ArrayList<>(genres));
    }

    private void OnFetchGenresError(Throwable exception) {
        //TODO: add error handler
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
