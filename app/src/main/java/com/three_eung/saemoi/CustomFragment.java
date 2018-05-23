package com.three_eung.saemoi;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

public abstract class CustomFragment extends Fragment {
    public abstract void setHousekeepData(ArrayList<HousekeepInfo> housekeepData);
    public abstract void setSavingData(ArrayList<SavingInfo> savingData);
}