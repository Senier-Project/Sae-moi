package com.three_eung.saemoi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;

public class SavingTab extends CustomFragment implements View.OnClickListener {
    private static final String TAG = SavingTab.class.getSimpleName();

    private View mView;
    private SavingAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private DatabaseReference mRef;
    private ArrayList<SavingInfo> mSavingList;

    private TextView totalSave;
    private int totalSaving;

    public static Fragment newInstance(ArrayList<SavingInfo> mSavingList) {
        Bundle args = new Bundle();
        args.putString("TAG", TAG);

        SavingTab savingTab = new SavingTab();
        savingTab.setArguments(args);
        savingTab.setSavingData(mSavingList);

        return savingTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_saving, container, false);

        mSavingList = ((InitApp)(getActivity().getApplication())).getSavingList();

        TextView saveUid = (TextView) mView.findViewById(R.id.save_uid);
        saveUid.setText(InitApp.sUser.getDisplayName());
        totalSave = (TextView) mView.findViewById(R.id.save_total);

        mAdapter = new SavingAdapter(this.getContext(), mSavingList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.getChildAdapterPosition(v);

                SavingInfo savingInfo = mSavingList.get(position);

                showList(savingInfo);
            }
        }, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = mRecyclerView.getChildAdapterPosition(v);

                SavingInfo savingInfo = mSavingList.get(position);

                modifyItem(savingInfo);

                return true;
            }
        });

        layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.save_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("saving");

        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.save_fab);
        fab.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_fab:
                FragmentManager fm = getChildFragmentManager();
                SavingInputDialog inputDialog = SavingInputDialog.newInstance(new SavingInputDialog.InfoListener() {
                    @Override
                    public void onDataInputComplete(SavingInfo savingInfo) {
                        if (savingInfo != null) {
                            mRef.push().setValue(savingInfo);
                        }
                    }
                });
                inputDialog.show(fm, "InputDialogSaving");
                break;
        }
    }

    private void showList(SavingInfo savingInfo) {
        FragmentManager fm = getChildFragmentManager();
        SavingListDialog.newInstance(savingInfo).show(fm, "SavingList");
    }

    private void modifyItem(final SavingInfo savingInfo) {
        AlertDialog.Builder choiceDialogBuilder = new AlertDialog.Builder(this.getContext());
        choiceDialogBuilder.setTitle("삭제")
                .setMessage("메시지를 삭제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRef.child(savingInfo.getId()).removeValue();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
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

    private void setTotal() {
        totalSaving = 0;

        for (SavingInfo savingInfo : mSavingList) {
            totalSaving += savingInfo.getValue() * savingInfo.getCount();
        }

        if (totalSave != null) {
            totalSave.setText(Utils.toCurrencyFormat(totalSaving));
        }
    }

    @Override
    public void setHousekeepData(ArrayList<HousekeepInfo> housekeepData) {
    }

    @Override
    public void setSavingData(ArrayList<SavingInfo> savingData) {
        this.mSavingList = savingData;
        setTotal();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mSavingList = events.getSvList();
        setTotal();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mSavingList = ((InitApp)(getActivity().getApplication())).getSavingList();
        setTotal();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<SavingInfo> items;
        private View.OnClickListener mListener;
        private View.OnLongClickListener mLongListener;

        public SavingAdapter(Context context, ArrayList<SavingInfo> items, View.OnClickListener mListener, View.OnLongClickListener mLongListener) {
            this.mContext = context;
            this.items = items;
            this.mListener = mListener;
            this.mLongListener = mLongListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saving, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final int itempos = position;

            String title = items.get(itempos).getTitle();
            int unit = items.get(itempos).getValue();
            int total = items.get(itempos).getCount() * unit;

            Log.e(TAG, "onBindViewHolder: " + title + "  " + itempos);

            viewHolder.title.setText(title);
            viewHolder.unit.setText(Utils.toCurrencyFormat(unit) + "원");
            viewHolder.total.setText(Utils.toCurrencyFormat(total) + "원");
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SavingInfo savingInfo = items.get(itempos);
                    String key = savingInfo.getId();
                    int count = savingInfo.getCount() + 1;
                    ArrayList<String> savedDate = savingInfo.getSavedDate();

                    if (savedDate == null)
                        savedDate = new ArrayList<>();

                    Calendar calendar = Calendar.getInstance();
                    savedDate.add(Utils.dateToString(calendar.getTime()));

                    SavingInfo temp = new SavingInfo(savingInfo.getTitle(), savingInfo.getValue(), count, savingInfo.getStartDate(), savedDate);

                    mRef.child(key).setValue(temp);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            else
                return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView unit;
            public TextView total;
            public ImageView btn;

            public ViewHolder(View view) {
                super(view);

                title = (TextView) view.findViewById(R.id.save_title);
                unit = (TextView) view.findViewById(R.id.save_unit);
                total = (TextView) view.findViewById(R.id.save_total);
                btn = (ImageView) view.findViewById(R.id.save_btn);

                if(mListener != null) {
                    view.setOnClickListener(mListener);
                }

                if (mLongListener != null) {
                    view.setOnLongClickListener(mLongListener);
                }
            }
        }
    }
}
