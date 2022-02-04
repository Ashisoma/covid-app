package org.chskenya.covidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.adapter.PatientContactListViewAdapter;
import org.chskenya.covidapp.fragment.AddContactBottomSheetFragment;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.util.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ContactLinelistActivity extends AppCompatActivity {

    private static final String TAG = "ContactLinelistActivity";
    private Toolbar toolbar;
    private RecyclerView rvLinelistView;
    private LinearLayout noContactsLayout;

    private User user;
    private Patient inpatient;
    private PatientDB patientDB;

    private List<PatientContact> patientContactList = new ArrayList<>();

    private AddContactBottomSheetFragment addContactBottomSheetFragment;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_linelist);

        initViews();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(ContactLinelistActivity.this, LoginActivity.class));
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getPatientContacts();

        FloatingActionButton fab = findViewById(R.id.addContactFab);
        fab.setOnClickListener(view -> showSheet(inpatient, ContactLinelistActivity.this));
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        noContactsLayout = findViewById(R.id.noContactsLayout);
        rvLinelistView = findViewById(R.id.rvLinelistView);
        patientDB = PatientDB.getInstance(ContactLinelistActivity.this);
    }

    private void getPatientContacts() {
        patientContactList = Arrays.asList(inpatient.getPatientContacts());
        if (patientContactList.isEmpty()) {
            noContactsLayout.setVisibility(View.VISIBLE);
            rvLinelistView.setVisibility(View.GONE);
        } else {
            noContactsLayout.setVisibility(View.GONE);
            rvLinelistView.setVisibility(View.VISIBLE);

            populateRecyclerView(patientContactList);
        }
       /* AuthRetrofitApiClient.getInstance(this)
                .getAuthorizedApi()
                .getPatientContacts(inpatient.getId())
                .enqueue(new Callback<List<PatientContact>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<PatientContact>> call, @NotNull Response<List<PatientContact>> response) {
                        if (response.code() == 200) {
                            runOnUiThread(()-> {
                                patientContactList = response.body();
                                assert patientContactList != null;
                                if (patientContactList.isEmpty()) {
                                    noContactsLayout.setVisibility(View.VISIBLE);
                                    rvLinelistView.setVisibility(View.GONE);
                                } else {
                                    noContactsLayout.setVisibility(View.GONE);
                                    rvLinelistView.setVisibility(View.VISIBLE);

                                    populateRecyclerView(patientContactList);
                                }
                            });
                        } else if (response.code() == 401) {
                            runOnUiThread(() -> new SweetAlertDialog(ContactLinelistActivity.this,
                                    SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.session_expiry_error))
                                    .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
                                        logout();
                                    })
                                    .show()
                            );
                            Log.d(TAG, "onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<PatientContact>> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);

                        runOnUiThread(() -> new SweetAlertDialog(ContactLinelistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                .setConfirmText(getResources().getString(R.string.okay))
                                .show());
                    }
                });*/
    }

    public void populateRecyclerView(List<PatientContact> patientContactList) {
        rvLinelistView.setLayoutManager(new LinearLayoutManager(ContactLinelistActivity.this));
        rvLinelistView.setHasFixedSize(true);

        PatientContactListViewAdapter patientContactListViewAdapter = new PatientContactListViewAdapter(ContactLinelistActivity.this);
        rvLinelistView.setAdapter(patientContactListViewAdapter);
        patientContactListViewAdapter.setPatientContactList(patientContactList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (addContactBottomSheetFragment != null && addContactBottomSheetFragment.isVisible()) {
            addContactBottomSheetFragment.dismissAllowingStateLoss();
        }
    }

    public void showSheet(Patient inpatient, Context context) {
        addContactBottomSheetFragment = AddContactBottomSheetFragment.newInstance(inpatient, context);
        addContactBottomSheetFragment.show(getSupportFragmentManager(), "add_contact");
    }

    public void logout() {
        editor.putString("token", "");
        editor.commit();
        SessionManager.INSTANCE.setUser(null);
        startActivity(new Intent(ContactLinelistActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}