package com.varsity.dgmdashboard.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.varsity.dgmdashboard.DGMDashboardApplication;
import com.varsity.dgmdashboard.R;
import com.varsity.dgmdashboard.adapter.HistoryListAdapter;
import com.varsity.dgmdashboard.databinding.FragmentHistoryBinding;
import com.varsity.dgmdashboard.model.LeadStatusListResponseModel;
import com.varsity.dgmdashboard.utils.SnackBar;
import com.varsity.dgmdashboard.viewmodel.DashboardViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding mBinding;
    private View snakBarView;
    private DashboardViewModel dashboardViewModel;
    private HistoryListAdapter historyListAdapter;
    ArrayList<LeadStatusListResponseModel> historyList;

    ArrayList<LeadStatusListResponseModel> todaysList ;

    ArrayList<LeadStatusListResponseModel> pendingList;
    ArrayList<Integer> colorCodeList;

    private String currentDateAndTime;


    int value =0;
    public static HistoryFragment getNewInstance(Context context) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        init();
        return mBinding.getRoot();
    }

    private void init() {
        pendingList = new ArrayList<>();
        todaysList = new ArrayList<>();
        snakBarView = getActivity().findViewById(android.R.id.content);
        dashboardViewModel = new DashboardViewModel(getContext());
        mBinding.mcvPending.setVisibility(View.GONE);
        mBinding.tvNoData.setVisibility(View.VISIBLE);

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        currentDateAndTime = currentDate.format(new Date());
        Log.d("Currendate",currentDateAndTime.toString());

        getHistoryListData();

        mBinding.prioritySpinner.setText("Today's Call History (" + todaysList.size() + ")");


        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        mBinding.prioritySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog(todaysList.size(),pendingList.size());
                setData();
            }
        });
    }

    private void setData() {
        if (value == 0 && todaysList.size() ==0){
            mBinding.mcvPending.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }else if (value == 1 && pendingList.size() ==0){
            mBinding.mcvPending.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }else {
            mBinding.mcvPending.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setVisibility(View.GONE);
        }
        if (value == 0){
            historyListAdapter=new HistoryListAdapter(todaysList,colorCodeList,getContext());
            mBinding.rvHistory.setAdapter(historyListAdapter);
        }else{
            historyListAdapter=new HistoryListAdapter(pendingList,colorCodeList, getContext());
            mBinding.rvHistory.setAdapter(historyListAdapter);
        }
    }

    private void getHistoryListData() {
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            dashboardViewModel.getStatusWiseLeadsList(snakBarView,"Communicated").observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel != null) {
                    if (responseModel.size() != 0) {
                        mBinding.tvNoData.setVisibility(View.GONE);
                        historyList=responseModel;
                        colorCodeList=new ArrayList<>();
                        for (LeadStatusListResponseModel data:historyList){
                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            colorCodeList.add(color);
                        }
                        todaysList.clear();
                        pendingList.clear();
                        for(int i=0; i< responseModel.size();i++){
                            if (responseModel.get(i).getReminderDateStr()!=null &&responseModel.get(i).getReminderDateStr().equalsIgnoreCase(currentDateAndTime)){
                                todaysList.add(responseModel.get(i));
                            }else if (responseModel.get(i).getReminderDateStr() == null || !Objects.equals(responseModel.get(i).getReminderDateStr(), currentDateAndTime)){
                                pendingList.add(responseModel.get(i));
                            }
                        }
                        if (value == 0 && todaysList.size() ==0){
                            mBinding.mcvPending.setVisibility(View.GONE);
                            mBinding.tvNoData.setVisibility(View.VISIBLE);
                        }
                        historyListAdapter=new HistoryListAdapter(todaysList,colorCodeList, getContext());
                        mBinding.rvHistory.setAdapter(historyListAdapter);
                    }else {
                        mBinding.tvNoData.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }

    void filter(String text){
        ArrayList<LeadStatusListResponseModel> temp = new ArrayList();
        /*for(LeadStatusListResponseModel d: historyList){
            if(d.getName().toLowerCase().contains(text.toLowerCase()) || d.getMobile().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }*/

        for(LeadStatusListResponseModel d: historyList){
            if(d.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        if (temp!=null && temp.size()!=0){
            mBinding.tvNoData.setVisibility(View.GONE);
            mBinding.rvHistory.setVisibility(View.VISIBLE);
            historyListAdapter.updateList(temp);
        }else {
            mBinding.rvHistory.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showBottomSheetDialog(int todays_count, int pending_count) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetStyle);
        bottomSheetDialog.setContentView(R.layout.bottom_dailog_box);

        TextView todays = bottomSheetDialog.findViewById(R.id.todays_count);

        TextView pending = bottomSheetDialog.findViewById(R.id.pending_count);

        todays.setText("Today's Call History (" +todays_count +")");

        pending.setText("Pending Call History (" +pending_count +")");

        if (value ==0){
            todays.setTextColor(Color.parseColor("#1B6EBA"));
            pending.setTextColor(Color.parseColor("#979797"));
        }else{
            todays.setTextColor(Color.parseColor("#979797"));
            pending.setTextColor(Color.parseColor("#1B6EBA"));
        }

        todays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todays.setTextColor(Color.parseColor("#1B6EBA"));
                pending.setTextColor(Color.parseColor("#979797"));
                value = 0;
                mBinding.prioritySpinner.setText("Today's Call History (" + todaysList.size() + ")");
                setData();
                bottomSheetDialog.dismiss();

            }
        });
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todays.setTextColor(Color.parseColor("#979797"));
                pending.setTextColor(Color.parseColor("#1B6EBA"));
                value = 1;
                mBinding.prioritySpinner.setText("Previous Call History (" + pendingList.size() + ")");
                setData();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

}
