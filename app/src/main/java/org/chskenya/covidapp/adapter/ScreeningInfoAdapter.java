package org.chskenya.covidapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.chskenya.covidapp.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScreeningInfoAdapter extends PagerAdapter {
    private static final String TAG = "ScreeningInfoAdapter";

    private SearchableSpinner chillsSpinner, weaknessSpinner, coughSpinner, soreThroatSpinner,
            runnyNoseSpinner, weightLossSpinner, nightSweatsSpinner, tasteSpinner, smellSpinner,
            breathingSpinner, diarrhoeaSpinner, headacheSpinner, irritabilitySpinner, nauseaSpinner,
            breathShortnessSpinner, painSpinner;
    private final Context context;
    private final int[] layouts;

    private View pageOne = null,
            pageTwo = null,
            pageThree = null;

    private final List<String> optionList;

    public ScreeningInfoAdapter(Context context, int[] layouts) {
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

                    chillsSpinner = view.findViewById(R.id.chillsSpinner);
                    ArrayAdapter<String> chillsAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    chillsSpinner.setAdapter(chillsAdapter);

                    weaknessSpinner = view.findViewById(R.id.weaknessSpinner);
                    ArrayAdapter<String> weaknessAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    weaknessSpinner.setAdapter(weaknessAdapter);

                    coughSpinner = view.findViewById(R.id.coughSpinner);
                    ArrayAdapter<String> coughAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    coughSpinner.setAdapter(coughAdapter);

                    soreThroatSpinner = view.findViewById(R.id.soreThroatSpinner);
                    ArrayAdapter<String> soreThroatAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    soreThroatSpinner.setAdapter(soreThroatAdapter);

                    runnyNoseSpinner = view.findViewById(R.id.runnyNoseSpinner);
                    ArrayAdapter<String> runnyNoseAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    runnyNoseSpinner.setAdapter(runnyNoseAdapter);

                } else view = pageOne;
                break;
            }
            case 1: {
                if (pageTwo == null) {
                    pageTwo = view;

                    weightLossSpinner = view.findViewById(R.id.weightLossSpinner);
                    ArrayAdapter<String> weightLossAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    weightLossSpinner.setAdapter(weightLossAdapter);

                    nightSweatsSpinner = view.findViewById(R.id.nightSweatsSpinner);
                    ArrayAdapter<String> nightSweatsAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    nightSweatsSpinner.setAdapter(nightSweatsAdapter);

                    tasteSpinner = view.findViewById(R.id.tasteSpinner);
                    ArrayAdapter<String> tasteAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    tasteSpinner.setAdapter(tasteAdapter);

                    smellSpinner = view.findViewById(R.id.smellSpinner);
                    ArrayAdapter<String> smellAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    smellSpinner.setAdapter(smellAdapter);

                    breathingSpinner = view.findViewById(R.id.breathingSpinner);
                    ArrayAdapter<String> breathingAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    breathingSpinner.setAdapter(breathingAdapter);

                } else view = pageTwo;
                break;
            }
            case 2: {
                if (pageThree == null) {
                    pageThree = view;

                    diarrhoeaSpinner = view.findViewById(R.id.diarrhoeaSpinner);
                    ArrayAdapter<String> diarrhoeaAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    diarrhoeaSpinner.setAdapter(diarrhoeaAdapter);

                    headacheSpinner = view.findViewById(R.id.headacheSpinner);
                    ArrayAdapter<String> headacheAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    headacheSpinner.setAdapter(headacheAdapter);

                    irritabilitySpinner = view.findViewById(R.id.irritabilitySpinner);
                    ArrayAdapter<String> irritabilityAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    irritabilitySpinner.setAdapter(irritabilityAdapter);

                    nauseaSpinner = view.findViewById(R.id.nauseaSpinner);
                    ArrayAdapter<String> nauseaAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    nauseaSpinner.setAdapter(nauseaAdapter);

                    breathShortnessSpinner = view.findViewById(R.id.breathShortnessSpinner);
                    ArrayAdapter<String> breathShortnessAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    breathShortnessSpinner.setAdapter(breathShortnessAdapter);

                    painSpinner = view.findViewById(R.id.painSpinner);
                    ArrayAdapter<String> painAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, optionList);
                    painSpinner.setAdapter(painAdapter);


                } else view = pageThree;
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

    public String getFeverHistory() {
        return chillsSpinner.getSelectedItem().toString();
    }

    public String getWeaknessHistory() {
        return weaknessSpinner.getSelectedItem().toString();
    }

    public String getCoughHistory() {
        return coughSpinner.getSelectedItem().toString();
    }

    public String getSoreThroatHistory() {
        return soreThroatSpinner.getSelectedItem().toString();
    }

    public String getRunnyNoseHistory() {
        return runnyNoseSpinner.getSelectedItem().toString();
    }

    public String getWeightLossHistory() {
        return weightLossSpinner.getSelectedItem().toString();
    }

    public String getNightSweatsHistory() {
        return nightSweatsSpinner.getSelectedItem().toString();
    }

    public String getTasteLossHistory() {
        return tasteSpinner.getSelectedItem().toString();
    }

    public String getSmellLossHistory() {
        return smellSpinner.getSelectedItem().toString();
    }

    public String getBreathingHistory() {
        return breathingSpinner.getSelectedItem().toString();
    }

    public String getDiarrhoeaHistory() {
        return diarrhoeaSpinner.getSelectedItem().toString();
    }

    public String getHeadacheHistory() {
        return headacheSpinner.getSelectedItem().toString();
    }

    public String getIrritabilityHistory() {
        return irritabilitySpinner.getSelectedItem().toString();
    }

    public String getNauseaHistory() {
        return nauseaSpinner.getSelectedItem().toString();
    }

    public String getBreathShortnessHistory() {
        return breathShortnessSpinner.getSelectedItem().toString();
    }

    public String getPainHistory() {
        return painSpinner.getSelectedItem().toString();
    }
}
