package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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

public class ForgotPassword extends AppCompatActivity {
    private static final String TAG = "ForgotPassword";
    private EditText etNumber;
    private Button btnResetPassword;
    private SweetAlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        // TODO: 20/04/2022  change this function to setActiveLow() after the web part has gone live
        btnResetPassword.setOnClickListener( v -> sign());


    }

    private void setActiveLow() {
        String number = etNumber.getText().toString().trim();

         if (!isValidPhoneNumber(number)) {
            etNumber.setError(getResources().getString(R.string.phone_format_error));
        } else {
            progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressDialog.setTitleText(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            RetrofitApiClient.getInstance(ForgotPassword.this).
                    getApi()
                    .resetNumber(number)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                            runOnUiThread(progressDialog::dismissWithAnimation);
                            if (response.isSuccessful()) {

                                runOnUiThread(() -> new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(getString(R.string.success))
                                        .setContentText(getString(R.string.register_success))
                                        .setConfirmButton(getString(R.string.log_in), dialog -> {
                                            dialog.dismissWithAnimation();
                                            Intent intent = new Intent(ForgotPassword.this,ResetPassord.class);
                                            startActivity(intent);
                                        })
                                        .show());
                            } else {
                                if (response.code() == 400) {
                                    runOnUiThread(() -> new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getString(R.string.this_number) + " "
                                                    + number + " " + getString(R.string.unrecognized_number_error))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show()
                                    );
                                } else {
                                    runOnUiThread(() -> new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.error_message))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show()
                                    );
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                            runOnUiThread(progressDialog::dismissWithAnimation);
                            runOnUiThread(() -> new SweetAlertDialog(ForgotPassword.this, SweetAlertDialog.ERROR_TYPE)
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
        etNumber = findViewById(R.id.etResetNumber);
        btnResetPassword = findViewById(R.id.btnReset);
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    public void signIn(View view) {
        onBackPressed();
    }

    public void sign() {
        Intent intent = new Intent(ForgotPassword.this, ResetPassord.class);
        startActivity(intent);
    }

    private boolean isValidPhoneNumber(String phone) {
        if (!phone.trim().equals("") && phone.length() == 10 && TextUtils.isDigitsOnly(phone)) {
            return Patterns.PHONE.matcher(phone).matches();
        }
        return false;
    }


}