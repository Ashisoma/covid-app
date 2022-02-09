package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.Lab;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_lab);
        initViews();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(LabActivity.this, LoginActivity.class));
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

        getLabLatestLabResults();

    }

    private void getLabLatestLabResults(){
        AuthRetrofitApiClient.getInstance(this)
                .getAuthorizedApi()
                .getLabResultsRequest(inpatient.getId())
                .enqueue(new Callback<Lab>() {
                    @Override
                    public void onResponse(Call<Lab> call, Response<Lab> response) {
                        if (response.code() == 200) {
                            runOnUiThread(() -> {
                                System.out.println(response.body());

                            });
                        } else if (response.code() == 401) {
                            System.out.println("============================== RESPONSE CODE 401================================");
                        }
                    }

                    @Override
                    public void onFailure(Call<Lab> call, Throwable t) {
                        System.out.println("============================== RESPONSE CODE 500================================");
                    }
                });
    }


    private void initViews() {
        patientDB = PatientDB.getInstance(LabActivity.this);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}