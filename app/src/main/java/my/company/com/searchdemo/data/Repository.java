package my.company.com.searchdemo.data;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import my.company.com.searchdemo.data.net.api.IRestApiService;
import my.company.com.searchdemo.data.net.mappers.GenreDtoToGenreMapper;
import my.company.com.searchdemo.data.net.mappers.MovieDtoToMovieMapper;
import my.company.com.searchdemo.domain.IRepository;
import my.company.com.searchdemo.domain.models.Genre;
import my.company.com.searchdemo.domain.models.Movie;

/**
 * @author Yevgen Oliinykov on 3/14/18.
 */
@Singleton
public class Repository implements IRepository {

    private final IRestApiService apiService;
    private final MovieDtoToMovieMapper movieMapper;
    private final GenreDtoToGenreMapper genreMapper;

    @Inject
    public Repository(IRestApiService apiService, MovieDtoToMovieMapper movieMapper,
                      GenreDtoToGenreMapper genreMapper) {
        this.apiService = apiService;
        this.movieMapper = movieMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public Single<Collection<Genre>> getAllGenres() {
        return this.apiService.getGenres().map(this.genreMapper::map);
    }

    @Override
    public Single<Collection<Movie>> getAllMoviesByGenre(long genreId) {
        return this.apiService.getMoviesByGenre(genreId).map(this.movieMapper::map);
    }

}
