package org.chskenya.covidapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class PatientHomePageActivity extends AppCompatActivity {
    private static final String TAG = "PatientHomePageActivity";

    private RelativeLayout covidManagementBtn, triageBtn, screeningBtn, contactTracingBtn, labbtn,
            radiologyBtn, patientManagementBtn, linkageBtn;
    private LinearLayout patientHistoryBtn;
    private TextView inpatientName, closePatientSession;

    private Patient inpatient;

    private PatientDB patientDB;
    private User user;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_page);

        initViews();

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        List<Patient> savedPatients = patientDB.getPatientListDAO().getAllPatients();
        Log.d(TAG, "onCreate: Patients: " + savedPatients);
        for (int i = 0; i < savedPatients.size(); i++) {
            Patient patient = savedPatients.get(i);
            if (patient.getActive() == 1) {
                inpatient = patient;
                Log.d(TAG, "onCreate: inpatient" + inpatient);
            }
        }

        inpatientName.setText(inpatient.getFirstName() + " " + inpatient.getSurname());

        triageBtn.setOnClickListener(v -> startActivity(new Intent(this, TriageActivity.class)));

        screeningBtn.setOnClickListener(v -> startActivity(new Intent(this, ScreeningActivity.class)));

        closePatientSession.setOnClickListener(v -> runOnUiThread(() -> new SweetAlertDialog(PatientHomePageActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.warning))
                .setConfirmButton(getResources().getString(R.string.okay), sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    inpatient.setActive(0);
                    patientDB.getPatientListDAO().updatePatient(inpatient);
                    Log.d(TAG, "onCreate: checked out patient" + inpatient);

                    startActivity(new Intent(PatientHomePageActivity.this, MainActivity.class));
                })
                .setCancelButton(getString(R.string.cancel), SweetAlertDialog::dismissWithAnimation)
                .setContentText(getString(R.string.check_out_patient))
                .show()
        ));

        contactTracingBtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, ContactLinelistActivity.class)));

        labbtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, LabRequestActivity.class)));

        radiologyBtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, RadiologyActivity.class)));

//        covidManagementBtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, PatientManagementActivity.class)));

        patientManagementBtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, PatientManagentActivity.class)));

//        linkageBtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, LinkageActivity.class)));

        patientHistoryBtn.setOnClickListener(v -> startActivity(new Intent(PatientHomePageActivity.this, PatientHistoryActivity.class)));

    }

    private void initViews() {
//        covidManagementBtn = findViewById(R.id.covidManagementBtn);
        triageBtn = findViewById(R.id.triageBtn);
        inpatientName = findViewById(R.id.inpatientName);
        closePatientSession = findViewById(R.id.closePatientSession);
        screeningBtn = findViewById(R.id.screeningBtn);
        contactTracingBtn = findViewById(R.id.contactTracingBtn);
        patientManagementBtn = findViewById(R.id.patientManagementBtn);
        patientHistoryBtn = findViewById(R.id.patientHistoryBtn);
//        linkageBtn = findViewById(R.id.linkageBtn);
        labbtn = findViewById(R.id.labbtn);
        radiologyBtn = findViewById(R.id.radiologyBtn);
        patientDB = PatientDB.getInstance(PatientHomePageActivity.this);
    }

}