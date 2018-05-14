package com.three_eung.saemoi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.three_eung.saemoi.databinding.ActivityDailylistBinding;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by CH on 2018-03-19.
 */

public class DailyListActivity extends AppCompatActivity implements View.OnClickListener {
    private DailyListAdapter mAdapter;
    private ActivityDailylistBinding mBinding;

    private LinearLayoutManager layoutManager;
    private ArrayList<DailyList> items;

    private String yearmonth;
    private int mYear, mMonth, mDay;

    private DatabaseReference mRef;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = (ActivityDailylistBinding) DataBindingUtil.setContentView(this, R.layout.activity_dailylist);

        Intent intent = getIntent();
        mYear = intent.getIntExtra("year", 0);
        mMonth = intent.getIntExtra("month", 0);
        mDay = intent.getIntExtra("day", 0);

        StringBuffer buffer = new StringBuffer();
        buffer.append(mYear);
        buffer.append(mMonth + 1);
        yearmonth = buffer.toString();

        items = new ArrayList<>();

        mAdapter = new DailyListAdapter(this, items, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = mBinding.moneylistView.getChildAdapterPosition(v);

                DailyList dailyList = items.get(position);

                modifyItem(dailyList);

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

        mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("inout");

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DailyList dailyList = dataSnapshot.getValue(DailyList.class);
                if (dailyList.getYearmonth().equals(yearmonth) && dailyList.getDay().equals(String.valueOf(mDay))) {
                    dailyList.setId(dataSnapshot.getKey());
                    items.add(dailyList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DailyList toRemove = new DailyList();

                for (Iterator<DailyList> iterator = items.iterator(); iterator.hasNext(); ) {
                    DailyList dailyList = iterator.next();

                    if (dailyList.getId().equals(dataSnapshot.getKey())) {
                        toRemove = dailyList;
                    }
                }

                items.remove(toRemove);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mBinding.dailyFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dailyFab:
                FragmentManager fm = getSupportFragmentManager();
                Bundle data = new Bundle();
                data.putInt("year", mYear);
                data.putInt("month", mMonth);
                data.putInt("day", mDay);
                InputDialog inputDialog = InputDialog.newInstance(new InputDialog.InfoListener() {
                    @Override
                    public void onDataInputComplete(EventInfo eventInfo) {
                        if (eventInfo != null) {
                            mRef.push().setValue(eventInfo);
                        }
                    }
                });
                inputDialog.setArguments(data);
                inputDialog.show(fm, "InputDialog");
                break;
        }
    }

    private void modifyItem(final DailyList dailyList) {
        AlertDialog.Builder choiceDialogBuilder = new AlertDialog.Builder(this);
        choiceDialogBuilder.setTitle("삭제")
                .setMessage("메시지를 삭제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRef.child(dailyList.getId()).removeValue();
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

        final CharSequence[] listItem = {"삭제"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("메뉴");
        alertDialogBuilder.setItems(listItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                switch (id) {
                    case 0:
                        choiceDialog.show();
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

}
