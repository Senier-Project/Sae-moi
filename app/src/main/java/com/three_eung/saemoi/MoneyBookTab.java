package com.three_eung.saemoi;

import android.app.Activity;
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

/**
 * Created by CH on 2018-02-18.
 */

public class MoneyBookTab extends Fragment {
    Activity root;
    View mView;
    ViewPager mPager;
    TabLayout mTab;

    public static MoneyBookTab newInstance() {
        Bundle args = new Bundle();

        MoneyBookTab moneyBookTab = new MoneyBookTab();
        moneyBookTab.setArguments(args);

        return moneyBookTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_moneybook, container, false);
        root = getActivity();

        mPager = (ViewPager)mView.findViewById(R.id.moneyPager);
        mTab = (TabLayout)mView.findViewById(R.id.moneyTab);

        mPager.setAdapter(new MoneyPagerAdapter(getChildFragmentManager()));
        mTab.setupWithViewPager(mPager);

        return mView;
    }

    class MoneyPagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_NUMBER = 3;

        public MoneyPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return CalendarTab.newInstance();
                case 1:
                    return CalendarTab.newInstance();
                case 2:
                    return CalendarTab.newInstance();
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
