package com.varsity.dgmdashboard.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.varsity.dgmdashboard.R;
import com.varsity.dgmdashboard.databinding.RowAssignLeadManuallyBinding;
import com.varsity.dgmdashboard.model.GetProDetailsLeadResponse;
import com.varsity.dgmdashboard.utils.SnackBar;

import java.util.ArrayList;

public class LeadAllocationManualAdapter extends RecyclerView.Adapter<LeadAllocationManualAdapter.DataViewHolder> {


    private ArrayList<GetProDetailsLeadResponse> list;
    private LeadAllocationListener listener;
    private int totalLeadCount;
    private int assignnedLeads=0;

    public LeadAllocationManualAdapter(ArrayList<GetProDetailsLeadResponse> list,int totalLeadCount,LeadAllocationListener listener) {
        this.list = list;
        this.listener=listener;
        this.totalLeadCount = totalLeadCount;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAssignLeadManuallyBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_assign_lead_manually, parent, false);
        return new LeadAllocationManualAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadAllocationManualAdapter.DataViewHolder holder, int position) {
        holder.mBinding.tvUserName.setText(list.get(position).getName());
        String userName = list.get(position).getName();
        holder.mBinding.ivUser.setText(userName.trim().substring(0, 1));

        int total = list.get(position).getCommunicatedLeads() + list.get(position).getCompletedLeads() + list.get(position).getPendingLeads();
        holder.mBinding.tvTotalAllotmentCount.setText("" + total);

        holder.mBinding.tvCommunicated.setText(" " + list.get(position).getCommunicatedLeads());
        holder.mBinding.tvConverted.setText(" " + list.get(position).getCompletedLeads());
        holder.mBinding.tvPending.setText(" " + list.get(position).getPendingLeads());

        holder.mBinding.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("assignedLeads",String.valueOf(assignnedLeads));
                Log.d("Total leads",String.valueOf(totalLeadCount));

                if (totalLeadCount != 0 && assignnedLeads < totalLeadCount){
                    assignnedLeads= assignnedLeads + 1;
                    Log.d("assignedLeads",String.valueOf(assignnedLeads));
                    int lead= Integer.parseInt(holder.mBinding.tvLeadCount.getText().toString())+1;
                    holder.mBinding.tvLeadCount.setText(""+lead);
                    listener.updateLeadCount(list.get(position),lead,0,position);
                }
            }
        });

        holder.mBinding.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lead= Integer.parseInt(holder.mBinding.tvLeadCount.getText().toString());
                if (lead!=0){
                    assignnedLeads= assignnedLeads - 1;
                    Log.d("assignedLeads",String.valueOf(assignnedLeads));
                    lead--;
                }
                holder.mBinding.tvLeadCount.setText(""+lead);
                listener.updateLeadCount(list.get(position),lead,1,position);
            }
        });
    }

    @Override
    public int getItemCount() {
      return list != null ? list.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowAssignLeadManuallyBinding mBinding;

        public DataViewHolder(RowAssignLeadManuallyBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }

    public interface LeadAllocationListener{
        void updateLeadCount(GetProDetailsLeadResponse data,int leadCount, int type,int position);
    }
}
