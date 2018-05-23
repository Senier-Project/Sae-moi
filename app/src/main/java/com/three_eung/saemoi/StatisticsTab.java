package com.three_eung.saemoi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StatisticsTab extends CustomFragment implements View.OnClickListener, OnChartValueSelectedListener {
    private static final String TAG = StatisticsTab.class.getSimpleName();

    private View mView;
    private PieChart mChart;
    private ArrayList<HousekeepInfo> mHousekeepList = new ArrayList<>();
    private ArrayList<SavingInfo> mSavingList;
    private ArrayList<PieEntry> yValues;
    private volatile boolean isIncome = true;
    private Calendar calendar;
    private HashMap<String, Integer> mValueMap;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        StatisticsTab statisticsTab = new StatisticsTab();
        statisticsTab.setArguments(args);

        return statisticsTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_statistics, container, false);

        calendar = Calendar.getInstance();

        Button btn_income = (Button) mView.findViewById(R.id.statis_income);
        Button btn_outcome = (Button) mView.findViewById(R.id.statis_outcome);
        btn_income.setOnClickListener(this);
        btn_outcome.setOnClickListener(this);

        mChart = (PieChart) mView.findViewById(R.id.statis_chart);

        mChart.setUsePercentValues(false);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5,10,5,5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(false);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleRadius(61f);
        mChart.setClickable(false);

        yValues = new ArrayList<>();

        PieDataSet dataSet = new PieDataSet(yValues, "카테고리");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        mChart.setData(data);



        //mChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        updateData();

        return mView;
    }

    private void updateData() {
        yValues.clear();
        mValueMap = new HashMap<>();

        for(HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if(date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                String category = housekeepInfo.getCategory();

                if (housekeepInfo.getIsIncome() && isIncome) {
                    Log.e(TAG, "updateData: " + housekeepInfo.getIsIncome() + "  " + isIncome);
                    if(mValueMap.containsKey(category)) {
                        int value = mValueMap.get(category) + housekeepInfo.getValue();
                        mValueMap.put(category, value);
                    } else {
                        mValueMap.put(category, housekeepInfo.getValue());
                    }
                } else if(!housekeepInfo.getIsIncome() && !isIncome) {
                    Log.e(TAG, "updateData: " + housekeepInfo.getIsIncome() + "  " + isIncome);
                    if(mValueMap.containsKey(category)) {
                        int value = mValueMap.get(category) + housekeepInfo.getValue();
                        mValueMap.put(category, value);
                    } else {
                        mValueMap.put(category, housekeepInfo.getValue());
                    }
                }
            }
        }

        for(String key : mValueMap.keySet()) {
            yValues.add(new PieEntry(mValueMap.get(key), key));
        }

        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    @Override
    public void setHousekeepData(ArrayList<HousekeepInfo> housekeepData) {
        this.mHousekeepList = housekeepData;
    }

    @Override
    public void setSavingData(ArrayList<SavingInfo> savingData) {
        this.mSavingList = savingData;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mHousekeepList = events.getHkList();
        updateData();
    }

    @Override
    public void onStart() {
        super.onStart();

        mHousekeepList = ((InitApp)(getActivity().getApplication())).getHousekeepList();
        updateData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statis_income:
                isIncome = true;
                updateData();
                break;
            case R.id.statis_outcome:
                isIncome = false;
                updateData();
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
    }
}
