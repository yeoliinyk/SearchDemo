package my.company.com.searchdemo.presentation.ui.movies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.domain.models.Movie;
import timber.log.Timber;

/**
 * @author Yevgen Oliinykov on 3/20/18.
 */

public class GenrePagerAdapter extends FragmentStatePagerAdapter implements Filterable {

    private MovieFilter filter;

    private List<Genre> genres = new ArrayList<>();
    private Map<Genre, Collection<Movie>> movies = new HashMap<>();
    private boolean isFiltered;

    public GenrePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Genre genre = this.genres.get(position);
        return MoviesListFragment.newInstance(genre, this.movies.get(genre));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int position = super.getItemPosition(object);

        MoviesListFragment fragment = (MoviesListFragment) object;
        if (fragment.getArguments().containsKey(MoviesListFragment.ARG_GENRE))
        {
            Genre genre = Parcels.unwrap(fragment.getArguments().getParcelable(MoviesListFragment.ARG_GENRE));
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

    public void updateGenres(List<Genre> genres, Map<Genre, Collection<Movie>> movies) {
        this.genres = genres;
        this.movies = movies;
        notifyDataSetChanged();
    }

    private Collection<Movie> getMoviesByPosition(int position) {
        return movies.get(genres.get(position));
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MovieFilter(this, this.genres, this.movies);
        }
        return filter;
    }

    private static class MovieFilter extends Filter {
        private List<Genre> genres = new ArrayList<>();
        private Map<Genre, Collection<Movie>> movies = new HashMap<>();
        private GenrePagerAdapter adapter;

        public MovieFilter(GenrePagerAdapter adapter, List<Genre> origGenres, Map<Genre,
                Collection<Movie>> origMovies) {
            this.genres = new ArrayList<>(origGenres);
            this.movies = new HashMap<>(origMovies);
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Timber.i("Perform filtering on thread -> %s", Thread.currentThread().getId());
            final FilterResults results = new FilterResults();
            Map<Genre, Collection<Movie>> filtered = new HashMap<>(this.movies);
            adapter.isFiltered = constraint.length() > 0;
            if (constraint.length() > 0) {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                Stream.of(filtered).forEach(x -> {
                    x.setValue(Stream.of(x.getValue()).filter(m -> m.getName().toLowerCase().contains(filterPattern) ||
                            m.getDescription().toLowerCase().contains(filterPattern)).toList());
                });
            }
            results.values = filtered;
            results.count = filtered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Map<Genre, Collection<Movie>> movies = (Map<Genre, Collection<Movie>>) results.values;
            List<Genre> genres = Stream.of(movies).filter(x -> x.getValue().size() > 0).map(Map.Entry::getKey)
                    .sortBy(Genre::getId).toList();
            this.adapter.updateGenres(genres, movies);
        }
    }
}
