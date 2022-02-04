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

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.model.County;
import org.chskenya.covidapp.model.InitialData;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PatientHistoryAdapter extends PagerAdapter {
    private static final String TAG = "PatientHistoryAdapter";

    private SearchableSpinner travelledSpinner, contactSpinner, countySpinner, vaccinatedSpinner;
    private LinearLayout hasTravelledLayout, vaccinedetlayout;
    private EditText fdosedate, sdosedate;

    private final Context context;
    private final int[] layouts;
    private County[] counties;
    private final InitialData initialData;

    private View pageOne = null,
            pageTwo = null;

    private final List<String> optionList;

    final Calendar myCalendar  = Calendar.getInstance();
    final Calendar myCalendar2  = Calendar.getInstance();

    public PatientHistoryAdapter(Context context, InitialData initialData, int[] layouts) {
        this.initialData = initialData;
        this.context = context;
        this.layouts = layouts;

        optionList = new ArrayList<>();
        optionList.add("Yes");
        optionList.add("No");

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

                    travelledSpinner = view.findViewById(R.id.travelledSpinner);
                    ArrayAdapter<String> travelledAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    travelledSpinner.setAdapter(travelledAdapter);

                    hasTravelledLayout = view.findViewById(R.id.hasTravelledLayout);

                    travelledSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String option = optionList.get(position);
                            if (option.equals("Yes")) {
                                hasTravelledLayout.setVisibility(View.VISIBLE);
                            } else {
                                hasTravelledLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    counties = initialData.getCounties();
                    ArrayAdapter<County> countySpinnerAdapter = new ArrayAdapter<>(context,
                            R.layout.spinner_layout, R.id.tvName, counties);
                    countySpinner = view.findViewById(R.id.countySpinner);
                    countySpinner.setAdapter(countySpinnerAdapter);

                    contactSpinner = view.findViewById(R.id.contactSpinner);
                    ArrayAdapter<String> contactAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    contactSpinner.setAdapter(contactAdapter);

                } else view = pageOne;
                break;
            }
            case 1: {
                if (pageTwo == null) {
                    pageTwo = view;

                    vaccinatedSpinner = view.findViewById(R.id.vaccinatedSpinner);
                    ArrayAdapter<String> vaccinatedAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    vaccinatedSpinner.setAdapter(vaccinatedAdapter);

                    vaccinedetlayout = view.findViewById(R.id.vaccinedetlayout);

                    vaccinatedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String option = optionList.get(position);
                            if (option.equals("Yes")) {
                                vaccinedetlayout.setVisibility(View.VISIBLE);
                            } else {
                                vaccinedetlayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    fdosedate = view.findViewById(R.id.fdosedate);

                    DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(myCalendar, fdosedate);
                    };
                    fdosedate.setOnClickListener(v -> new DatePickerDialog(context, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show());

                    sdosedate = view.findViewById(R.id.sdosedate);

                    DatePickerDialog.OnDateSetListener date2 = (view1, year, monthOfYear, dayOfMonth) -> {
                        myCalendar2.set(Calendar.YEAR, year);
                        myCalendar2.set(Calendar.MONTH, monthOfYear);
                        myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(myCalendar2, sdosedate);
                    };
                    sdosedate.setOnClickListener(v -> new DatePickerDialog(context, date2, myCalendar2
                            .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                            myCalendar2.get(Calendar.DAY_OF_MONTH)).show());

                } else view = pageTwo;
                break;
            }
        }
        container.addView(view);
        return view;
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

    private void updateLabel(Calendar myCalendar, EditText editText) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

}
