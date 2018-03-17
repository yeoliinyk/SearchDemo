package my.company.com.searchdemo.presentation.ui.movies;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import my.company.com.searchdemo.R;
import my.company.com.searchdemo.databinding.ItemMovieBinding;
import my.company.com.searchdemo.domain.models.Movie;
import my.company.com.searchdemo.presentation.base.DataBoundListAdapter;

/**
 * @author Yevgen Oliinykov on 3/17/18.
 */

public class MoviesListAdapter extends DataBoundListAdapter<Movie, ItemMovieBinding> {

    private final MovieClickCallback movieClickCallback;

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
        binding.setMovie(item);
    }

    @Override
    protected boolean areItemsTheSame(Movie oldItem, Movie newItem) {
        return false;
    }

    @Override
    protected boolean areContentsTheSame(Movie oldItem, Movie newItem) {
        return false;
    }

    public interface MovieClickCallback {
        void onClick(Movie movie);
    }
}
