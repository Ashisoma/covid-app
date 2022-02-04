package org.chskenya.covidapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class TriageActivity extends AppCompatActivity {

    private static final String TAG = "TriageActivity";
    private Button btnSubmit;
    private EditText etTemp, etWeight, etHeight, etspo2, etzscore;
    private SearchableSpinner coughSpinner, breathingDifficultySpinner, weightLossSpinner;
    private double temp, weight, height, spo2, zscore;

    private List<String> optionList;

    private Patient inpatient;
    private PatientDB patientDB;
    private User user;

    private SweetAlertDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triage);

        initViews();

        List<Patient> savedPatients = patientDB.getPatientListDAO().getAllPatients();
        Log.d(TAG, "onCreate: Patients: " + savedPatients);
        for (int i = 0; i < savedPatients.size(); i++) {
            Patient patient = savedPatients.get(i);
            if (patient.getActive() == 1) {
                inpatient = patient;
                Log.d(TAG, "onCreate: inpatient" + inpatient);
            }
        }
        user = SessionManager.INSTANCE.getUser();

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        ArrayAdapter<String> coughAdapter = new ArrayAdapter<>(TriageActivity.this,
                android.R.layout.simple_spinner_item, optionList);
        coughSpinner.setAdapter(coughAdapter);

        ArrayAdapter<String> breathingDifficultyAdapter = new ArrayAdapter<>(TriageActivity.this,
                android.R.layout.simple_spinner_item, optionList);
        breathingDifficultySpinner.setAdapter(breathingDifficultyAdapter);

        ArrayAdapter<String> weightLossAdapter = new ArrayAdapter<>(TriageActivity.this,
                android.R.layout.simple_spinner_item, optionList);
        weightLossSpinner.setAdapter(weightLossAdapter);

        btnSubmit.setOnClickListener(v -> {
            temp = Double.parseDouble(etTemp.getText().toString());
            weight = Double.parseDouble(etWeight.getText().toString());
            height = Double.parseDouble(etHeight.getText().toString());
            spo2 = Double.parseDouble(etspo2.getText().toString());
            zscore = Double.parseDouble(etzscore.getText().toString());
            String cough = coughSpinner.getSelectedItem().toString();
            String difficulty_in_breathing = breathingDifficultySpinner.getSelectedItem().toString();
            String weight_loss = weightLossSpinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(etTemp.getText().toString().trim())){
                etTemp.setError("Enter current temperature");
            } else if (temp > 44 || temp < 35) {
                etTemp.setError("Enter a valid temperature");
            } else if (TextUtils.isEmpty(etWeight.getText().toString().trim())){
                etWeight.setError("Enter current Weight");
            } else if (weight < 2 || weight > 150) {
                etWeight.setError("Enter a valid Weight");
            } else if (TextUtils.isEmpty(etHeight.getText().toString().trim())){
                etHeight.setError("Enter current height");
            } else if (height > 250) {
                etHeight.setError("Enter a valid height");
            } else if (TextUtils.isEmpty(etspo2.getText().toString().trim())){
                etspo2.setError("Enter current SPO2");
            } else if (spo2 < 1 || spo2 > 100) {
                etspo2.setError("Enter a valid SPO2");
            } else if (TextUtils.isEmpty(etzscore.getText().toString().trim())){
                etzscore.setError("Enter current zscore");
            } else if (zscore > 3) {
                etzscore.setError("Enter a valid zscore");
            } else if (coughSpinner.getSelectedItem() == null) {
                Toast.makeText(this, "Does the patient have a cough?", Toast.LENGTH_SHORT).show();
            } else if (breathingDifficultySpinner.getSelectedItem() == null) {
                Toast.makeText(this, "Does the patient have observed difficulty in breathing?", Toast.LENGTH_SHORT).show();
            } else if (weightLossSpinner.getSelectedItem() == null) {
                Toast.makeText(this, "Does the patient have unintended weight loss?", Toast.LENGTH_SHORT).show();
            } else {
                pDialog.show();
                AuthRetrofitApiClient.getInstance(TriageActivity.this)
                        .getAuthorizedApi()
                        .savePatientTriage(temp, weight, height, spo2, zscore, cough,
                                difficulty_in_breathing, weight_loss, inpatient.getId(), user.getId())
                        .enqueue(new Callback<Patient>() {
                            @Override
                            public void onResponse(@NotNull Call<Patient> call, @NotNull Response<Patient> response) {
                                runOnUiThread(()-> pDialog.dismissWithAnimation());
                                if (response.code() == 200) {
                                    runOnUiThread(() -> new SweetAlertDialog(TriageActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.patient_save_data_success))
                                            .setConfirmButton(getString(R.string.main_activity), dialog -> {
                                                dialog.dismissWithAnimation();
                                                startActivity(new Intent(TriageActivity.this, PatientHomePageActivity.class));
                                                finish();
                                            })
                                            .show()
                                    );
                                } else if (response.code() == 401) {
                                    runOnUiThread(() -> new SweetAlertDialog(TriageActivity.this,
                                            SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.session_expiry_error))
                                            .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
                                                editor.putString("token", "");
                                                editor.commit();
                                                SessionManager.INSTANCE.setUser(null);
                                                startActivity(new Intent(TriageActivity.this, LoginActivity.class));
                                                finish();
                                            })
                                            .show()
                                    );
                                    Log.d(TAG, "onResponse: " + response.message());
                                }
                            }
                            @Override
                            public void onFailure(@NotNull Call<Patient> call, @NotNull Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                                runOnUiThread(() -> {
                                    pDialog.dismissWithAnimation();
                                    new SweetAlertDialog(TriageActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show();
                                });
                            }
                        });
            }
        });
    }

    private void initViews() {
        etTemp = findViewById(R.id.etTemp);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etspo2 = findViewById(R.id.etspo2);
        etzscore = findViewById(R.id.etzscore);
        coughSpinner = findViewById(R.id.coughSpinner);
        breathingDifficultySpinner = findViewById(R.id.breathingDifficultySpinner);
        weightLossSpinner = findViewById(R.id.weightLossSpinner);
        btnSubmit = findViewById(R.id.btnSubmit);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.saving_patient));
        pDialog.setCancelable(false);
        patientDB = PatientDB.getInstance(TriageActivity.this);

        optionList = new ArrayList<>();
        optionList.add("Yes");
        optionList.add("No");
    }
}
