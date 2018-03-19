package my.company.com.searchdemo.data;

import android.support.v4.util.Pair;

import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import my.company.com.searchdemo.AppExecutors;
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
    private final AppExecutors appExecutors;
    private final MovieDtoToMovieMapper movieMapper;
    private final GenreDtoToGenreMapper genreMapper;

    @Inject
    public Repository(IRestApiService apiService, AppExecutors appExecutors,
                      MovieDtoToMovieMapper movieMapper, GenreDtoToGenreMapper genreMapper) {
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.movieMapper = movieMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public Single<Collection<Genre>> getAllGenres() {
        return this.apiService.getGenres()
                .subscribeOn(Schedulers.from(this.appExecutors.networkIO()))
                .map(this.genreMapper::map)
                .observeOn(Schedulers.from(this.appExecutors.mainThread()));

    }

    @Override
    public Single<Collection<Movie>> getAllMoviesByGenre(long genreId) {
        return this.apiService.getMoviesByGenre(genreId)
                .subscribeOn(Schedulers.from(this.appExecutors.networkIO()))
                .map(this.movieMapper::map)
                .observeOn(Schedulers.from(this.appExecutors.mainThread()));
    }

    @Override
    public Single<Map<Genre, Collection<Movie>>> getAllMovies(List<Genre> genres) {
        return Observable.just(genres)
                .subscribeOn(Schedulers.from(this.appExecutors.networkIO()))
                .observeOn(Schedulers.from(this.appExecutors.mainThread()))
                .flatMapIterable(x -> x)
                .flatMap(g -> this.apiService.getMoviesByGenre(g.getId()).map(this.movieMapper::map).map(m -> Pair.create(g,m)).toObservable())
                .collect(HashMap::new, new BiConsumer<Map<Genre, Collection<Movie>>, Pair<Genre, Collection<Movie>>>() {
                    @Override
                    public void accept(Map<Genre, Collection<Movie>> map, Pair<Genre, Collection<Movie>> pair) throws Exception {
                        map.put(pair.first, pair.second);
                    }
                });
    }

}
