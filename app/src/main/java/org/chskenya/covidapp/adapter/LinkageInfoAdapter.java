package org.chskenya.covidapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LinkageInfoAdapter extends PagerAdapter {
    private static final String TAG = "LinkageInfoAdapter";

    private final InitialData initialData;
    private final Context context;
    private final int[] layouts;

    private Patient inpatient;

    final Calendar myCalendar  = Calendar.getInstance();

    private View pageOne = null,
            pageTwo = null,
            pageThree = null;

    public LinkageInfoAdapter(Context context, InitialData initialData, int[] layouts) {
        this.initialData = initialData;
        this.context = context;
        this.layouts = layouts;

        PatientDB patientDB = PatientDB.getInstance(context);
        List<Patient> savedPatients = patientDB.getPatientListDAO().getAllPatients();
        Log.d(TAG, "onCreate: Patients: " + savedPatients);
        for (int i = 0; i < savedPatients.size(); i++) {
            Patient patient = savedPatients.get(i);
            if (patient.getActive() == 1) {
                inpatient = patient;
                Log.d(TAG, "onCreate: inpatient" + inpatient);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d(TAG, "instantiateItem: Container " + container.getChildCount());
        Log.i(TAG, "instantiateItem: Position " + position);

        View view = inflater.inflate(layouts[position], container, false);

        switch (position) {
            case 0: {
                if (pageOne == null) {
                    pageOne = view;

                    EditText etNames = view.findViewById(R.id.etNames);
                    etNames.setText(inpatient.getFirstName() + " " + inpatient.getSecondName() + " " + inpatient.getSurname());

                    EditText etgender = view.findViewById(R.id.etgender);
                    etgender.setText(inpatient.getGender());

                    EditText etAge = view.findViewById(R.id.etAge);

                } else view = pageOne;
                break;
            }
            case 1: {
                if (pageTwo == null) {
                    pageTwo = view;

                } else view = pageTwo;
                break;
            }
            case 2: {
                if (pageThree == null) {
                    pageThree = view;

                } else view = pageThree;
                break;
            }
        }
        container.addView(view);
        return view;
    }

    private int getAge(Calendar myCalendar) {
        Calendar now = Calendar.getInstance();
        double diff = (long)(now.getTimeInMillis() - myCalendar.getTimeInMillis());
        int years = -1;
        if (diff < 0) {
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(context.getResources().getString(R.string.error_title))
                    .setContentText(context.getResources().getString(R.string.sweetalert_error_message))
                    .setConfirmText(context.getResources().getString(R.string.okay))
                    .show();
        } else {
            // Get difference between years
            years =  now.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);
        }
        return years;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}
