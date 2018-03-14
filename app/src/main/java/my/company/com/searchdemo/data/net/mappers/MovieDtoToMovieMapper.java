package my.company.com.searchdemo.data.net.mappers;

import java.util.ArrayList;
import java.util.Collection;

import my.company.com.searchdemo.data.net.dto.MovieDto;
import my.company.com.searchdemo.domain.models.Movie;

/**
 * @author Yevgen Oliinykov on 3/14/18.
 */

public class MovieDtoToMovieMapper implements Mapper<MovieDto, Movie>{

    @Override
    public Movie map(MovieDto source) {
        return new Movie(source.id, source.name, source.description);
    }

    @Override
    public Collection<Movie> map(Collection<MovieDto> source) {
        Collection<Movie> result = new ArrayList<>();

        for (MovieDto dto : source)
            result.add(map(dto));

        return result;
    }
}
