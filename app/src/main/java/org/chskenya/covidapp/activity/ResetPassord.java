package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.retrofit.RetrofitApiClient;
import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassord extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";
    private EditText etPassword, etNumber, etConfirmPassword;
    private Button btnResetPassord;
    private SweetAlertDialog progressDialog;

    private String password, passconfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passord);
        initViews();
    }

    private void resetUserPassword() {
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
            RetrofitApiClient.getInstance(ResetPassord.this).
                    getApi()
                    .resetPassword(number,password)
                    .enqueue(new Callback<User[]>() {
                        @Override
                        public void onResponse(@NotNull Call<User[]> call, @NotNull Response<User[]> response) {
                            runOnUiThread(progressDialog::dismissWithAnimation);
                            if (response.isSuccessful()) {
                                password = null;
                                passconfirm = null;
                                runOnUiThread(() -> new SweetAlertDialog(ResetPassord.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(getString(R.string.success))
                                        .setContentText(getString(R.string.reset_success))
                                        .setConfirmButton(getString(R.string.log_in), dialog -> {
                                            dialog.dismissWithAnimation();
                                            onBackPressed();
                                        })
                                        .show());
                            } else {
                                if (response.code() == 400) {
                                    runOnUiThread(() -> new SweetAlertDialog(ResetPassord.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getString(R.string.this_number) + " "
                                                    + number + " " + getString(R.string.unrecognized_number_error))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show()
                                    );
                                } else {
                                    runOnUiThread(() -> new SweetAlertDialog(ResetPassord.this, SweetAlertDialog.ERROR_TYPE)
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
                            runOnUiThread(() -> new SweetAlertDialog(ResetPassord.this, SweetAlertDialog.ERROR_TYPE)
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
        etPassword = findViewById(R.id.etPassResetPassword);
        etNumber = findViewById(R.id.etPassResetNumber);
        etConfirmPassword = findViewById(R.id.etPassResetConfirmPassword);
        btnResetPassord = findViewById(R.id.btnResetPass);

        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    private boolean isValidPhoneNumber(String phone) {
        if (!phone.trim().equals("") && phone.length() == 10 && TextUtils.isDigitsOnly(phone)) {
            return Patterns.PHONE.matcher(phone).matches();
        }
        return false;
    }
}