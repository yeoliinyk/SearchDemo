package my.company.com.searchdemo.presentation.utils;

import android.databinding.BindingAdapter;
import android.view.View;

/**
 * @author Yevgen Oliinykov on 3/19/18.
 */

public class BindingUtils {

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
