package org.chskenya.covidapp.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.chskenya.covidapp.util.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class AuthRetrofitApiClient {
    private static final String TAG = "AuthRetrofitApiClient";
    private static AuthRetrofitApiClient client;

    public static final String base_url = Constants.BASE_URL;

    public static String token;

    private static final String MY_PREFS_NAME = Constants.MY_PREFS_NAME;

    private final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    OkHttpClient authorizedOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(chain -> {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder()
                        .addHeader("UUID", Constants.UUID)
                        .addHeader("TOKEN", token);
                request = requestBuilder.build();
                return chain.proceed(request);
            })
            .build();

    Retrofit authorizedretrofit = new Retrofit.Builder()
            .client(authorizedOkHttpClient)
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static synchronized AuthRetrofitApiClient getInstance(Context context) {
        if (client == null) {
            SharedPreferences pref = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            token  = pref.getString("token", null);
            Log.d(TAG, "getInstance: token" + token);
            client = new AuthRetrofitApiClient();
        }

        return client;
    }

    public static void resetRetrofitClient(){
        client = null;
    }
    public ApiClient getAuthorizedApi() {
        return authorizedretrofit.create(ApiClient.class);
    }
}
