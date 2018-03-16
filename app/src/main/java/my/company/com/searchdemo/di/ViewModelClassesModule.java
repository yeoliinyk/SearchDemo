package my.company.com.searchdemo.di;

import dagger.Module;
import dagger.Provides;
import my.company.com.searchdemo.presentation.ui.movies.MoviesMainViewModel;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */
@Module
public class ViewModelClassesModule {

    @Provides
    Class<MoviesMainViewModel> provideMoviesMainViewModelClass() {
        return MoviesMainViewModel.class;
    }
}
