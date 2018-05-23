package com.three_eung.saemoi;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSettingActivity extends AppCompatActivity {
    private HashMap<String, Integer> mBudget;
    private BudgetSettingAdapter mAdapter;
    LinearLayoutManager layoutManager;
    //private ActivityBudgetSettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setting);
        RecyclerView mRecyclerView = findViewById(R.id.category_list);

        //mBinding = (ActivityBudgetSettingBinding) DataBindingUtil.setContentView(this, R.layout.activity_budget_setting);

        //저장된 카테고리마다의 예산
        //카테고리 이름 - 예산 값 식비, 의류비, 유흥비
        mBudget = mBudget = (HashMap<String, Integer>)((InitApp) getApplication()).getBudget();
        ArrayList<String> cate = new ArrayList<>();
        ArrayList<Integer> bud = new ArrayList<>();

        cate.addAll(mBudget.keySet());
        bud.addAll(mBudget.values());

        Log.e("NNNN", "onCreate: " + cate.toString() + bud.toString());

        mAdapter = new BudgetSettingAdapter(this, cate, bud);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    class BudgetSettingAdapter extends RecyclerView.Adapter<BudgetSettingAdapter.ViewHolder>{

        private Context mContext;
        private HashMap<String, Integer> items;
        private ArrayList<String> cate;
        private ArrayList<Integer> bud;
        private View.OnLongClickListener mListener;

        public BudgetSettingAdapter(Context context,ArrayList<String> cate, ArrayList<Integer> bud){
            this.mContext = context;
            this.cate = cate;
            this.bud = bud;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_budget, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            int itempos = position;

            Log.e("MMMM", "onBindViewHolder: " + cate.toString() + bud.toString());

            viewHolder.category.setText(cate.get(itempos));
            viewHolder.budget.setText(bud.get(itempos).toString());
        }
        @Override
        public int getItemCount() { return cate.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView category;
            public EditText budget;
            public ViewHolder(View view) {
                super(view);

                category = (TextView)view.findViewById(R.id.category_title);
                budget = (EditText) view.findViewById(R.id.category_budget);
            }
        }
    }
}