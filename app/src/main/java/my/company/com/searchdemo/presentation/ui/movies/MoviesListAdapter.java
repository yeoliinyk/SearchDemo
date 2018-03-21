package my.company.com.searchdemo.presentation.ui.movies;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import my.company.com.searchdemo.R;
import my.company.com.searchdemo.databinding.ItemMovieBinding;
import my.company.com.searchdemo.domain.models.Movie;
import my.company.com.searchdemo.presentation.base.DataBoundListAdapter;
import timber.log.Timber;

/**
 * @author Yevgen Oliinykov on 3/17/18.
 */

public class MoviesListAdapter extends DataBoundListAdapter<Movie, ItemMovieBinding>
        implements Filterable {

    private List<Movie> originalList = new ArrayList<>();
    private final MovieClickCallback movieClickCallback;
    private MovieFilter filter;
    private String searchQuery = "";

    public MoviesListAdapter(MovieClickCallback movieClickCallback) {
        this.movieClickCallback = movieClickCallback;
    }

    @Override
    protected ItemMovieBinding createBinding(ViewGroup parent) {
        ItemMovieBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_movie, parent, false);

        binding.getRoot().setOnClickListener(v -> {
            Movie movie = binding.getMovie();
            if (movie != null && movieClickCallback != null)
                movieClickCallback.onClick(movie);
        });

        return binding;
    }

    @Override
    protected void bind(ItemMovieBinding binding, Movie item) {
//        binding.setMovie(item);
        if (searchQuery.length() > 0) {
            //color your text here
            SpannableStringBuilder sb = new SpannableStringBuilder(item.getName());
            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);
            int index = item.getName().toLowerCase().indexOf(searchQuery);
            while (index > -1) {
                 //specify color here
                sb.setSpan(fcs, index, index + searchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                index = item.getName().toLowerCase().indexOf(searchQuery, index + 1);
            }
            binding.name.setText(sb);
        } else {
            binding.name.setText(item.getName());
        }
        binding.desc.setText(item.getDescription());
    }

    @Override
    protected boolean areItemsTheSame(Movie oldItem, Movie newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    protected boolean areContentsTheSame(Movie oldItem, Movie newItem) {
        return oldItem.getId() == newItem.getId();
    }

    public void setOriginalList(List<Movie> originalList) {
        this.originalList = originalList;
    }

    public List<Movie> getOriginalList() {
        return originalList;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MovieFilter(this, this.originalList);
        }
        return filter;
    }

    public void replace(List<Movie> update, String searchQuery) {
        this.searchQuery = searchQuery;
        super.replace(update);
    }

    private static class MovieFilter extends Filter {

        private final MoviesListAdapter adapter;
        private final List<Movie> originalList;
        private final List<Movie> filteredList;

        private MovieFilter(MoviesListAdapter adapter, List<Movie> originalList) {
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            Timber.i("Perform filtering on thread -> %s", Thread.currentThread().getId());
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Movie movie : originalList) {
                    if (movie.getName().toLowerCase().contains(filterPattern) ||
                            movie.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(movie);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<Movie> result = (ArrayList<Movie>) filterResults.values;
            this.adapter.replace(result);
        }
    }

    public interface MovieClickCallback {
        void onClick(Movie movie);
    }
}
