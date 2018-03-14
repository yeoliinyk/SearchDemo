package my.company.com.searchdemo.data.net.api;

import java.util.Collection;
import java.util.List;

import io.reactivex.Single;
import my.company.com.searchdemo.data.net.dto.GenreDto;
import my.company.com.searchdemo.data.net.dto.MovieDto;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Yevgen Oliinykov on 3/14/18.
 */

public interface IRestApiService {

    @GET("api/genres/")
    Single<Collection<GenreDto>> getGenres();

    @GET("api/movies/")
    Single<Collection<MovieDto>> getMoviesByGenre(@Query("genreId") long genreId);

}
