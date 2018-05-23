package com.three_eung.saemoi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.three_eung.saemoi.databinding.ActivityDailylistBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by CH on 2018-03-19.
 */

public class DailyListActivity extends AppCompatActivity implements View.OnClickListener {
    private DailyListAdapter mAdapter;
    private ActivityDailylistBinding mBinding;

    private LinearLayoutManager layoutManager;
    private ArrayList<HousekeepInfo> mHousekeepList = new ArrayList<>();
    private ArrayList<HousekeepInfo> items = new ArrayList<>();

    private Calendar now;

    private DatabaseReference mRef;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = (ActivityDailylistBinding) DataBindingUtil.setContentView(this, R.layout.activity_dailylist);

        now = Calendar.getInstance();

        Intent intent = getIntent();
        now.setTimeInMillis(intent.getLongExtra("date", 0));

        setSupportActionBar(mBinding.listToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(now.get(Calendar.YEAR)+"년 "+(now.get(Calendar.MONTH)+1)+"월 "+now.get(Calendar.DATE) + "일");

        mHousekeepList = ((InitApp)getApplication()).getHousekeepList();
        setList();

        mAdapter = new DailyListAdapter(this, items, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = mBinding.moneylistView.getChildAdapterPosition(v);

                HousekeepInfo housekeepInfo = items.get(position);

                modifyItem(housekeepInfo);

                return true;
            }
        });

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mBinding.moneylistView.setHasFixedSize(true);
        mBinding.moneylistView.setLayoutManager(layoutManager);
        mBinding.moneylistView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mBinding.moneylistView.addItemDecoration(dividerItemDecoration);

        mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("housekeeping");

        EventBus.getDefault().register(this);

        mBinding.dailyFab.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dailyFab:
                FragmentManager fm = getSupportFragmentManager();
                Bundle data = new Bundle();
                data.putLong("date", now.getTimeInMillis());
                InputDialog inputDialog = InputDialog.newInstance(new InputDialog.InfoListener() {
                    @Override
                    public void onDataInputComplete(HousekeepInfo housekeepInfo) {
                        if (housekeepInfo != null) {
                            mRef.push().setValue(housekeepInfo);
                        }
                    }
                });
                inputDialog.setArguments(data);
                inputDialog.show(fm, "InputDialog");
                break;
        }
    }

    @Subscribe
    public void onEvent(@NonNull Events events) {
        this.mHousekeepList = events.getHkList();
        setList();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setList() {
        items.clear();

        for(HousekeepInfo housekeepInfo : mHousekeepList) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Utils.stringToDate(housekeepInfo.getDate()));
            if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) && cal.get(Calendar.DATE) == now.get(Calendar.DATE)) {
                Log.e("FUCK", "setList: " + cal.compareTo(now));
                items.add(housekeepInfo);
            }
        }
    }

    private void modifyItem(final HousekeepInfo housekeepInfo) {
        AlertDialog.Builder choiceDialogBuilder = new AlertDialog.Builder(this);
        choiceDialogBuilder.setTitle("삭제")
                .setMessage("내역을 삭제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRef.child(housekeepInfo.getId()).removeValue();
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        final AlertDialog choiceDialog = choiceDialogBuilder.create();

        final CharSequence[] listItem = {"수정", "삭제"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("메뉴");
        alertDialogBuilder.setItems(listItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                switch (id) {
                    case 1:
                        choiceDialog.show();
                        break;
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<HousekeepInfo> items;
        private View.OnLongClickListener mListener;

        public DailyListAdapter(Context context, ArrayList<HousekeepInfo> items, View.OnLongClickListener mListener) {
            this.mContext = context;
            this.items = items;
            this.mListener = mListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dailylist, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            int itempos = position;

            String inout;

            if(items.get(itempos).getIsIncome()) {
                inout = getResources().getText(R.string.income).toString();
                viewHolder.inout.setTextColor(Color.parseColor("#0000FF"));
            }
            else {
                inout = getResources().getText(R.string.outcome).toString();
                viewHolder.inout.setTextColor(Color.parseColor("#FF0000"));
            }

            viewHolder.inout.setText(inout);

            viewHolder.value.setText(String.valueOf(items.get(itempos).getValue()));
            viewHolder.category.setText(items.get(itempos).getCategory());
            viewHolder.memo.setText(items.get(itempos).getMemo());
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView inout;
            public TextView value;
            public TextView category;
            public TextView memo;
            public ViewHolder(View view) {
                super(view);

                inout = (TextView)view.findViewById(R.id.item_daily_inex);
                value = (TextView)view.findViewById(R.id.item_daily_value);
                category = (TextView)view.findViewById(R.id.item_daily_cate);
                memo = (TextView)view.findViewById(R.id.item_daily_memo);

                if(mListener != null) {
                    view.setOnLongClickListener(mListener);
                }
            }
        }
    }

}
