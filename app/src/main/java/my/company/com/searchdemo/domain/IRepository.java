package my.company.com.searchdemo.domain;

import java.util.Collection;

import io.reactivex.Single;
import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.domain.models.Movie;

/**
 * @author Yevgen Oliinykov on 3/13/18.
 */

public interface IRepository {
    Single<Collection<Genre>> getAllGenres();
    Single<Collection<Movie>> getAllMoviesByGenre(long genreId);
}
