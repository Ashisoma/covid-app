package org.chskenya.covidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.fragment.AddContactBottomSheetFragment;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    }


    private void initViews() {
        patientDB = PatientDB.getInstance(PatientHistoryActivity.this);
        toolbar = findViewById(R.id.toolbar);
    }
}