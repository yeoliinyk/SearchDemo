package my.company.com.searchdemo;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import my.company.com.searchdemo.presentation.base.ActivityNavigator;
import my.company.com.searchdemo.presentation.ui.movies.MoviesMainFragment;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject ActivityNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            this.navigator.replaceFragment(R.id.container, new MoviesMainFragment(), new Bundle());
        }

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return this.dispatchingAndroidInjector;
    }
}
