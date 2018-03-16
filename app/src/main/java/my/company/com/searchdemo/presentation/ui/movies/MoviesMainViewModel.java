package my.company.com.searchdemo.presentation.ui.movies;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import my.company.com.searchdemo.AppExecutors;
import my.company.com.searchdemo.domain.IRepository;
import my.company.com.searchdemo.domain.models.Genre;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */

public class MoviesMainViewModel extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final IRepository repository;
    private final AppExecutors appExecutors;

    public final ObservableField<List<Genre>> genres = new ObservableField<>(new ArrayList<>());

    @Inject
    public MoviesMainViewModel(IRepository repository, AppExecutors appExecutors) {
        this.repository = repository;
        this.appExecutors = appExecutors;

        getGenres();
    }


    public void getGenres() {
        disposables.add(this.repository.getAllGenres()
                .subscribeOn(Schedulers.from(appExecutors.networkIO()))
                .observeOn(Schedulers.from(appExecutors.mainThread()))
                .subscribe(this::OnFetchGenresSuccess, this::OnFetchGenresError));
    }

    private void OnFetchGenresSuccess(Collection<Genre> genres) {
        this.genres.set(new ArrayList<>(genres));
    }

    private void OnFetchGenresError(Throwable exception) {

    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
