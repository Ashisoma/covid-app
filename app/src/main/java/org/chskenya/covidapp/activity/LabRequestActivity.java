package org.chskenya.covidapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.LabTestType;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.InitialDataDB;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class LabRequestActivity extends AppCompatActivity {
    private static final String TAG = "LabRequestActivity";

    private SearchableSpinner specimenCollectedSpinner, specimenTypeSpinner, testTypeSpinner;
    private LinearLayout notCollectedLayout, specimenCollectedLayout;
    private Button btnSubmit;
    private EditText etReason, etDateSpecimenCollected, etDateSpecimenSent;
    private TextView errorTxt;

    private Patient inpatient;
    private User user;
    private InitialData initialData;
    private PatientDB patientDB;

    final Calendar myCalendar  = Calendar.getInstance();
    final Calendar myCalendar2  = Calendar.getInstance();

    private SweetAlertDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_request);

        initviews();

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

        List<String> optionList = new ArrayList<>();
        optionList.add("Yes");
        optionList.add("No");

        ArrayAdapter<String> specimenCollectedAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, optionList);
        specimenCollectedSpinner.setAdapter(specimenCollectedAdapter);

        specimenCollectedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = optionList.get(position);
                if (option.equals("Yes")) {
                    specimenCollectedLayout.setVisibility(View.VISIBLE);
                    notCollectedLayout.setVisibility(View.GONE);
                } else {
                    notCollectedLayout.setVisibility(View.VISIBLE);
                    specimenCollectedLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LabTestType[] labTestTypes = initialData.getLabTestTypes();
        ArrayAdapter<LabTestType> testTypesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labTestTypes);
        testTypeSpinner.setAdapter(testTypesAdapter);

        testTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LabTestType labTestType = labTestTypes[position];
                ArrayAdapter<String> specimenTypeSpinnerAdapter = new ArrayAdapter<>(LabRequestActivity.this,
                        R.layout.spinner_layout, R.id.tvName, labTestType.getSpecimenTypes());
                specimenTypeSpinner.setAdapter(specimenTypeSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar, etDateSpecimenCollected);
        };
        etDateSpecimenCollected.setOnClickListener(v -> new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        DatePickerDialog.OnDateSetListener date2 = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar2, etDateSpecimenSent);
        };
        etDateSpecimenSent.setOnClickListener(v -> new DatePickerDialog(this, date2, myCalendar2
                .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                myCalendar2.get(Calendar.DAY_OF_MONTH)).show());

        btnSubmit.setOnClickListener(v -> {
            String specimen_collected = specimenCollectedSpinner.getSelectedItem().toString();
            String reason_not_collected = etReason.getText().toString();
            String test_type = testTypeSpinner.getSelectedItem().toString();
            String specimen_type = specimenTypeSpinner.getSelectedItem().toString();
            String date_collected = etDateSpecimenCollected.getText().toString();
            String date_sent_to_lab = etDateSpecimenSent.getText().toString();

            errorTxt.setVisibility(View.GONE);

            boolean error = false;

            if (specimen_collected.equals("No")) {
                if (TextUtils.isEmpty(reason_not_collected)) {
                    error = true;
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("Provide reason why specimen was not collected.");
                }
            } else if (specimen_collected.equals("Yes")) {
                if (TextUtils.isEmpty(specimen_type)) {
                    error = true;
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("Select Specimen type");
                } else if (TextUtils.isEmpty(date_collected)) {
                    error = true;
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("Select Date the Specimen was collected");
                } else if (TextUtils.isEmpty(date_sent_to_lab)) {
                    error = true;
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("Select Date Specimen was sent to lab");
                }
            } else if (specimenCollectedSpinner.getSelectedItem() == null) {
                error = true;
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Select option whether specimen was collected or not");
            }

            if (!error) {
                runOnUiThread(() -> pDialog.show());
                AuthRetrofitApiClient.getInstance(LabRequestActivity.this)
                        .getAuthorizedApi()
                        .saveLabRequest(inpatient.getId(), user.getId(), specimen_collected,
                                reason_not_collected, test_type, specimen_type, date_collected, date_sent_to_lab)
                        .enqueue(new Callback<Patient>() {
                            @Override
                            public void onResponse(@NotNull Call<Patient> call, @NotNull Response<Patient> response) {
                                runOnUiThread(() -> pDialog.dismissWithAnimation());
                                if (response.code() == 200) {
                                    runOnUiThread(() -> new SweetAlertDialog(LabRequestActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.patient_save_data_success))
                                            .setConfirmButton(getString(R.string.main_activity), dialog -> {
                                                dialog.dismissWithAnimation();

                                                startActivity(new Intent(LabRequestActivity.this, PatientHomePageActivity.class));
                                                finish();
                                            })
                                            .show()
                                    );
                                } else if (response.code() == 401) {
                                    runOnUiThread(() -> new SweetAlertDialog(LabRequestActivity.this,
                                            SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.session_expiry_error))
                                            .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
                                                editor.putString("token", "");
                                                editor.commit();
                                                SessionManager.INSTANCE.setUser(null);
                                                startActivity(new Intent(LabRequestActivity.this, LoginActivity.class));
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
                                    new SweetAlertDialog(LabRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void initviews() {
        specimenCollectedSpinner = findViewById(R.id.specimenCollectedSpinner);
        notCollectedLayout = findViewById(R.id.notCollectedLayout);
        specimenCollectedLayout = findViewById(R.id.specimenCollectedLayout);
        specimenTypeSpinner = findViewById(R.id.specimenTypeSpinner);
        btnSubmit = findViewById(R.id.btnSubmit);
        etReason = findViewById(R.id.etReason);
        etDateSpecimenCollected = findViewById(R.id.etDateSpecimenCollected);
        etDateSpecimenSent = findViewById(R.id.etDateSpecimenSent);
        errorTxt = findViewById(R.id.errorTxt);
        testTypeSpinner = findViewById(R.id.testTypeSpinner);
        InitialDataDB initialDataDB = InitialDataDB.getInstance(LabRequestActivity.this);
        initialData = initialDataDB.getInitialDataDAO().getInitialData();
        patientDB = PatientDB.getInstance(LabRequestActivity.this);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.saving_patient));
        pDialog.setCancelable(false);
        patientDB = PatientDB.getInstance(LabRequestActivity.this);
    }

    private void updateLabel(Calendar myCalendar, EditText editText) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog.isShowing()) {
            pDialog.dismissWithAnimation();
        }
    }
}