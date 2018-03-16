package my.company.com.searchdemo.presentation.helpers;

import io.reactivex.functions.Consumer;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */

public interface PlainConsumer<T> extends Consumer<T> {

    @Override
    void accept(T t);
}
