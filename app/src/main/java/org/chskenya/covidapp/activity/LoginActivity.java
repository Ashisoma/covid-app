package org.chskenya.covidapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.retrofit.RetrofitApiClient;
import org.chskenya.covidapp.util.Constants;
import org.chskenya.covidapp.util.SessionManager;
import org.chskenya.covidapp.util.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final String MY_PREFS_NAME = Constants.MY_PREFS_NAME;
    SharedPreferences pref;
    SharedPreferences.Editor tokeneditor;
    private EditText etPassword, etNumber;
    private Button btnLogin;

    private SweetAlertDialog pDialog;
    private String number, password;

    private Utility utility;

    List<Patient> patientList = new ArrayList<>();
    private PatientDB patientDB;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AuthRetrofitApiClient.resetRetrofitClient();

        initViews();

        utility = new Utility(LoginActivity.this);

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        tokeneditor = pref.edit();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }

        btnLogin.setOnClickListener(v -> {
            btnLogin.setEnabled(false);
            number = etNumber.getText().toString().trim();
            if (!isValidPhoneNumber(number)) {
                etNumber.setError(getResources().getString(R.string.phone_format_error));
            } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
                etPassword.setError(getString(R.string.password_error));
            } else {
                pDialog.show();
                password = etPassword.getText().toString();

                RetrofitApiClient.getInstance(this)
                        .getApi()
                        .login(number, password)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                                if (response.isSuccessful()) {
                                    user = response.body();

                                    password = null;
                                    if (pDialog.isShowing()) {
                                        pDialog.dismiss();
                                    }

                                    runOnUiThread(() -> {
                                        if (user != null) {
                                            SessionManager.INSTANCE.setUser(user);
                                            tokeneditor.putString("token", user.getToken());
                                            tokeneditor.commit();

                                            getPatients();
                                        } else {
                                            Log.d(TAG, "onResponse: user null");
                                        }
                                    });
                                } else {
                                    pDialog.dismiss();
                                    runOnUiThread(() -> {
                                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText(getResources().getString(R.string.error_title))
                                                        .setContentText(getResources().getString(R.string.unauthorized_user_error))
                                                        .setConfirmText(getResources().getString(R.string.okay))
                                                        .show();
                                                if (!btnLogin.isEnabled()) btnLogin.setEnabled(true);
                                            }
                                    );
                                    Log.d(TAG, response.message());
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                                pDialog.dismiss();
                                runOnUiThread(() -> {
                                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText(getResources().getString(R.string.error_title))
                                                    .setContentText(getResources().getString(R.string.server_access_error))
                                                    .setConfirmText(getResources().getString(R.string.okay))
                                                    .show();
                                            if (!btnLogin.isEnabled()) btnLogin.setEnabled(true);
                                        }
                                );
                                Log.d(TAG, t.toString());
                            }
                        });
            }

        });
    }

    private void getPatients() {
        runOnUiThread(() -> {
            pDialog.setTitleText("Getting Patients");
            pDialog.show();
        });
        AuthRetrofitApiClient.getInstance(LoginActivity.this)
                .getAuthorizedApi()
                .getPatients(user.getFacility())
                .enqueue(new Callback<List<Patient>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Patient>> call, @NotNull Response<List<Patient>> response) {
                        runOnUiThread(()-> pDialog.dismissWithAnimation());
                        if (response.code() == 200) {

                            runOnUiThread(() -> {
                                patientList = response.body();

                                for (int i = 0; i < patientList.size(); i++) {
                                    patientDB.getPatientListDAO().insertPatients(patientList.get(i));
                                }

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                        SweetAlertDialog d = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getString(R.string.error_title))
                                                .setContentText(getResources().getString(R.string.session_expiry_error))
                                                .setConfirmButton(getString(R.string.log_in), SweetAlertDialog::dismissWithAnimation);
                                        d.setOnDismissListener(dialog -> {
                                            SessionManager.INSTANCE.setUser(null);
                                            tokeneditor.putString("token", "");
                                            tokeneditor.commit();
                                            d.dismissWithAnimation();
                                        });
                                        d.show();
                                    }
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Patient>> call, @NotNull Throwable t) {
                        runOnUiThread(() -> {
                            pDialog.dismissWithAnimation();
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        });
                    }
                });
    }

    private void initViews() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        etNumber = findViewById(R.id.etNumber);
        etPassword = findViewById(R.id.passwordedt);
        btnLogin = findViewById(R.id.btnLogin);
        patientDB = PatientDB.getInstance(LoginActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private boolean isValidPhoneNumber(String phone) {

        if (!phone.equals("") && phone.length() == 10 && TextUtils.isDigitsOnly(phone)) {
            return Patterns.PHONE.matcher(phone).matches();
        }
        return false;
    }
}