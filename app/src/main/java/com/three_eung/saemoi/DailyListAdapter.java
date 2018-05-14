package com.three_eung.saemoi;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CH on 2018-03-19.
 */

public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<DailyList> items;
    private View.OnLongClickListener mListener;

    public DailyListAdapter(Context context, ArrayList<DailyList> items, View.OnLongClickListener mListener) {
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

        String inout = items.get(itempos).getInout();

        viewHolder.inout.setText(inout);
        if(inout.equals("income")) {
            viewHolder.inout.setTextColor(Color.parseColor("#0000FF"));
        } else {
            viewHolder.inout.setTextColor(Color.parseColor("#FF0000"));
        }

        viewHolder.value.setText(String.valueOf(items.get(itempos).getValue()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView inout;
        public TextView value;
        public TextView category;
        public ViewHolder(View view) {
            super(view);

            inout = (TextView)view.findViewById(R.id.inoutView);
            value = (TextView)view.findViewById(R.id.valueView);
            category = (TextView)view.findViewById(R.id.cateView);

            if(mListener != null) {
                view.setOnLongClickListener(mListener);
            }
        }
    }
}
