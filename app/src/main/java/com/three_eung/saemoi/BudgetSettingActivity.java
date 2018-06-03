package com.three_eung.saemoi;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.three_eung.saemoi.bind.BudgetBind;
import com.three_eung.saemoi.databinding.ActivityBudgetSettingBinding;
import com.three_eung.saemoi.databinding.ItemCategoryBudgetBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSettingActivity extends AppCompatActivity {
    private ArrayList<BudgetBind> mBindingList;
    private HashMap<String, Integer> mBudget;
    private HashMap<String, Integer> mCate;
    private BudgetSettingAdapter mAdapter;
    LinearLayoutManager layoutManager;
    ActivityBudgetSettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = (ActivityBudgetSettingBinding) DataBindingUtil.setContentView(this, R.layout.activity_budget_setting);

        //저장된 카테고리마다의 예산
        //카테고리 이름 - 예산 값 식비, 의류비, 유흥비
        mBudget = (HashMap<String, Integer>)((InitApp) getApplication()).getBudget();
        mCate = (HashMap<String, Integer>)((InitApp) getApplication()).getExCate();
        mBindingList = new ArrayList<>();

        for(String cate_key : mCate.keySet()) {
            for(String budget_key : mBudget.keySet()) {
                if(cate_key.equals(budget_key)) {
                    BudgetBind budgetBind = new BudgetBind(budget_key, Integer.toString(mBudget.get(budget_key)));

                    mBindingList.add(budgetBind);
                }
            }
        }

        mAdapter = new BudgetSettingAdapter(this, mBindingList);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mBinding.setBudgetList.setHasFixedSize(true);
        mBinding.setBudgetList.setLayoutManager(layoutManager);
        mBinding.setBudgetList.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mBinding.setBudgetList.addItemDecoration(dividerItemDecoration);
    }

    class BudgetSettingAdapter extends RecyclerView.Adapter<BudgetSettingAdapter.ViewHolder>{

        private Context mContext;
        private ArrayList<BudgetBind> items;
        private View.OnLongClickListener mListener;

        public BudgetSettingAdapter(Context context,ArrayList<BudgetBind> items) {
            this.mContext = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemCategoryBudgetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_category_budget, parent, false);

            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.itemBinding.setCate(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemCategoryBudgetBinding itemBinding;
            public ViewHolder(ItemCategoryBudgetBinding itemBinding) {
                super(itemBinding.getRoot());

                this.itemBinding = itemBinding;
            }
        }
    }
}