package my.company.com.searchdemo.data.net.mappers;

import java.util.Collection;

/**
 * @author Yevgen Oliinykov on 3/14/18.
 */

public interface Mapper<S, R> {
    R map(S source);
    Collection<R> map(Collection<S> source);
}
