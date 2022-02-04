package org.chskenya.covidapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PatientManagementInfoAdapter extends PagerAdapter {
    private static final String TAG = "PatientManInfoAdapter";

    private final Context context;
    private final int[] layouts;

    private View pageOne = null,
            pageTwo = null,
            pageThree = null;

    private final List<String> optionList;

    public PatientManagementInfoAdapter(Context context, int[] layouts) {
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
