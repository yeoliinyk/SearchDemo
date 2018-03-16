package my.company.com.searchdemo;

import android.support.v4.app.FragmentActivity;

import dagger.Module;
import dagger.Provides;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */
@Module
public class MainModule {

    @Provides
    FragmentActivity provideActivity(MainActivity activity) {
        return activity;
    }
}
