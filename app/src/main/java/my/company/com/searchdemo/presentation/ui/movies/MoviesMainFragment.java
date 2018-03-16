package my.company.com.searchdemo.presentation.ui.movies;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import my.company.com.searchdemo.MainActivity;
import my.company.com.searchdemo.R;
import my.company.com.searchdemo.databinding.FragmentMoviesMainBinding;
import my.company.com.searchdemo.di.Injectable;
import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.presentation.base.MvvmFragment;
import my.company.com.searchdemo.presentation.helpers.AutoClearedValue;

/**
 * @author Yevgen Oliinykov on 3/15/18.
 */

public class MoviesMainFragment extends MvvmFragment<FragmentMoviesMainBinding, MoviesMainViewModel> implements Injectable {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private OnGenresChangedCallback onGenresChangedCallback;

    private AutoClearedValue<GenrePagerAdapter> pagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                new GenrePagerAdapter(getActivity().getSupportFragmentManager()));
        viewPager = binding.get().viewPager;
        viewPager.setAdapter(pagerAdapter.get());
        setupTabs();

        this.onGenresChangedCallback = new OnGenresChangedCallback();
        this.pagerAdapter.get().updateGenres(viewModel.genres.get());
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viewModel.genres.addOnPropertyChangedCallback(this.onGenresChangedCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.genres.removeOnPropertyChangedCallback(this.onGenresChangedCallback);
    }

    private class OnGenresChangedCallback extends Observable.OnPropertyChangedCallback
    {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            pagerAdapter.get().updateGenres(((ObservableField<List<Genre>>)observable).get()); //ugly cast :(
        }
    }

    private static class GenrePagerAdapter extends FragmentStatePagerAdapter {

        private List<Genre> genres = new ArrayList<>();

        public GenrePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MainActivity.PlaceholderFragment.newInstance(genres.get(position).getId());
        }

        @Override
        public int getCount() {
            return genres.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return genres.get(position).getName();
        }

        public void updateGenres(List<Genre> genres)
        {
            this.genres = genres;
            notifyDataSetChanged();
        }
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
}
