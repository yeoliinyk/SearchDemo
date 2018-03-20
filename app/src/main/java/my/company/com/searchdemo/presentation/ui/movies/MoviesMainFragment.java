package my.company.com.searchdemo.presentation.ui.movies;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxrelay2.BehaviorRelay;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import my.company.com.searchdemo.AppExecutors;
import my.company.com.searchdemo.R;
import my.company.com.searchdemo.databinding.FragmentMoviesMainBinding;
import my.company.com.searchdemo.di.Injectable;
import my.company.com.searchdemo.presentation.base.MvvmFragment;
import my.company.com.searchdemo.presentation.helpers.AutoClearedValue;

/**
 * @author Yevgen Oliinykov on 3/15/18.
 */

public class MoviesMainFragment extends MvvmFragment<FragmentMoviesMainBinding, MoviesMainViewModel> implements Injectable {

    @Inject AppExecutors appExecutors;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private OnMoviesChangedCallback onMoviesChangedCallback;

    private AutoClearedValue<GenrePagerAdapter> pagerAdapter;

    BehaviorRelay<String> searchRelay = BehaviorRelay.createDefault("");
    Disposable searchRelayDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_movies_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.pagerAdapter = new AutoClearedValue<>(this,
                new GenrePagerAdapter(getChildFragmentManager()));
        viewPager = binding.get().viewPager;
        viewPager.setAdapter(pagerAdapter.get());
        setupTabs();

        this.onMoviesChangedCallback = new OnMoviesChangedCallback();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_movies, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchRelay.accept(s);
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viewModel.movies.addOnPropertyChangedCallback(this.onMoviesChangedCallback);
        subscribeToSearchView();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.movies.removeOnPropertyChangedCallback(this.onMoviesChangedCallback);
        unsubscribeFromSearchView();
    }

    private void setupTabs() {
        this.appBarLayout = getActivity().findViewById(R.id.appbar);
        this.tabLayout = (TabLayout) getActivity().getLayoutInflater()
                .inflate(R.layout.view_tab_layout, appBarLayout, false);
        appBarLayout.addView(tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(this.viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private class OnMoviesChangedCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            populateAdapter();
        }
    }

    private void populateAdapter() {
        this.pagerAdapter.get().invalidateFilter();
        this.pagerAdapter.get().update(this.viewModel.movies.get());
    }

    private void subscribeToSearchView() {
        this.searchRelayDisposable = this.searchRelay
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.from(this.appExecutors.mainThread()))
                .subscribe(query -> pagerAdapter.get().getFilter().filter(query));
    }

    private void unsubscribeFromSearchView() {
        if (this.searchRelayDisposable != null)
            this.searchRelayDisposable.dispose();
    }

}
