package my.company.com.searchdemo.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import my.company.com.searchdemo.MainActivity;
import my.company.com.searchdemo.MainModule;

/**
 * @author Yevgen Oliinykov on 3/15/18.
 */
@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = {FragmentBuildersModule.class, MainModule.class})
    abstract MainActivity contributeMainActivity();
}
