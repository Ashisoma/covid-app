package org.chskenya.covidapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.adapter.ScreeningInfoAdapter;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.chskenya.covidapp.util.SessionManager;
import org.chskenya.covidapp.util.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class ScreeningActivity extends AppCompatActivity {
    private static final String TAG = "ScreeningActivity";
    private ViewPager viewPager;
    private int[] layouts;
    private Button next, previous;

    private User user;
    private Patient inpatient;
    private PatientDB patientDB;

    private Utility utility;

    private SweetAlertDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screening);

        initViews();

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(ScreeningActivity.this, LoginActivity.class));
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

        //setup layouts
        layouts = new int[]{
                R.layout.screening_info1,
                R.layout.screening_info2,
                R.layout.screening_info3
        };

        ScreeningInfoAdapter screeningInfoAdapter = new ScreeningInfoAdapter(this, layouts);
        viewPager.setAdapter(screeningInfoAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //change next button text
                if (position == layouts.length - 1) {
                    next.setText(getString(R.string.finish));
                    previous.setVisibility(View.VISIBLE);
                } else if (position == 0) {
                    previous.setVisibility(View.GONE);
                } else {
                    previous.setVisibility(View.VISIBLE);
                    next.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        next.setOnClickListener(v -> {
            int current = getItem(+1);

            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                runOnUiThread(()-> pDialog.show());
                AuthRetrofitApiClient.getInstance(ScreeningActivity.this)
                        .getAuthorizedApi()
                        .savePatientScreeningData(inpatient.getId(), user.getId(), screeningInfoAdapter.getFeverHistory(),
                                screeningInfoAdapter.getWeaknessHistory(), screeningInfoAdapter.getCoughHistory(),
                                screeningInfoAdapter.getSoreThroatHistory(), screeningInfoAdapter.getRunnyNoseHistory(),
                                screeningInfoAdapter.getWeightLossHistory(), screeningInfoAdapter.getNightSweatsHistory(),
                                screeningInfoAdapter.getTasteLossHistory(), screeningInfoAdapter.getSmellLossHistory(),
                                screeningInfoAdapter.getBreathingHistory(), screeningInfoAdapter.getDiarrhoeaHistory(),
                                screeningInfoAdapter.getHeadacheHistory(), screeningInfoAdapter.getIrritabilityHistory(),
                                screeningInfoAdapter.getNauseaHistory(), screeningInfoAdapter.getBreathShortnessHistory(),
                                screeningInfoAdapter.getPainHistory())
                        .enqueue(new Callback<Patient>() {
                            @Override
                            public void onResponse(@NotNull Call<Patient> call, @NotNull Response<Patient> response) {
                                runOnUiThread(()-> pDialog.dismissWithAnimation());
                                if (response.code() == 200) {
                                    Patient p = response.body();
                                    runOnUiThread(() -> new SweetAlertDialog(ScreeningActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.patient_save_data_success))
                                            .setConfirmButton(getString(R.string.main_activity), dialog -> {
                                                dialog.dismissWithAnimation();

                                                startActivity(new Intent(ScreeningActivity.this, PatientHomePageActivity.class));
                                                finish();
                                            })
                                            .show());
                                } else if (response.code() == 401) {
                                    runOnUiThread(() -> new SweetAlertDialog(ScreeningActivity.this,
                                            SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.session_expiry_error))
                                            .setConfirmButton(getString(R.string.log_in), sweetAlertDialog -> {
                                                editor.putString("token", "");
                                                editor.commit();
                                                SessionManager.INSTANCE.setUser(null);
                                                startActivity(new Intent(ScreeningActivity.this, LoginActivity.class));
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
                                    new SweetAlertDialog(ScreeningActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show();
                                });
                            }
                        });
            }

        });

        previous.setOnClickListener(v2 -> {
            int prev = getItem(-1);
            if (prev >= 0) {
                viewPager.setCurrentItem(prev);
                next.setText(getString(R.string.next));
            } else {
                Toast.makeText(this, getString(R.string.no_prev_page), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        next = findViewById(R.id.btn_next);
        previous = findViewById(R.id.btn_previous);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.saving_patient));
        pDialog.setCancelable(false);
        patientDB = PatientDB.getInstance(ScreeningActivity.this);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}