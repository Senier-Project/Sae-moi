package com.three_eung.saemoi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeTab extends Fragment {
    private Activity root;
    private View mView;
    public static HomeTab newInstance() {
        Bundle args = new Bundle();

        HomeTab homeTab = new HomeTab();
        homeTab.setArguments(args);

        return homeTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home,container,false);

        

        return mView;
    }
}
