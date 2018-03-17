package my.company.com.searchdemo.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import my.company.com.searchdemo.presentation.base.AppViewModelFactory;
import my.company.com.searchdemo.presentation.ui.movies.MoviesListViewModel;
import my.company.com.searchdemo.presentation.ui.movies.MoviesMainViewModel;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */
@Module(includes = ViewModelClassesModule.class)
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MoviesMainViewModel.class)
    abstract ViewModel bindMoviesMainViewModel(MoviesMainViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MoviesListViewModel.class)
    abstract ViewModel bindMoviesListViewModel(MoviesListViewModel userViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory factory);
}
