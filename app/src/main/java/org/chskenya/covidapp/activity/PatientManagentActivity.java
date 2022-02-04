package org.chskenya.covidapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.adapter.PatientHistoryAdapter;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.User;
import org.chskenya.covidapp.offlineRoom.InitialDataDB;
import org.chskenya.covidapp.util.SessionManager;
import org.chskenya.covidapp.util.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static org.chskenya.covidapp.util.Constants.MY_PREFS_NAME;

public class PatientManagentActivity extends AppCompatActivity {
    private static final String TAG = "PatientHistoryActivity";

    private ViewPager viewPager;
    private int[] layouts;
    private Button next, previous;

    private User user;

    private InitialData initialData;
    private InitialDataDB initialDataDB;
    private Utility utility;

    private SweetAlertDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        initViews();

        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        user = SessionManager.INSTANCE.getUser();
        if (user == null) {
            editor.putString("token", "");
            editor.commit();
            SessionManager.INSTANCE.setUser(null);
            startActivity(new Intent(PatientManagentActivity.this, LoginActivity.class));
            finish();
        }

        //setup layouts
        layouts = new int[]{
                R.layout.patient_history_info1,
                R.layout.patient_history_info2
        };

        PatientHistoryAdapter patientHistoryAdapter = new PatientHistoryAdapter(this, initialData, layouts);
        viewPager.setAdapter(patientHistoryAdapter);
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
                runOnUiThread(() -> new SweetAlertDialog(PatientManagentActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.success))
                        .setContentText(getString(R.string.patient_save_data_success))
                        .setConfirmButton(getString(R.string.main_activity), dialog -> {
                            dialog.dismissWithAnimation();

                            startActivity(new Intent(PatientManagentActivity.this, PatientHomePageActivity.class));
                            finish();
                        })
                        .show());
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
        initialDataDB = InitialDataDB.getInstance(PatientManagentActivity.this);
        initialData = initialDataDB.getInitialDataDAO().getInitialData();
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}