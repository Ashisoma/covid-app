package org.chskenya.covidapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.InitialDataDB;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class RadiologyActivity extends AppCompatActivity {
    private static final String TAG = "RadiologyActivity";

    private InitialData initialData;
    private EditText etDateRequested, etDateDone, etComments;
    private SearchableSpinner testTypeSpinner, resultSpinner;
    private Button btnSubmit;

    private User user;
    private Patient inpatient;
    private PatientDB patientDB;

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendar2 = Calendar.getInstance();

    private SweetAlertDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology);

        initViews();

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);

            startActivity(new Intent(RadiologyActivity.this, LoginActivity.class));
            finish();
        }

        List<Patient> savedPatients = patientDB.getPatientListDAO().getAllPatients();
        Log.d(TAG, "onCreate: Patients: " + savedPatients);
        for (int i = 0; i < savedPatients.size(); i++) {
            Patient patient = savedPatients.get(i);
            if (patient.getActive() == 1) {
                inpatient = patient;
            }
        }

        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar, etDateRequested);
        };
        etDateRequested.setOnClickListener(v -> new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        DatePickerDialog.OnDateSetListener date2 = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar2, etDateDone);
        };
        etDateDone.setOnClickListener(v -> new DatePickerDialog(this, date2, myCalendar2
                .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                myCalendar2.get(Calendar.DAY_OF_MONTH)).show());

        String[] testTypes = initialData.getRadiologyTestTypes();
        ArrayAdapter<String> testTypesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, testTypes);
        testTypeSpinner.setAdapter(testTypesAdapter);

        String[] testResults = initialData.getXrayResults();
        ArrayAdapter<String> testResultsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, testResults);
        resultSpinner.setAdapter(testResultsAdapter);

        btnSubmit.setOnClickListener(v -> {

            /*
            SearchableSpinner mySpinner = (SearchableSpinner)findViewById(R.id.spinner_configurable_item);
            int selectedItemOfMySpinner = mySpinner.getSelectedItemPosition();
            String actualPositionOfMySpinner = (String) mySpinner.getItemAtPosition(selectedItemOfMySpinner);

            if (actualPositionOfMySpinner.isEmpty()) {
                setSpinnerError(mySpinner,"field can't be empty");
            }
             */

            String date_requested = etDateRequested.getText().toString();
            String date_done = etDateDone.getText().toString();
            String results = resultSpinner.getSelectedItem().toString();
            String comments = etComments.getText().toString();
            String test_type = testTypeSpinner.getSelectedItem().toString();
            boolean error = false;

            if (TextUtils.isEmpty(test_type)) {
                error = true;
                Toast.makeText(this, "Select Test Type.", Toast.LENGTH_SHORT).show();
                setSpinnerError(testTypeSpinner,"This field cannot be empty");
            } else if (TextUtils.isEmpty(date_requested)) {
                error = true;
                Toast.makeText(this, "Select date the radiology was requested.", Toast.LENGTH_SHORT).show();
//                return;
            } else if (TextUtils.isEmpty(date_done)) {
                error = true;
                etDateDone.setError("Select date the radiology was done.");
                Toast.makeText(this, "Select date the radiology was done.", Toast.LENGTH_SHORT).show();
            } else if (results.isEmpty()) {
                error = true;
                Toast.makeText(this, "Select date the radiology was done.", Toast.LENGTH_SHORT).show();
                setSpinnerError(resultSpinner,"This field cannot be empty");
            } else if (TextUtils.isEmpty(comments)) {
                error = true;
                Toast.makeText(this, "Enter a comment about the radiology procedure", Toast.LENGTH_SHORT).show();
            }

            if (!error) {
                System.out.println(error);
                runOnUiThread(() -> pDialog.show());
                AuthRetrofitApiClient.getInstance(RadiologyActivity.this)
                        .getAuthorizedApi()
                        .saveRadiologyRequest(inpatient.getId(), date_requested, test_type,
                                date_done, results, comments, user.getId())
                        .enqueue(new Callback<Patient>() {
                            @Override
                            public void onResponse(@NotNull Call<Patient> call, @NotNull Response<Patient> response) {
                                runOnUiThread(() -> pDialog.dismissWithAnimation());
                                if (response.code() == 200) {
                                    runOnUiThread(() -> new SweetAlertDialog(RadiologyActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.patient_save_data_success))
                                            .setConfirmButton(getString(R.string.main_activity), dialog -> {
                                                dialog.dismissWithAnimation();

                                                startActivity(new Intent(RadiologyActivity.this, PatientHomePageActivity.class));
                                                finish();
                                            })
                                            .show());
                                } else if (response.code() == 401) {
                                    runOnUiThread(() -> new SweetAlertDialog(RadiologyActivity.this,
                                            SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.session_expiry_error))
                                            .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
                                                editor.putString("token", "");
                                                editor.commit();
                                                SessionManager.INSTANCE.setUser(null);
                                                startActivity(new Intent(RadiologyActivity.this, LoginActivity.class));
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
                                    new SweetAlertDialog(RadiologyActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        etDateRequested = findViewById(R.id.etDateRequested);
        etDateDone = findViewById(R.id.etDateDone);
        testTypeSpinner = findViewById(R.id.testTypeSpinner);
        resultSpinner = findViewById(R.id.resultSpinner);
        etComments = findViewById(R.id.etComments);
        btnSubmit = findViewById(R.id.btnSubmit);
        InitialDataDB initialDataDB = InitialDataDB.getInstance(RadiologyActivity.this);
        initialData = initialDataDB.getInitialDataDAO().getInitialData();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.saving_patient));
        pDialog.setCancelable(false);
        patientDB = PatientDB.getInstance(RadiologyActivity.this);
    }

    private void updateLabel(Calendar myCalendar, EditText editText) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }
    private void setSpinnerError(SearchableSpinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }
}