package com.three_eung.saemoi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by CH on 2018-02-18.
 */

public class CalendarTab extends CustomFragment implements View.OnClickListener {
    private static final String TAG = CalendarTab.class.getSimpleName();

    private Activity root;
    private View mView;
    private Fragment mFragment;

    private TextView currentDate;
    private GridView gridView;

    private Date date;
    private Calendar calendar;
    private String today;

    private ArrayList<DayInfo> calendarList = new ArrayList<DayInfo>();
    private HashMap<String, HashMap<String, DayInfo>> monthList = new HashMap<String, HashMap<String, DayInfo>>();

    private enum DayOfTheWeek {
        SUN, MON, TUE, WED, THU, FRI, SAT
    }

    private CalendarAdapter mAdapter;
    private ArrayList<HousekeepInfo> mHousekeepList;

    public static Fragment newInstance(ArrayList<HousekeepInfo> mHousekeepList) {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        CalendarTab calendarTab = new CalendarTab();
        calendarTab.setArguments(args);
        calendarTab.setHousekeepData(mHousekeepList);

        return calendarTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false);

        currentDate = (TextView) mView.findViewById(R.id.calendar_month);
        gridView = (GridView) mView.findViewById(R.id.calendar_calGrid);
        Button prevButton = (Button) mView.findViewById(R.id.calendar_btnPrev);
        Button nextButton = (Button) mView.findViewById(R.id.calendar_btnNext);

        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        StringBuffer buffer = new StringBuffer();
        buffer.append(currentYear);
        buffer.append(currentMonth);
        buffer.append(calendar.get(Calendar.DATE));
        today = buffer.toString();
    }

    @Override
    public void onStart() {
        super.onStart();

        mHousekeepList = ((InitApp)(getActivity().getApplication())).getHousekeepList();
        EventBus.getDefault().register(this);
        refreshAdapter(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_btnPrev:
                refreshAdapter(-1);
                break;
            case R.id.calendar_btnNext:
                refreshAdapter(1);
        }
    }

    private ArrayList<DayInfo> setList(Calendar currentMonth) {
        ArrayList<DayInfo> currentList = new ArrayList<>();
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < dayNum; i++) {
            DayInfo dayInfo = new DayInfo();
            currentList.add(dayInfo);
        }

        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            DayInfo dayInfo = new DayInfo(i + 1);
            currentList.add(dayInfo);
        }

        for (HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if (date != null) {
                if (date.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) && date.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)) {
                    int idx = dayNum + date.get(Calendar.DATE) - 2;

                    if (housekeepInfo.getIsIncome()) {
                        int newValue = currentList.get(idx).getIncome() + housekeepInfo.getValue();
                        currentList.get(idx).setIncome(newValue);
                    } else {
                        int newValue = currentList.get(idx).getExpend() + housekeepInfo.getValue();
                        currentList.get(idx).setExpend(newValue);
                    }
                }
            }
        }

        return currentList;
    }

    public void refreshAdapter(int value) {
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, value);

        calendarList = setList(calendar);
        /*
        String yearmonth = String.valueOf(calendar.get(Calendar.YEAR)) + "" + String.valueOf(calendar.get(Calendar.MONTH) + 1);

        if (monthList.containsKey(yearmonth)) {
            currentList = (HashMap<String, DayInfo>) monthList.get(yearmonth);
        }

        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);


        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            boolean flag = true;
            if (currentList != null) {
                if (currentList.containsKey(String.valueOf(i + 1))) {
                    calendarList.add(currentList.get(String.valueOf(i + 1)));
                    flag = false;
                }
            }
            if (flag) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.setDay(i + 1);
                calendarList.add(dayInfo);
            }
        }
        */

        currentDate.setText(Utils.toYearMonth(calendar));

        mAdapter = new CalendarAdapter();
        gridView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setHousekeepData(ArrayList<HousekeepInfo> housekeepData) {
        this.mHousekeepList = housekeepData;
    }

    @Override
    public void setSavingData(ArrayList<SavingInfo> savingData) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mHousekeepList = events.getHkList();

        refreshAdapter(0);
    }

    public class CalendarAdapter extends BaseAdapter implements View.OnClickListener {
        private LayoutInflater inflater;

        private class ViewHolder {
            public TextView day;
            public TextView income;
            public TextView outcome;
            public String date;
        }

        public CalendarAdapter() {
            inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_calendar, viewGroup, false);

                viewHolder.day = (TextView) convertView.findViewById(R.id.dayTv);
                viewHolder.income = (TextView) convertView.findViewById(R.id.incomeTv);
                viewHolder.outcome = (TextView) convertView.findViewById(R.id.outcomeTv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            DayInfo dayInfo = calendarList.get(position);


            if (dayInfo.getDay() == 0) {
                viewHolder.day.setText("");
                convertView.setClickable(false);
            } else {
                viewHolder.day.setText(String.valueOf(dayInfo.getDay()));
                if (dayInfo.getIncome() != 0 || dayInfo.getExpend() != 0) {
                    viewHolder.income.append(String.valueOf(dayInfo.getIncome()));
                    viewHolder.outcome.append(String.valueOf(dayInfo.getExpend()));
                    viewHolder.income.setVisibility(View.VISIBLE);
                    viewHolder.outcome.setVisibility(View.VISIBLE);
                }
            }

            if (position % 7 == DayOfTheWeek.SAT.ordinal()) {
                viewHolder.day.setTextColor(Color.BLUE);
            } else if (position % 7 == DayOfTheWeek.SUN.ordinal()) {
                viewHolder.day.setTextColor(Color.RED);
            }

            if (today.equals(calendar.get(Calendar.YEAR) + "" + (calendar.get(Calendar.MONTH) + 1) + "" + dayInfo.getDay())) {
                viewHolder.day.setText("★ " + dayInfo.getDay());
            }

            convertView.setOnClickListener(this);

            return convertView;
        }

        @Override
        public int getCount() {
            return calendarList.size();
        }

        @Override
        public Object getItem(int position) {
            return calendarList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();

            int mDay;

            String dayStr = viewHolder.day.getText().toString();
            if (dayStr.contains("★")) {
                mDay = Integer.parseInt(dayStr.substring(2, dayStr.length()));
            } else {
                mDay = Integer.parseInt(dayStr);
            }

            Calendar now = Calendar.getInstance();
            now.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), mDay);

            Intent intent = new Intent(getActivity(), DailyListActivity.class);
            intent.putExtra("date", now.getTimeInMillis());
            startActivity(intent);

            /*
            InputDialog inputDialog = InputDialog.newInstance();
            inputDialog.setArguments(data);
            inputDialog.setTargetFragment(mFragment, 0);
            inputDialog.show(getFragmentManager(), TAG);*/
        }
    }
}