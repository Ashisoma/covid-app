package org.chskenya.covidapp.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.County;
import org.chskenya.covidapp.model.Facility;
import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.model.Subcounty;
import org.chskenya.covidapp.util.SessionManager;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PatientInfoAdapter extends PagerAdapter {
    private static final String TAG = "PatientInfoAdapter";
    SearchableSpinner facilitySpinner, genderSpinner, citizenshipSpinner, educationLevelSpinner,
            maritalStatusSpinner, investigationPlaceSpinner, CountySpinner, subcountySpinner,
            departmentSpinner;

    EditText etFName, etSecName, etSurname, etPhone, etPatientNationalID, etGuardianNationalID,
            etGuardianName, etOccupation, etnokName, etnokPhone;

    RadioButton aliveyesopt;
    County county;

    private PatientContact patientContact;

    LinearLayout childIdView, adultIdView;

    private EditText dob;

    private Facility[] facilities;
    private County[] counties;
    private final InitialData initialData;
    private final Context context;
    private final int[] layouts;
    private String stringDob;

    int years = 0;

    final Calendar myCalendar  = Calendar.getInstance();

    private View pageOne = null,
            pageTwo = null,
            pageThree = null;

    public PatientInfoAdapter(Context context, InitialData initialData, int[] layouts) {
        this.initialData = initialData;
        this.context = context;
        this.layouts = layouts;

        patientContact = SessionManager.INSTANCE.getPatientContact();
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

                    etFName = view.findViewById(R.id.etFName);
                    etSecName = view.findViewById(R.id.etSecName);
                    etSurname = view.findViewById(R.id.etSurname);
                    etPhone = view.findViewById(R.id.etPhone);

                    if (patientContact != null) {
                        etFName.setText(patientContact.getFirstName());
                        etSecName.setText(patientContact.getMiddleName());
                        etSurname.setText(patientContact.getSurname());
                        etPhone.setText(patientContact.getPhoneNumber());
                    }

                    etPatientNationalID = view.findViewById(R.id.etPatientNationalID);
                    etGuardianNationalID = view.findViewById(R.id.etGuardianNationalID);
                    etGuardianName = view.findViewById(R.id.etGuardianName);

                    childIdView = view.findViewById(R.id.childIdView);
                    adultIdView = view.findViewById(R.id.adultIdView);

                    dob = view.findViewById(R.id.dob);

                    DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(myCalendar);
                        years = getAge(myCalendar);

                        if (years < 18) {
                            adultIdView.setVisibility(View.GONE);
                            childIdView.setVisibility(View.VISIBLE);
                        } else {
                            childIdView.setVisibility(View.GONE);
                            adultIdView.setVisibility(View.VISIBLE);
                        }
                    };
                    dob.setOnClickListener(v -> new DatePickerDialog(context, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show());

                    List<String> gender = new ArrayList<>();
                    gender.add("Male");
                    gender.add("Female");

                    genderSpinner = view.findViewById(R.id.genderSpinner);
                    ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, gender);
                    genderSpinner.setAdapter(genderAdapter);

                    List<String> departments = new ArrayList<>();
                    departments.add("OPD");
                    departments.add("IPD");
                    departments.add("CCC");
                    departments.add("MCH");
                    departments.add("MOPC");

                    departmentSpinner = view.findViewById(R.id.departmentSpinner);
                    ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, departments);
                    departmentSpinner.setAdapter(departmentAdapter);

                } else view = pageOne;
                break;
            }
            case 1: {
                if (pageTwo == null) {
                    pageTwo = view;

                    etOccupation = view.findViewById(R.id.etOccupation);
                    aliveyesopt = view.findViewById(R.id.aliveyesopt);

                    String[] nationalities = initialData.getNationalities();
                    ArrayAdapter<String> nationalitiesSpinnerAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, nationalities);
                    citizenshipSpinner = view.findViewById(R.id.citizenshipSpinner);
                    citizenshipSpinner.setAdapter(nationalitiesSpinnerAdapter);

                    //level of education
                    List<String> schlevelcat = new ArrayList<>();
                    schlevelcat.add("No formal education");
                    schlevelcat.add("Primary");
                    schlevelcat.add("Secondary");
                    schlevelcat.add("Tertiary");

                    educationLevelSpinner = view.findViewById(R.id.educationLevelSpinner);
                    ArrayAdapter<String> EduLevelAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, schlevelcat);
                    educationLevelSpinner.setAdapter(EduLevelAdapter);

                    //marital status
                    List<String> maritalStatusList = new ArrayList<>();
                    maritalStatusList.add("Married");
                    maritalStatusList.add("Single");
                    maritalStatusList.add("Divorced");
                    maritalStatusList.add("Minor");

                    maritalStatusSpinner = view.findViewById(R.id.maritalStatusSpinner);
                    ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, maritalStatusList);
                    maritalStatusSpinner.setAdapter(maritalStatusAdapter);

                    if (years < 16) {
                        // TODO: 03-Aug-21 set minor as default for those less than 16 years
                    }

                } else view = pageTwo;
                break;
            }
            case 2: {
                if (pageThree == null) {
                    pageThree = view;

                    etnokName = view.findViewById(R.id.etnokName);
                    etnokPhone = view.findViewById(R.id.etnokPhone);

                    //level of education
                    List<String> investigationPlaceCat = new ArrayList<>();
                    investigationPlaceCat.add("Household");
                    investigationPlaceCat.add("Mass Testing");
                    investigationPlaceCat.add("Quarantine");
                    investigationPlaceCat.add("Health Facility");

                    investigationPlaceSpinner = view.findViewById(R.id.investigationPlaceSpinner);
                    ArrayAdapter<String> investigationPlaceAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, investigationPlaceCat);
                    investigationPlaceSpinner.setAdapter(investigationPlaceAdapter);

                    facilities = initialData.getFacilities();
                    ArrayAdapter<Facility> facilitySpinnerAdapter = new ArrayAdapter<>(context,
                            R.layout.spinner_layout, R.id.tvName, facilities);
                    facilitySpinner = view.findViewById(R.id.facility_spinner);
                    facilitySpinner.setAdapter(facilitySpinnerAdapter);

                    LinearLayout getHealthFacility = view.findViewById(R.id.getHealthFacility);
                    investigationPlaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 3) {
                                getHealthFacility.setVisibility(View.VISIBLE);
                            } else {
                                getHealthFacility.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    counties = initialData.getCounties();
                    ArrayAdapter<County> countySpinnerAdapter = new ArrayAdapter<>(context,
                            R.layout.spinner_layout, R.id.tvName, counties);
                    CountySpinner = view.findViewById(R.id.CountySpinner);
                    CountySpinner.setAdapter(countySpinnerAdapter);

                    subcountySpinner = view.findViewById(R.id.subcountySpinner);

                    CountySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            County county = counties[position];
                            ArrayAdapter<Subcounty> subcountySpinnerAdapter = new ArrayAdapter<>(context,
                                    R.layout.spinner_layout, R.id.tvName, county.getSubcounties());
                            subcountySpinner.setAdapter(subcountySpinnerAdapter);

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
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

    private void updateLabel(Calendar myCalendar) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        stringDob = sdf.format(myCalendar.getTime());
        dob.setText(stringDob);
    }

    public String getFirstName() {
        return etFName.getText().toString().trim();
    }

    public String getSecondName() {
        return etSecName.getText().toString().trim();
    }

    public String getSurname() {
        return etSurname.getText().toString().trim();
    }

    public String getGender() {
        return genderSpinner.getSelectedItem().toString();
    }

    public String getDepartment() {
        return departmentSpinner.getSelectedItem().toString();
    }

    public String getPhone() {
        return etPhone.getText().toString();
    }

    public String getDob() {
        return stringDob;
    }

    public String getNationalID() {
        return etPatientNationalID.getText().toString().trim();
    }

    public String getGuardianID() {
        return etGuardianNationalID.getText().toString().trim();
    }

    public String getGuardianName() {
        return etGuardianName.getText().toString().trim();
    }

    public String getCitizenship() {
        return citizenshipSpinner.getSelectedItem().toString();
    }

    public String getEducationLevel() {
        return educationLevelSpinner.getSelectedItem().toString();
    }

    public String getMaritalStatus() {
        return maritalStatusSpinner.getSelectedItem().toString();
    }

    public String getOccupation() {
        return etOccupation.getText().toString().trim();
    }

    public String getAlive() {
        boolean alive = aliveyesopt.isChecked();
        return alive ? "Yes" : "No";
    }

    public String getCaseLocation() {
        return investigationPlaceSpinner.getSelectedItem().toString();
    }

    public String getInvestigatingFacility() {
        Facility facility = facilities[facilitySpinner.getSelectedItemPosition()];
        return facility.getMflCode();
    }

    public int getCounty() {
        county = counties[CountySpinner.getSelectedItemPosition()];
        return county.getCode();
    }

    public int getSubCounty() {
        Subcounty[] subcounties = county.getSubcounties();
        Subcounty subcounty = subcounties[subcountySpinner.getSelectedItemPosition()];
        return subcounty.getId();
    }

    public String getNokName() {
        return etnokName.getText().toString();
    }

    public String getNokPhone() {
        return etnokPhone.getText().toString().trim();
    }
}
