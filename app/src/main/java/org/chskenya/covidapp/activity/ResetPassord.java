package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import org.chskenya.covidapp.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPassord extends AppCompatActivity {
    private EditText etPassword, etNumber, etConfirmPassword;
    private Button btnResetPassord;
    private SweetAlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passord);
        initViews();
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