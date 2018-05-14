package com.three_eung.saemoi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by CH on 2018-02-18.
 */

public class CalendarTab extends Fragment implements View.OnClickListener {
    static final String TAG = MainActivity.class.getName();

    private Activity root;
    private View mView;
    private Fragment mFragment;

    private TextView currentDate;
    private GridView gridView;

    private Calendar calendar;
    private String today;

    private ArrayList<DayInfo> calendarList = new ArrayList<DayInfo>();
    private HashMap<String, HashMap<String, DayInfo>> monthList = new HashMap<String, HashMap<String, DayInfo>>();

    private enum DayOfTheWeek {
        SUN, MON, TUE, WED, THU, FRI, SAT
    }

    private DatabaseReference mRef;
    private CalendarAdapter mAdapter;
    private ChildEventListener mChildListener;

    public static CalendarTab newInstance() {
        Bundle args = new Bundle();

        CalendarTab calendarTab = new CalendarTab();
        calendarTab.setArguments(args);

        return calendarTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false);

        currentDate = (TextView)mView.findViewById(R.id.monthText);
        gridView = (GridView)mView.findViewById(R.id.calGrid);
        Button prevButton = (Button)mView.findViewById(R.id.btnPrev);
        Button nextButton = (Button)mView.findViewById(R.id.btnNext);

        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

/*
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EventInfo eventInfo = dataSnapshot.getValue(EventInfo.class);
                eventInfo.setId(dataSnapshot.getKey());

                if(monthList.containsKey(eventInfo.getYearmonth())) {
                    if(monthList.get(eventInfo.getYearmonth()).containsKey(eventInfo.getDay())) {
                        monthList.get(eventInfo.getYearmonth()).get(eventInfo.getDay()).updateItem(eventInfo);
                    } else {
                        DayInfo dayInfo = new DayInfo();
                        dayInfo.setDay(eventInfo.getDay());
                        dayInfo.updateItem(eventInfo);
                        monthList.get(eventInfo.getYearmonth()).put(eventInfo.getDay(), dayInfo);
                    }
                } else {
                    DayInfo dayInfo = new DayInfo();
                    dayInfo.setDay(eventInfo.getDay());
                    dayInfo.updateItem(eventInfo);
                    HashMap<String, DayInfo> dayList = new HashMap<String, DayInfo>();
                    dayList.put(eventInfo.getDay(), dayInfo);
                    monthList.put(eventInfo.getYearmonth(), dayList);
                }

                if(mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                EventInfo eventInfo = dataSnapshot.getValue(EventInfo.class);
                eventInfo.setId(dataSnapshot.getKey());

                if(monthList.containsKey(eventInfo.getYearmonth())) {
                    if(monthList.get(eventInfo.getYearmonth()).containsKey(eventInfo.getDay())) {
                        monthList.get(eventInfo.getYearmonth()).get(eventInfo.getDay()).removeItem(eventInfo);
                    }
                }

                if(mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/


        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        root = getActivity();
        mFragment = this;
        mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("inout");

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EventInfo eventInfo = dataSnapshot.getValue(EventInfo.class);
                eventInfo.setId(dataSnapshot.getKey());

                if(monthList.containsKey(eventInfo.getYearmonth())) {
                    if(monthList.get(eventInfo.getYearmonth()).containsKey(eventInfo.getDay())) {
                        monthList.get(eventInfo.getYearmonth()).get(eventInfo.getDay()).updateItem(eventInfo);
                    } else {
                        DayInfo dayInfo = new DayInfo();
                        dayInfo.setDay(eventInfo.getDay());
                        dayInfo.updateItem(eventInfo);
                        monthList.get(eventInfo.getYearmonth()).put(eventInfo.getDay(), dayInfo);
                    }
                } else {
                    DayInfo dayInfo = new DayInfo();
                    dayInfo.setDay(eventInfo.getDay());
                    dayInfo.updateItem(eventInfo);
                    HashMap<String, DayInfo> dayList = new HashMap<String, DayInfo>();
                    dayList.put(eventInfo.getDay(), dayInfo);
                    monthList.put(eventInfo.getYearmonth(), dayList);
                }

                if(mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                EventInfo eventInfo = dataSnapshot.getValue(EventInfo.class);
                eventInfo.setId(dataSnapshot.getKey());

                if(monthList.containsKey(eventInfo.getYearmonth())) {
                    if(monthList.get(eventInfo.getYearmonth()).containsKey(eventInfo.getDay())) {
                        monthList.get(eventInfo.getYearmonth()).get(eventInfo.getDay()).removeItem(eventInfo);
                    }
                }

                if(mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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

        mRef.addChildEventListener(mChildListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshAdapter(0);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRef.removeEventListener(mChildListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(mChildListener);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnPrev:
                refreshAdapter(-1);
                break;
            case R.id.btnNext:
                refreshAdapter(1);
        }
    }

    public void refreshAdapter(int value) {
        HashMap<String, DayInfo> currentList = null;

        calendarList = new ArrayList<DayInfo>();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, value);

        String yearmonth = String.valueOf(calendar.get(Calendar.YEAR))+""+String.valueOf(calendar.get(Calendar.MONTH) + 1);

        if(monthList.containsKey(yearmonth)) {
            currentList = (HashMap<String, DayInfo>)monthList.get(yearmonth);
        }

        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        for(int i=1; i<dayNum; i++) {
            DayInfo dayInfo = new DayInfo();
            dayInfo.setDay("");
            calendarList.add(dayInfo);
        }

        for(int i=0; i<calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            boolean flag = true;
            if(currentList != null) {
                if (currentList.containsKey(String.valueOf(i + 1))) {
                    calendarList.add(currentList.get(String.valueOf(i + 1)));
                    flag = false;
                }
            }
            if(flag) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.setDay(String.valueOf(i + 1));
                calendarList.add(dayInfo);
            }
        }

        currentDate.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월");

        mAdapter = new CalendarAdapter();
        gridView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    public DatabaseReference getReference() {
        return mRef;
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
            inflater = (LayoutInflater)getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_calendar, viewGroup, false);

                viewHolder.day = (TextView)convertView.findViewById(R.id.dayTv);
                viewHolder.income = (TextView)convertView.findViewById(R.id.incomeTv);
                viewHolder.outcome = (TextView)convertView.findViewById(R.id.outcomeTv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            DayInfo dayInfo = calendarList.get(position);
            viewHolder.day.setText(dayInfo.getDay());
            if(dayInfo.getDay().equals("")) {
                convertView.setClickable(false);
            }

            if(position % 7 == DayOfTheWeek.SAT.ordinal()) {
                viewHolder.day.setTextColor(Color.parseColor("#2E64FE"));
            } else if(position % 7 == DayOfTheWeek.SUN.ordinal()) {
                viewHolder.day.setTextColor(Color.RED);
            }

            if(dayInfo.getEventList() != null) {
                viewHolder.income.setText(String.valueOf(dayInfo.getIncome()));
                viewHolder.outcome.setText(String.valueOf(dayInfo.getOutcome()));
            }

            if(today.equals(calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH) + 1)+""+dayInfo.getDay())) {
                viewHolder.day.setText("★ "+dayInfo.getDay());
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
            ViewHolder viewHolder = (ViewHolder)v.getTag();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay;
            String dayStr = viewHolder.day.getText().toString();
            if(dayStr.contains("★")) {
                mDay = Integer.parseInt(dayStr.substring(2, dayStr.length()));
            } else {
                mDay = Integer.parseInt(dayStr);
            }

            Bundle data = new Bundle();
            data.putInt("year", mYear);
            data.putInt("month", mMonth);
            data.putInt("day", mDay);
            Intent intent = new Intent(root, DailyListActivity.class);
            intent.putExtra("year", mYear);
            intent.putExtra("month", mMonth);
            intent.putExtra("day", mDay);
            startActivity(intent);

            /*
            InputDialog inputDialog = InputDialog.newInstance();
            inputDialog.setArguments(data);
            inputDialog.setTargetFragment(mFragment, 0);
            inputDialog.show(getFragmentManager(), TAG);*/
        }
    }
}