package my.company.com.searchdemo.domain.models;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * @author Yevgen Oliinykov on 3/13/18.
 */
@Parcel(Parcel.Serialization.BEAN)
public class Movie {

    private long id;
    private String name;
    private String description;

    @ParcelConstructor
    public Movie(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;

        Movie movie = (Movie) o;

        if (id != movie.id) return false;
        if (!name.equals(movie.name)) return false;
        return description != null ? description.equals(movie.description) : movie.description == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
