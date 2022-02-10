package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView date_taken, travelled,places_traveled,
    contact_with_infected,contactSetting,vaccinated,first_dose,secondDose;
    private LinearLayout nothingDataLV,dataView;

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
                        if(response.body() != null) {
                            nothingDataLV.setVisibility(View.GONE);
                            dataView.setVisibility(View.VISIBLE);
                            date_taken.setText(response.body().getDate_taken());
                            travelled.setText(response.body().getTravelled());
                            places_traveled.setText(response.body().getPlaces_traveled());
                            contact_with_infected.setText(response.body().getContact_with_infected());
                            contactSetting.setText(response.body().getContact_setting());
                            vaccinated.setText(response.body().getVaccinated());
                            first_dose.setText(response.body().getFirst_dose());
                            secondDose.setText(response.body().getSecond_dose());
                        }
                    });
                }
                else if (response.code() == 401) {

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
        date_taken = findViewById(R.id.date_taken_tv);
        travelled = findViewById(R.id.travelled_in_the_last_14_days_tv);
        places_traveled = findViewById(R.id.places_travelled_tv);
        contact_with_infected = findViewById(R.id.contact_with_infected_tv);
        contactSetting = findViewById(R.id.contact_setting_tv);
        vaccinated = findViewById(R.id.vaccinated_tv);
        first_dose = findViewById(R.id.fist_dose_tv);
        secondDose = findViewById(R.id.second_dose_tv);
        nothingDataLV = findViewById(R.id.nothingDataLV);
        dataView = findViewById(R.id.dataView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}