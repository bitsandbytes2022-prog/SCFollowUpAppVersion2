package com.varsity.dgmdashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.varsity.dgmdashboard.R;
import com.varsity.dgmdashboard.activity.CallDialActivity;
import com.varsity.dgmdashboard.databinding.RowHistoryNewBinding;
import com.varsity.dgmdashboard.model.LeadStatusListResponseModel;

import java.util.ArrayList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.DataViewHolder> {

    private ArrayList<LeadStatusListResponseModel> list;
    private ArrayList<Integer> colorCodeList;
    private int mExpandedPosition = -1;

    private Context context;

    public HistoryListAdapter(ArrayList<LeadStatusListResponseModel> list, ArrayList<Integer> colorCodeList, Context context) {
        this.list = list;
        this.colorCodeList=colorCodeList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowHistoryNewBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_history_new, parent, false);
        return new HistoryListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListAdapter.DataViewHolder holder, int position) {
        holder.mBinding.tvUserName.setText(""+list.get(position).getName().trim());
        holder.mBinding.tvMobileNo.setText("" + list.get(position).getMobile());
        holder.mBinding.tvDate.setText("" + list.get(position).getFollowUpDate());
        holder.mBinding.tvEmployeeId.setText("" + list.get(position).getFollowupEmp());
        holder.mBinding.tvFollowupBy.setText("" + list.get(position).getFollowupEmpName());
        holder.mBinding.tvFeedback.setText("" + list.get(position).getFeedBack());
        holder.mBinding.tvComment.setText("" + list.get(position).getComment());

        if (list.get(position).getName() != null) {
            holder.mBinding.ivUser.setText(list.get(position).getName().trim().substring(0, 1));
        }

        holder.mBinding.ivCall.setOnClickListener(v -> {
            Context context = holder.mBinding.getRoot().getContext();
            Intent intent = new Intent(context, CallDialActivity.class);
            intent.putExtra("MOBILE_NO", list.get(position).getMobile());
            intent.putExtra("Lead_ID", list.get(position).getLeadId());
            intent.putExtra("Assigned_ID", list.get(position).getAssignedTo());
            intent.putExtra("STUDENT_NAME", list.get(position).getName());
            intent.putExtra("PARENT_NAME", list.get(position).getParentName());
            context.startActivity(intent);
        });

        /*Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));*/
        GradientDrawable bgShape = (GradientDrawable) holder.mBinding.ivUser.getBackground();
        bgShape.setColor(colorCodeList.get(position));

        final boolean isExpanded = position == mExpandedPosition;
        holder.mBinding.llDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        if (isExpanded){
            //holder.mBinding.tvShowHide.setText("Hide Details");
        }else {
            //holder.mBinding.tvShowHide.setText("Show Details");
        }
        holder.itemView.setActivated(isExpanded);
        holder.mBinding.tvShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mExpandedPosition = isExpanded ? -1 : position;
                showBottomSheetDialog(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;

    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowHistoryNewBinding mBinding;

        public DataViewHolder(RowHistoryNewBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;

        }
    }

    public void updateList(ArrayList<LeadStatusListResponseModel> newList){
        list = newList;
        notifyDataSetChanged();
    }

    private void showBottomSheetDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetStyle);
        bottomSheetDialog.setContentView(R.layout.history_bottom_sheet);

        TextView name = bottomSheetDialog.findViewById(R.id.name);
        TextView followup_by = bottomSheetDialog.findViewById(R.id.followup_by);
        TextView emp_id = bottomSheetDialog.findViewById(R.id.employee_id);
        TextView feedback = bottomSheetDialog.findViewById(R.id.feedback);
        TextView comment = bottomSheetDialog.findViewById(R.id.comments);

        name.setText(list.get(position).getName());
        followup_by.setText(list.get(position).getFollowupEmpName());
        emp_id.setText(list.get(position).getFollowupEmp());
        feedback.setText(list.get(position).getFeedBack());
        comment.setText(list.get(position).getComment());

        bottomSheetDialog.show();

    }
}
