package org.chskenya.covidapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.InitialDataDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.retrofit.RetrofitApiClient;
import org.chskenya.covidapp.util.Constants;
import org.chskenya.covidapp.util.SessionManager;
import org.chskenya.covidapp.util.Utility;
import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String token = "";

    private InitialData data;
    private InitialDataDB initialDataDB;
    private Utility utility;

    private LinearLayout gettingDataLayout;
    private TextView loadDataTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initViews();

        pref = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();
        utility = new Utility(SplashScreenActivity.this);

        initialCheck();
    }

    private void initViews() {
        gettingDataLayout = findViewById(R.id.gettingDataLayout);
        loadDataTxt = findViewById(R.id.loadDataTxt);
        initialDataDB = InitialDataDB.getInstance(SplashScreenActivity.this);
    }

    private void initialCheck() {
        data = initialDataDB.getInitialDataDAO().getInitialData();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            runOnUiThread(() -> {
                if (data != null) {
                    initialDataDB.getInitialDataDAO().deleteInitialData(data);
                }
                getInitialData();
            });
        } else {
            runOnUiThread(() -> {
                if (data == null) {
                    SweetAlertDialog networkErrorDialog = new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.error_title))
                            .setContentText(getResources().getString(R.string.sweetalert_error_message))
                            .setConfirmText(getResources().getString(R.string.okay))
                            .setCancelButton(R.string.exit_app, sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                SplashScreenActivity.this.finishAffinity();
                            })
                            .setConfirmButton(R.string.retry, sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                initialCheck();
                            });
                    networkErrorDialog.setCancelable(false);
                    networkErrorDialog.show();
                } else {
                    checkToken();
                }
            });
        }
    }

    private void getInitialData() {
        runOnUiThread(() -> gettingDataLayout.setVisibility(View.VISIBLE));
        RetrofitApiClient.getInstance(SplashScreenActivity.this).getApi()
                .getInitialData()
                .enqueue(new Callback<InitialData>() {
                    @Override
                    public void onResponse(@NotNull Call<InitialData> call, @NotNull Response<InitialData> response) {
                        if (response.code() == 200) {
                            data = response.body();
                            runOnUiThread(() -> {
                                if (data != null) {
                                    initialDataDB.getInitialDataDAO().insertData(data);
                                    checkToken();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                gettingDataLayout.setVisibility(View.GONE);
                                new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.error_title))
                                        .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                        .setConfirmText(getResources().getString(R.string.okay))
                                        .show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<InitialData> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: Could not retrieve data. ", t);
                        runOnUiThread(() -> {
                            gettingDataLayout.setVisibility(View.GONE);
                            new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show();
                        });
                    }
                });
    }

    private void checkToken() {
        token = pref.getString("token", null);
        if (token == null || token.equals("")) {
            new Handler().postDelayed(() -> {
                if (data == null) getInitialData();
                else {
                    gettingDataLayout.setVisibility(View.GONE);
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            verifyToken();
        }
    }


    private void verifyToken() {
        runOnUiThread(() -> loadDataTxt.setText(getResources().getString(R.string.verify_session)));
        AuthRetrofitApiClient.getInstance(SplashScreenActivity.this)
                .getAuthorizedApi()
                .verifyToken()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                        runOnUiThread(() -> gettingDataLayout.setVisibility(View.GONE));
                        if (response.code() == 200) {
                            User user = response.body();

                            runOnUiThread(() -> {
                                SessionManager.INSTANCE.setUser(user);
                                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Log.d(TAG, "onResponse: Session Expired");
                                clearToken();
                                SessionManager.INSTANCE.setUser(null);
                                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                                finish();
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                        runOnUiThread(() -> {
                            gettingDataLayout.setVisibility(View.GONE);
                            SweetAlertDialog d = new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setCancelButton(R.string.exit_app, sweetAlertDialog -> {
                                        clearToken();
                                        sweetAlertDialog.dismissWithAnimation();
                                        SplashScreenActivity.this.finishAffinity();
                                    })
                                    .setConfirmButton(R.string.retry, sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        if (data == null) getInitialData();
                                    });
                            d.setCancelable(false);
                            d.setCanceledOnTouchOutside(false);
                            d.show();
                        });
                    }
                });
    }

    private void clearToken() {
        editor.putString("token", "");
        editor.commit();
    }
}