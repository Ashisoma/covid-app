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
import org.chskenya.covidapp.model.Radiology;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RadiologyResultsActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Toolbar toolbar;
    private User user;
    private Patient inpatient;
    private PatientDB patientDB;
    private LinearLayout viewData, nothingHere;
    private TextView date_requested_tv, results, date_done_tv, comments_tv, test_type_tv;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology_results);
        initViews();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(RadiologyResultsActivity.this, LoginActivity.class));
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

        getLatestRadiology();

    }

    private void getLatestRadiology(){
        AuthRetrofitApiClient.getInstance(this)
                .getAuthorizedApi()
                .getRadiologyData(inpatient.getId())
                .enqueue(new Callback<Radiology>() {
                    @Override
                    public void onResponse(Call<Radiology> call, Response<Radiology> response) {
                        if (response.code() == 200) {
                            runOnUiThread(() -> {
                                System.out.println(response.body());
                                if(response.body() != null) {
                                    viewData.setVisibility(View.VISIBLE);
                                    nothingHere.setVisibility(View.GONE);
                                    test_type_tv.setText(response.body().getTest_type());
                                    date_requested_tv.setText(response.body().getDate_requested());
                                    date_done_tv.setText(response.body().getDate_done());
                                    comments_tv.setText(response.body().getComments());
                                    results.setText(response.body().getResults());
                                }
                            });
                        } else if (response.code() == 401) {
                            System.out.println("============================== RESPONSE CODE 401================================");
                        }
                    }

                    @Override
                    public void onFailure(Call<Radiology> call, Throwable t) {
                        System.out.println("============================== RESPONSE CODE 500================================");
                    }
                });
            }


    private void initViews() {
        patientDB = PatientDB.getInstance(RadiologyResultsActivity.this);
        toolbar = findViewById(R.id.toolbar);
        nothingHere = findViewById(R.id.nothingHere);
        viewData = findViewById(R.id.viewData);
        date_requested_tv = findViewById(R.id.date_requested_tv);
        date_done_tv = findViewById(R.id.date_done_tv);
        comments_tv = findViewById(R.id.comments_tv);
        test_type_tv = findViewById(R.id.test_types_tv);
        results = findViewById(R.id.radiology_results_tv);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}