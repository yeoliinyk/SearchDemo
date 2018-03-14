package my.company.com.searchdemo.data.net.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Yevgen Oliinykov on 3/14/18.
 */

public class GenreDto {

    @SerializedName("id")
    public long id;

    @SerializedName("name")
    public String name;
}
