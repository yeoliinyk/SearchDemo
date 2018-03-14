package my.company.com.searchdemo.data.net.mappers;

import java.util.ArrayList;
import java.util.Collection;

import my.company.com.searchdemo.data.net.dto.GenreDto;
import my.company.com.searchdemo.domain.models.Genre;

/**
 * @author Yevgen Oliinykov on 3/14/18.
 */

public class GenreDtoToGenreMapper implements Mapper<GenreDto, Genre> {

    @Override
    public Genre map(GenreDto source) {
        return new Genre(source.id, source.name);
    }

    @Override
    public Collection<Genre> map(Collection<GenreDto> source) {
        Collection<Genre> result = new ArrayList<>();

        for (GenreDto dto : source)
            result.add(map(dto));

        return result;
    }
}
