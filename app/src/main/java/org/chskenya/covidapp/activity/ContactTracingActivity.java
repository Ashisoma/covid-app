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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.County;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.model.Subcounty;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.InitialDataDB;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;
import org.chskenya.covidapp.util.Utility;
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

public class ContactTracingActivity extends AppCompatActivity {
    private static final String TAG = "ContactTracingActivity";

    private EditText tracingDate, testingDate;
    private SearchableSpinner countySpinner, subcountySpinner, tracedSpinner, testedSpinner, testOutcomeSpinner;
    private LinearLayout testedLayout, contactTracedLayout;
    private Button btnSubmit;
    private TextView contactInfo;

    final Calendar myCalendar  = Calendar.getInstance();
    final Calendar myCalendar2  = Calendar.getInstance();

    County county;
    private County[] counties;
    private InitialData initialData;
    private InitialDataDB initialDataDB;

    private User user;
    private Patient inpatient;
    private PatientDB patientDB;

    private PatientContact patientContact;
    private Utility utility;

    private SweetAlertDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private List<String> optionList;

    private String contact_traced, tracing_date, contact_tested, testing_date, test_outcome;

    int countyCode = 0, subcountyId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_tracing);

        initViews();

        contactInfo.setText(patientContact.getFirstName() + " " + patientContact.getMiddleName() + " " + patientContact.getSurname());

        ArrayAdapter<String> tracedAdapter = new ArrayAdapter<>(ContactTracingActivity.this,
                android.R.layout.simple_spinner_item, optionList);
        tracedSpinner.setAdapter(tracedAdapter);

        tracedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = optionList.get(position);
                if (option.equals("Yes")) {
                    contactTracedLayout.setVisibility(View.VISIBLE);
                } else {
                    contactTracedLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        counties = initialData.getCounties();
        ArrayAdapter<County> countySpinnerAdapter = new ArrayAdapter<>(ContactTracingActivity.this,
                R.layout.spinner_layout, R.id.tvName, counties);
        countySpinner.setAdapter(countySpinnerAdapter);

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                County county = counties[position];
                ArrayAdapter<Subcounty> subcountySpinnerAdapter = new ArrayAdapter<>(ContactTracingActivity.this,
                        R.layout.spinner_layout, R.id.tvName, county.getSubcounties());
                subcountySpinner.setAdapter(subcountySpinnerAdapter);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar, tracingDate);
        };

        tracingDate.setOnClickListener(v -> new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        ArrayAdapter<String> testedAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, optionList);
        testedSpinner.setAdapter(testedAdapter);

        testedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = optionList.get(position);
                if (option.equals("Yes")) {
                    testedLayout.setVisibility(View.VISIBLE);
                } else {
                    testedLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DatePickerDialog.OnDateSetListener date2 = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar2, testingDate);
        };
        testingDate.setOnClickListener(v -> new DatePickerDialog(this, date2, myCalendar2
                .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                myCalendar2.get(Calendar.DAY_OF_MONTH)).show());

        List<String> statusList = new ArrayList<>();
        statusList.add("Positive");
        statusList.add("Negative");

        ArrayAdapter<String> testOutcomeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusList);
        testOutcomeSpinner.setAdapter(testOutcomeAdapter);

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(ContactTracingActivity.this, LoginActivity.class));
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

        btnSubmit.setOnClickListener(v -> {
            contact_traced = "";
            if (tracedSpinner.getSelectedItem() != null) {
                contact_traced = tracedSpinner.getSelectedItem().toString();
            }
            if (countySpinner.getSelectedItem() != null) {
                county = counties[countySpinner.getSelectedItemPosition()];
                countyCode = county.getCode();
            }
            if (subcountySpinner.getSelectedItem() != null) {
                Subcounty[] subcounties = county.getSubcounties();
                Subcounty subcounty = subcounties[subcountySpinner.getSelectedItemPosition()];
                subcountyId = subcounty.getId();
            }
            tracing_date = tracingDate.getText().toString();
            contact_tested = "";
            if (testedSpinner.getSelectedItem() != null) {
                contact_tested = testedSpinner.getSelectedItem().toString();
            }
            testing_date = testingDate.getText().toString();
            test_outcome = "";
            if (testOutcomeSpinner.getSelectedItem() != null) {
                test_outcome = testOutcomeSpinner.getSelectedItem().toString();
            }

            boolean error = false;

            if (contact_traced.equals("Yes")) {
                if (countyCode == 0) {
                    error = true;
                    Toast.makeText(this, "Select County", Toast.LENGTH_SHORT).show();
                } else if (subcountyId == 0) {
                    error = true;
                    Toast.makeText(this, "Select sub county", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tracing_date)) {
                    error = true;
                    Toast.makeText(this, "Enter Tracing date", Toast.LENGTH_SHORT).show();
                } else if (contact_tested.equals("")) {
                    error = true;
                    Toast.makeText(this, "Select option for whether the contact was tested.", Toast.LENGTH_SHORT).show();
                } else if (contact_tested.equals("Yes")) {
                    if (TextUtils.isEmpty(testing_date)) {
                        error = true;
                        Toast.makeText(this, "Enter date tested", Toast.LENGTH_SHORT).show();
                    } else if (test_outcome.equals("")) {
                        error = true;
                        Toast.makeText(this, "Select whether the contact was positive or negative.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (contact_traced.equals("")) {
                error = true;
                Toast.makeText(this, "Select whether the contact has been traced or not", Toast.LENGTH_SHORT).show();
            }

            if (!error) {
                submitData();
            }
        });

    }

    private void submitData() {
        runOnUiThread(() -> pDialog.show());
        AuthRetrofitApiClient.getInstance(ContactTracingActivity.this)
                .getAuthorizedApi()
                .savePatientContactTracingData(patientContact.getId(), countyCode,
                        subcountyId, contact_traced, tracing_date, user.getId(),
                        contact_tested, testing_date, test_outcome)
                .enqueue(new Callback<PatientContact>() {
                    @Override
                    public void onResponse(@NotNull Call<PatientContact> call, @NotNull Response<PatientContact> response) {
                        runOnUiThread(() -> pDialog.dismissWithAnimation());
                        if (response.code() == 200) {
                            PatientContact patientContact = response.body();
                            runOnUiThread(() -> {
                                if (test_outcome.equals("Positive")) {
                                    new SweetAlertDialog(ContactTracingActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.patient_save_data_success))
                                            .setConfirmButton("Register as new Patient", dialog -> {
                                                dialog.dismissWithAnimation();
                                                SessionManager.INSTANCE.setPatientContact(patientContact);
                                                startActivity(new Intent(ContactTracingActivity.this, PatientRegistrationActivity.class));
                                                finish();
                                            })
                                            .show();
                                } else {
                                    new SweetAlertDialog(ContactTracingActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.patient_save_data_success))
                                            .setConfirmButton("Back to Contact List page", dialog -> {
                                                dialog.dismissWithAnimation();
                                                startActivity(new Intent(ContactTracingActivity.this, ContactLinelistActivity.class));
                                                finish();
                                            })
                                            .show();
                                }
                            });
                        } else if (response.code() == 401) {
                            runOnUiThread(() -> new SweetAlertDialog(ContactTracingActivity.this,
                                    SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.session_expiry_error))
                                    .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        editor.putString("token", "");
                                        editor.commit();
                                        SessionManager.INSTANCE.setUser(null);
                                        startActivity(new Intent(ContactTracingActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .show()
                            );
                            Log.d(TAG, "onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<PatientContact> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);

                        runOnUiThread(() -> {
                            pDialog.dismissWithAnimation();
                            new SweetAlertDialog(ContactTracingActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show();
                        });
                    }
                });
    }

    private void initViews() {
        btnSubmit = findViewById(R.id.btnSubmit);
        initialDataDB = InitialDataDB.getInstance(ContactTracingActivity.this);
        initialData = initialDataDB.getInitialDataDAO().getInitialData();
        patientContact = (PatientContact) getIntent().getExtras().getSerializable("contact");
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.saving_patient));
        pDialog.setCancelable(false);
        contactInfo = findViewById(R.id.contactInfo);
        countySpinner = findViewById(R.id.countySpinner);
        subcountySpinner = findViewById(R.id.subcountySpinner);
        tracedSpinner = findViewById(R.id.tracedSpinner);
        tracingDate = findViewById(R.id.tracingDate);
        testedSpinner = findViewById(R.id.testedSpinner);
        testedLayout = findViewById(R.id.testedLayout);
        testingDate = findViewById(R.id.testingDate);
        testOutcomeSpinner = findViewById(R.id.testOutcomeSpinner);
        contactTracedLayout = findViewById(R.id.contactTracedLayout);
        patientDB = PatientDB.getInstance(ContactTracingActivity.this);

        optionList = new ArrayList<>();
        optionList.add("Yes");
        optionList.add("No");
    }


    private void updateLabel(Calendar myCalendar, EditText editText) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog.isShowing()) pDialog.dismiss();
    }

}