package org.chskenya.covidapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.retrofit.RetrofitApiClient;
import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etPassword, etNumber, etConfirmPassword;

    private String password, passconfirm;
    private SweetAlertDialog progressDialog;

    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        password = etPassword.getText().toString().trim();
        passconfirm = etConfirmPassword.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        if (password.isEmpty()) {
            etPassword.setError(getResources().getString(R.string.password_error));
        } else if (passconfirm.isEmpty()) {
            etConfirmPassword.setError(getResources().getString(R.string.confirm_password_error));
        } else if (!passconfirm.equals(password)) {
            etConfirmPassword.setError(getResources().getString(R.string.pass_dont_match_error));
        } else if (!isValidPhoneNumber(number)) {
            etNumber.setError(getResources().getString(R.string.phone_format_error));
        } else {
                progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                RetrofitApiClient.getInstance(RegisterActivity.this).
                        getApi()
                        .registerUser(number, password)
                        .enqueue(new Callback<User[]>() {
                            @Override
                            public void onResponse(@NotNull Call<User[]> call, @NotNull Response<User[]> response) {
                                runOnUiThread(progressDialog::dismissWithAnimation);
                                if (response.isSuccessful()) {
                                    password = null;
                                    passconfirm = null;
                                    runOnUiThread(() -> new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.register_success))
                                            .setConfirmButton(getString(R.string.log_in), dialog -> {
                                                dialog.dismissWithAnimation();
                                                onBackPressed();
                                            })
                                            .show());
                                } else {
                                    if (response.code() == 400) {
                                        runOnUiThread(() -> new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.error_title))
                                                .setContentText(getString(R.string.this_number) + " "
                                                        + number + " " + getString(R.string.unrecognized_number_error))
                                                .setConfirmText(getResources().getString(R.string.okay))
                                                .show()
                                        );
                                    } else {
                                        runOnUiThread(() -> new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.error_title))
                                                .setContentText(getResources().getString(R.string.error_message))
                                                .setConfirmText(getResources().getString(R.string.okay))
                                                .show()
                                        );
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<User[]> call, @NotNull Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                                runOnUiThread(progressDialog::dismissWithAnimation);
                                runOnUiThread(() -> new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.error_title))
                                        .setContentText(getResources().getString(R.string.internet_error))
                                        .setConfirmText(getResources().getString(R.string.okay))
                                        .show()
                                );
                            }
                        });
        }
    }

    private void initViews() {
        etPassword = findViewById(R.id.etPassword);
        etNumber = findViewById(R.id.etNumber);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    public void signIn(View view) {
        onBackPressed();
    }

    private boolean isValidPhoneNumber(String phone) {
        if (!phone.trim().equals("") && phone.length() == 10 && TextUtils.isDigitsOnly(phone)) {
            return Patterns.PHONE.matcher(phone).matches();
        }
        return false;
    }
}