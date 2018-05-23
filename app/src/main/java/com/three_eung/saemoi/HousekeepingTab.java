package com.three_eung.saemoi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by CH on 2018-02-18.
 */

public class HousekeepingTab extends CustomFragment {
    private static final String TAG = HousekeepingTab.class.getSimpleName();

    private View mView;
    private ViewPager mPager;
    private TabLayout mTab;
    private HousekeepPagerAdapter mAdapter;
    private ArrayList<HousekeepInfo> mHousekeepList;
    private ArrayList<SavingInfo> mSavingList;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        HousekeepingTab housekeepingTab = new HousekeepingTab();
        housekeepingTab.setArguments(args);

        return housekeepingTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_housekeep, container, false);

        mPager = (ViewPager)mView.findViewById(R.id.housekeep_pager);
        mTab = (TabLayout)mView.findViewById(R.id.housekeep_tab);

        mAdapter = new HousekeepPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(1);
        mTab.setupWithViewPager(mPager);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setHousekeepData(ArrayList<HousekeepInfo> housekeepData) {
        this.mHousekeepList = housekeepData;
    }

    @Override
    public void setSavingData(ArrayList<SavingInfo> savingData) {
        this.mSavingList = savingData;
    }

    class HousekeepPagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_NUMBER = 3;

        public HousekeepPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return BudgetTab.newInstance();
                case 1:
                    return CalendarTab.newInstance(((MainActivity)getActivity()).getHousekeepList());
                case 2:
                    return StatisticsTab.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return PAGE_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "예산";
                case 1:
                    return "달력";
                case 2:
                    return "통계";
                default:
                    return null;
            }
        }
    }
}
