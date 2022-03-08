package org.chskenya.covidapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.adapter.PatientListviewAdapter;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private LinearLayout layoutSearchPatient;
    private ImageView searchPatient;
    private EditText etSearchParam;
    private TextView addPatientBtn;

    private Toolbar toolbar;
    private Patient inpatient;
    private SweetAlertDialog pDialog;

    private PatientDB patientDB;
    private User user;

    private RecyclerView rvPatientView;

    private List<Patient> patientList = new ArrayList<>();

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 07/03/2022 CHANGE THE P,HISTORY TO TRIAGE DATA

        initViews();

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        user = SessionManager.INSTANCE.getUser();
        if (user != null) {
            toolbar.setTitle("Hello, " + user.getNames());
        }
        setSupportActionBar(toolbar);

        List<Patient> savedPatients = patientDB.getPatientListDAO().getAllPatients();
        Log.d(TAG, "onCreate: Patients: " + savedPatients);
        for (int i = 0; i < savedPatients.size(); i++) {
            Patient patient = savedPatients.get(i);
            if (patient.getActive() == 1) {
                inpatient = patient;
                Log.d(TAG, "onCreate: inpatient" + inpatient);
            }
        }

        if (inpatient == null) {
            Log.d(TAG, "onCreate: no patient");
        } else {
            Log.d(TAG, "onCreate: with patient");
            startActivity(new Intent(this, PatientHomePageActivity.class));
        }

        addPatientBtn.setOnClickListener(v1 -> {
            startActivity(new Intent(MainActivity.this, PatientRegistrationActivity.class));
        });

        searchPatient.setOnClickListener(v -> {
            String searchString = etSearchParam.getText().toString().trim();
            if (TextUtils.isEmpty(searchString)) {
                etSearchParam.setError("Enter Phone number or patient ID number");
            } else {
                pDialog.show();
                patientList = patientDB.getPatientListDAO().searchPatients(searchString);
                if(patientList.isEmpty()) {
                    runOnUiThread(() -> {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.no_list))
                                .setConfirmButton(getString(R.string.retry), SweetAlertDialog::dismissWithAnimation);

                    });

                }
                pDialog.dismiss();
                rvPatientView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rvPatientView.setHasFixedSize(true);
                PatientListviewAdapter patientListviewAdapter = new PatientListviewAdapter(MainActivity.this);
                rvPatientView.setAdapter(patientListviewAdapter);
                patientListviewAdapter.setPatientList(patientList);
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        layoutSearchPatient = findViewById(R.id.layoutSearchPatient);
        searchPatient = findViewById(R.id.searchPatient);
        etSearchParam = findViewById(R.id.etSearchParam);
        addPatientBtn = findViewById(R.id.addPatientBtn);
        patientDB = PatientDB.getInstance(MainActivity.this);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.getting_patients));
        pDialog.setCancelable(false);
        rvPatientView = findViewById(R.id.rvPatientView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navLogout) {
            runOnUiThread(() -> {
                SessionManager.INSTANCE.setUser(null);
                editor.putString("token", "");
                editor.commit();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            });
        } //else if (id == R.id.profile) {
//            startActivity(new Intent(this, UserProfile.class));
         else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}