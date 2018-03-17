package my.company.com.searchdemo.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import my.company.com.searchdemo.presentation.ui.movies.MoviesListFragment;
import my.company.com.searchdemo.presentation.ui.movies.MoviesMainFragment;

/**
 * @author Yevgen Oliinykov on 3/15/18.
 */
@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MoviesMainFragment contributeMoviesMainFragment();

    @ContributesAndroidInjector
    abstract MoviesListFragment contributeMoviesListFragment();

}
