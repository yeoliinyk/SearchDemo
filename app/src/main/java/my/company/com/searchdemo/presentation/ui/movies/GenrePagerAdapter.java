package my.company.com.searchdemo.presentation.ui.movies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.domain.models.Movie;


/**
 * @author Yevgen Oliinykov on 3/20/18.
 */

public class GenrePagerAdapter extends FragmentPagerAdapter implements Filterable {

    private MovieFilter filter;

    private List<Genre> genres = new ArrayList<>();
    private Map<Genre, Collection<Movie>> movies = new HashMap<>();
    private boolean isFiltered;
    private int baseId = 0;

    public GenrePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Genre genre = this.genres.get(position);
        return MoviesListFragment.newInstance(genre, this.movies.get(genre));
    }

    // workaround for bug in FragmentPagerAdapter -> on data set changed fragments remains cached with old position tag
    @Override
    public long getItemId(int position) {
        return baseId + position;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int position = POSITION_UNCHANGED;
        MoviesListFragment fragment = (MoviesListFragment) object;
        Genre genre = getGenreFromFragment(fragment);
        if (genre != null) {
            int correctPosition = this.genres.indexOf(genre);
            position = correctPosition >= 0 ? correctPosition : POSITION_NONE;
            fragment.updateMoviesList(new ArrayList<>(this.movies.get(genre)));
        }

        return position;
    }

    @Override
    public int getCount() {
        return genres.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = genres.get(position).getName();

        if (isFiltered)
            title += String.format(Locale.getDefault(), " (%d)", getMoviesByPosition(position).size());

        return title;
    }

    public void update(Map<Genre, Collection<Movie>> movies) {
        // workaround for bug in FragmentPagerAdapter -> on data set changed fragments remains cached with old position tag
        baseId += movies.size();
        this.movies = movies;
        this.genres = buildGenresList(movies);

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MovieFilter(this, this.movies);
        }
        return filter;
    }

    public void invalidateFilter() {
        this.filter = null;
    }

    private Genre getGenreFromFragment(Fragment fragment) {
        if (fragment == null)
            return null;

        Bundle args = fragment.getArguments();
        if (args != null && args.containsKey(MoviesListFragment.ARG_GENRE)) {
            return Parcels.unwrap(fragment.getArguments().getParcelable(MoviesListFragment.ARG_GENRE));
        }

        return null;
    }

    private List<Genre> buildGenresList(Map<Genre, Collection<Movie>> movies) {
        return Stream.of(movies)
                .filter(x -> x.getValue().size() > 0)
                .map(Map.Entry::getKey)
                .sortBy(Genre::getId)
                .toList();
    }

    private Collection<Movie> getMoviesByPosition(int position) {
        return movies.get(genres.get(position));
    }

    private static class MovieFilter extends Filter {

        private Map<Genre, Collection<Movie>> origMovies = new HashMap<>();
        private GenrePagerAdapter adapter;

        public MovieFilter(GenrePagerAdapter adapter, Map<Genre, Collection<Movie>> origMovies) {
            this.origMovies = new HashMap<>(origMovies);
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            Map<Genre, Collection<Movie>> filtered = new HashMap<>(this.origMovies);
            adapter.isFiltered = false;
            if (this.origMovies.size() > 0 && constraint.length() > 0) {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                Stream.of(filtered).forEach(entry -> {
                    List<Movie> filteredMovies = Stream.of(entry.getValue())
                            .filter(getPredicate(filterPattern))
                            .toList();
                    entry.setValue(filteredMovies);
                });
                adapter.isFiltered = true;
            }
            results.values = filtered;
            results.count = filtered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Map<Genre, Collection<Movie>> movies = (Map<Genre, Collection<Movie>>) results.values;
            this.adapter.update(movies);
        }

        protected Predicate<Movie> getPredicate(String filterPattern) {
            return m -> m.getName().toLowerCase().contains(filterPattern) ||
                    m.getDescription().toLowerCase().contains(filterPattern);
        }
    }
}
