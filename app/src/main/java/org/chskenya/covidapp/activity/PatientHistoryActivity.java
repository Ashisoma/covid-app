package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientHistory;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;

import java.util.List;

import retrofit2.Response;

public class PatientHistoryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Toolbar toolbar;
    private User user;
    private Patient inpatient;
    private PatientDB patientDB;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history_acivity);

        initViews();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(PatientHistoryActivity.this, LoginActivity.class));
            finish();
        }

        List<Patient> savedPatients = patientDB.getPatientListDAO().getAllPatients();
        Log.d(TAG, "onCreate: Patients: " + savedPatients);
        for (int i = 0; i < savedPatients.size(); i++) {
            Patient patient = savedPatients.get(i);
            if (patient.getActive() == 1) {
                inpatient = patient;
                Log.d(TAG, "onCreate: inpatient" + inpatient);
            }
        }

        toolbar.setTitle(inpatient.getFirstName() + " " + inpatient.getSecondName() + " " + inpatient.getSurname());
        setSupportActionBar(toolbar);

        getPatientHistory();
        System.out.println("===========================++++++=========HEREEEEE++++++============================");

    }



    private void getPatientHistory(){
        AuthRetrofitApiClient.getInstance(this)
                .getAuthorizedApi()
                .getPatientHistoryRequest(inpatient.getId()).enqueue(new retrofit2.Callback<PatientHistory>() {
            @Override
            public void onResponse(retrofit2.Call<PatientHistory> call, Response<PatientHistory> response) {
                if (response.code() == 200) {
                    runOnUiThread(()-> {
                        System.out.println(response.body());
                    });
                }
                else if (response.code() == 401) {
               /*     runOnUiThread(() -> new SweetAlertDialog(PatientHistoryActivity.this,
                                    SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.session_expiry_error))
                                    .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
//                                        logout();
                                    })
                                    .show()


                    );
        */
                    Log.d(TAG, "onResponse: " + response.message());
                }

            }

            @Override
            public void onFailure(retrofit2.Call<PatientHistory> call, Throwable t) {
                System.out.println("==============================NDURUUUU WEUHHHHHWUEHHHHHHHHH=========================================");
            }
        });
    }

    private void initViews() {
        patientDB = PatientDB.getInstance(PatientHistoryActivity.this);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}