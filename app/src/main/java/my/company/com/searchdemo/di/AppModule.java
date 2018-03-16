package my.company.com.searchdemo.di;

import android.app.Application;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import my.company.com.searchdemo.AppExecutors;
import my.company.com.searchdemo.BuildConfig;
import my.company.com.searchdemo.data.Repository;
import my.company.com.searchdemo.data.net.api.IRestApiService;
import my.company.com.searchdemo.data.net.helpers.MockResponseInterceptor;
import my.company.com.searchdemo.data.net.mappers.GenreDtoToGenreMapper;
import my.company.com.searchdemo.data.net.mappers.MovieDtoToMovieMapper;
import my.company.com.searchdemo.domain.IRepository;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Yevgen Oliinykov on 3/15/18.
 */
@Module(includes = ViewModelModule.class)
class AppModule {

    @Provides
    @Singleton
    static OkHttpClient provideOkHttpClient(Application app) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        if (BuildConfig.BUILD_TYPE.equals("debugLocal"))
        {
            httpClientBuilder.addInterceptor(new MockResponseInterceptor(app, null));
        }

        return httpClientBuilder.build();
    }

    @Singleton
    @Provides
    IRestApiService provideRestApiService(OkHttpClient httpClient, AppExecutors executors) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory
                        .createWithScheduler(Schedulers.from(executors.networkIO())))
                .client(httpClient)
                .build()
                .create(IRestApiService.class);
    }

    @Singleton
    @Provides
    IRepository provideRepository(IRestApiService apiService) {
        return new Repository(apiService, new MovieDtoToMovieMapper(), new GenreDtoToGenreMapper());
    }

}
