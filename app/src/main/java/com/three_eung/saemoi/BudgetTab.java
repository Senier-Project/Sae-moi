package com.three_eung.saemoi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BudgetTab extends CustomFragment {
    private static final String TAG = BudgetTab.class.getSimpleName();

    private View mView;
    private ArrayList<HousekeepInfo> mHousekeepList;
    private ArrayList<SavingInfo> mSavingList;
    private ArrayList<BudgetInfo> items;

    private DatabaseReference infoRef;
    private HashMap<String, Integer> mBudget;
    private int totalBudget, usedBudget;
    private TextView totalTv, availTv, remainTv, todayTv;
    private Calendar calendar;
    private BudgetAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private ValueEventListener mListener;
    private ImageView budgetImage;

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        BudgetTab budgetTab = new BudgetTab();
        budgetTab.setArguments(args);

        return budgetTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_budget, container, false);

        //하루에 쓸 돈
        todayTv = mView.findViewById(R.id.todayBudget);
        TextView userName = mView.findViewById(R.id.budget_userName);

        //테이블에서 총예산 : totalBudget | 남은예산 : remainBudget | 하루사용가능 예산 : availBuget
        totalTv = (TextView) mView.findViewById(R.id.totalBudget);
        remainTv = (TextView) mView.findViewById(R.id.remainBudget);
        availTv = (TextView) mView.findViewById(R.id.availBuget);
        budgetImage = (ImageView) mView.findViewById(R.id.budget_image);

        userName.setText(InitApp.sUser.getDisplayName());


        //오늘 날짜 sdf.format(cal.getTime()));

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.budget_list);


//        showBudget();


        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance();
        items = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayout.VERTICAL);

        mAdapter = new BudgetAdapter(this.getContext(), items);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setHousekeepData(ArrayList<HousekeepInfo> housekeepData) {
        this.mHousekeepList = housekeepData;
    }

    @Override
    public void setSavingData(ArrayList<SavingInfo> savingData) {
        this.mSavingList = savingData;
    }

    private void updateData() {
        mBudget = (HashMap<String, Integer>)(((InitApp) (getActivity().getApplication())).getBudget());
        items.clear();
        if (mBudget.containsKey("total"))
            totalBudget = mBudget.get("total");
        else
            totalBudget = 0;
        usedBudget = 0;

        for (HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar date = Calendar.getInstance();
            date.setTime(Utils.stringToDate(housekeepInfo.getDate()));

            if (housekeepInfo.getIsBudget()) {
                if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    int value = housekeepInfo.getValue();
                    String category = housekeepInfo.getCategory();
                    boolean isIncome = housekeepInfo.getIsIncome();
                    boolean isExist = true;

                    if (!isIncome) {
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getCategory().equals(category)) {
                                int temp = items.get(i).getValue() + value;

                                items.get(i).setValue(temp);
                                isExist = false;
                                break;
                            }
                        }

                        if (isExist) {
                            BudgetInfo budgetInfo = new BudgetInfo(category, value);

                            items.add(budgetInfo);
                        }
                    }

                    if (isIncome) {
                        usedBudget -= value;
                    } else {
                        usedBudget += value;
                    }

                }
            }
        }

        BudgetInfo budgetInfo = new BudgetInfo("총 예산", usedBudget);
        items.add(0, budgetInfo);

        remainTv.setText(Utils.toCurrencyString(totalBudget - usedBudget));
        int availBudget = (int) ((double) (totalBudget - usedBudget) / (double) (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DATE) + 1));
        Log.e(TAG, "updateData: " + ((double) totalBudget / (double) calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) + "   " + (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DATE) + 1));
        availTv.setText(Utils.toCurrencyString(availBudget));
        todayTv.setText(Utils.toCurrencyFormat(availBudget));
        totalTv.setText(Utils.toCurrencyString(totalBudget));

        Log.e(TAG, "updateData: " + totalBudget + "  " + availBudget + "  " + usedBudget);

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mHousekeepList = events.getHkList();
        updateData();
    }

    @Override
    public void onStart() {
        super.onStart();

        mHousekeepList = ((InitApp) (getActivity().getApplication())).getHousekeepList();
        updateData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<BudgetInfo> items;

        public BudgetAdapter(Context context, ArrayList<BudgetInfo> items) {
            this.mContext = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final int itempos = position;

            String category = items.get(itempos).getCategory();
            int total = items.get(itempos).getValue();

            Log.e(TAG, "onBindViewHolder: " + category + "  " + itempos);

            viewHolder.nameTv.setText(category);
            viewHolder.textTv.setText(Utils.toCurrencyString(total));

            ViewGroup.LayoutParams underParams = viewHolder.underV.getLayoutParams();
            underParams.width = viewHolder.textTv.getWidth();
            viewHolder.underV.setLayoutParams(underParams);

            ViewGroup.LayoutParams blankParams = viewHolder.blankV.getLayoutParams();
            ViewGroup.LayoutParams barParams = viewHolder.barV.getLayoutParams();

            int value = items.get(itempos).getValue();

            if (value < 0) {
                value = 0;
            }

            Log.e(TAG, "onBindViewHolder: " + getActivity().getWindowManager().getDefaultDisplay().getWidth() + "  " + getActivity().getWindowManager().getDefaultDisplay().getHeight());
            double ratio = 1.0 - ((double) value / (double) totalBudget);
            blankParams.width = (int) ((double) (getActivity().getWindowManager().getDefaultDisplay().getWidth() - Utils.dpToPx(getContext(), 50)) * Math.abs(ratio)) + Utils.dpToPx(getContext(), 5);
            Log.e(TAG, "onBindViewHolder: " + ratio + "  " + blankParams.width + "  " + viewHolder.barV.getWidth() + "  " + barParams.width);
            viewHolder.blankV.setLayoutParams(blankParams);

            if (ratio >= 0.8) {
                viewHolder.barV.setBackgroundResource(R.color.income);
                viewHolder.imgV.setImageResource(R.drawable.sae_money);
                if (position == 0)
                    budgetImage.setImageResource(R.drawable.sae_money);
            } else if (ratio >= 0.6) {
                viewHolder.barV.setBackgroundResource(R.color.green);
                viewHolder.imgV.setImageResource(R.drawable.sae_heart);
                if (position == 0)
                    budgetImage.setImageResource(R.drawable.sae_heart);
            } else if (ratio >= 0.4) {
                viewHolder.barV.setBackgroundResource(R.color.yellow);
                viewHolder.imgV.setImageResource(R.drawable.sae_lovely);
                if (position == 0)
                    budgetImage.setImageResource(R.drawable.sae_lovely);
            } else if (ratio >= 0.2) {
                viewHolder.barV.setBackgroundResource(R.color.red);
                viewHolder.imgV.setImageResource(R.drawable.sae_sweat);
                if (position == 0)
                    budgetImage.setImageResource(R.drawable.sae_sweat);
            } else if (ratio >= 0) {
                viewHolder.barV.setBackgroundResource(R.color.outcome);
                viewHolder.imgV.setImageResource(R.drawable.sae_angry);
                if (position == 0)
                    budgetImage.setImageResource(R.drawable.sae_angry);
            }
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            else
                return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView nameTv, textTv;
            public View blankV, underV;
            public ImageView barV, imgV;

            public ViewHolder(View view) {
                super(view);

                nameTv = (TextView) view.findViewById(R.id.item_budget_name);
                textTv = (TextView) view.findViewById(R.id.item_budget_text);
                underV = (View) view.findViewById(R.id.item_budget_under);
                blankV = (View) view.findViewById(R.id.item_budget_view);
                barV = (ImageView) view.findViewById(R.id.item_budget_bar);
                imgV = (ImageView) view.findViewById(R.id.item_budget_img);
            }
        }
    }
}
