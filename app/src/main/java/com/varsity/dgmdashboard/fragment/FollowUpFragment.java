package com.varsity.dgmdashboard.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.varsity.dgmdashboard.DGMDashboardApplication;
import com.varsity.dgmdashboard.R;
import com.varsity.dgmdashboard.adapter.FollowupListAdapter;
import com.varsity.dgmdashboard.databinding.FragmentFollowupBinding;
import com.varsity.dgmdashboard.model.LeadStatusListResponseModel;
import com.varsity.dgmdashboard.utils.PaginationListener;
import com.varsity.dgmdashboard.utils.SnackBar;
import com.varsity.dgmdashboard.viewmodel.DashboardViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FollowUpFragment extends Fragment {

    private FragmentFollowupBinding mBinding;
    private View snakBarView;
    private DashboardViewModel dashboardViewModel;
    private FollowupListAdapter followupListAdapter;
    private int pageNo = 0;
    private LinearLayoutManager layoutManager;
    public static boolean isLoadMore = false;
    private ArrayList<LeadStatusListResponseModel> followList;

    private ArrayList<LeadStatusListResponseModel> todaysList;

    private ArrayList<LeadStatusListResponseModel> pendingList ;

    private String currentDateAndTime;

    private int pos = 0;

    int value = 0;




    private String[] priorityList;

    public static FollowUpFragment getNewInstance(Context context) {
        FollowUpFragment fragment = new FollowUpFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_followup, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init();

    }


    @Override
    public void onResume() {
        init();
        super.onResume();
    }

    private void init() {
        //value = 0;
        followList = new ArrayList<>();
        todaysList = new ArrayList<>();
        pendingList = new ArrayList<>();
        snakBarView = getActivity().findViewById(android.R.id.content);
        dashboardViewModel = new DashboardViewModel(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        mBinding.rvFollowup.setLayoutManager(layoutManager);

        getFollowupListData();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        currentDateAndTime = currentDate.format(new Date());
        Log.d("Currendate",currentDateAndTime.toString());

        mBinding.rvFollowup.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (isLoadMore) {
                    pageNo++;
                    getLoadMoreFollowupListData();
                }
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
            }
        });

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

        /*mBinding.prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Posi", String.valueOf(position));
                pos = position;
                //getFollowupListData();
                //setData();
                showBottomSheetDialog(todaysList.size(),pendingList.size());
                setData();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        mBinding.prioritySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog(todaysList.size(),pendingList.size());
                setData();
            }
        });

    }

    private void setData() {
        if (value ==0 && todaysList.size() ==0){
            mBinding.mcvPending.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setText("No Today's Priority Data Found");
            setRecyclerVisibility(false);
        } else if (value ==1 && pendingList.size() ==0){
            mBinding.mcvPending.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setText("No Pending Calls Data Found");
            setRecyclerVisibility(false);
        }else{
            mBinding.tvNoData.setVisibility(View.GONE);
            setRecyclerVisibility(true);
        }
        if (value == 0){
            followupListAdapter = new FollowupListAdapter(todaysList);
            mBinding.rvFollowup.setAdapter(followupListAdapter);
        }else{
            followupListAdapter = new FollowupListAdapter(pendingList);
            mBinding.rvFollowup.setAdapter(followupListAdapter);
        }
    }


    private void setRecyclerVisibility(boolean isVisible) {
        if (isVisible) {
            mBinding.mcvPending.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setVisibility(View.GONE);
            mBinding.rvFollowup.setVisibility(View.VISIBLE);
            mBinding.searhNoData.setVisibility(View.GONE);
        }
        else{
            //mBinding.mcvPending.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
            mBinding.rvFollowup.setVisibility(View.GONE);
            mBinding.searhNoData.setVisibility(View.VISIBLE);

        }

    }

    private void getFollowupListData() {
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            dashboardViewModel.getFollowupList(snakBarView, "Pending", pageNo).observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel != null) {
                    if (responseModel.size() != 0) {
                        followList.addAll(responseModel);
                        isLoadMore = true;
                        todaysList.clear();
                        pendingList.clear();
                        for(int i=0; i< responseModel.size();i++){
                            if (responseModel.get(i).getReminderDateStr()!=null &&responseModel.get(i).getReminderDateStr().equalsIgnoreCase(currentDateAndTime)){
                                todaysList.add(responseModel.get(i));
                            }else if (responseModel.get(i).getReminderDateStr() == null || !Objects.equals(responseModel.get(i).getReminderDateStr(), currentDateAndTime)){
                                pendingList.add(responseModel.get(i));
                            }
                        }

                        priorityList = new String[]{"Today's Priority (" + todaysList.size() + ")" , "Pending Calls (" + pendingList.size() + ")" };
                        mBinding.prioritySpinner.setText("Today's Priority (" + todaysList.size() + ")");

                       // mBinding.tvNoData.setVisibility(View.GONE);
                        if (value ==0 && todaysList.size() ==0){
                            mBinding.mcvPending.setVisibility(View.GONE);
                            mBinding.tvNoData.setVisibility(View.VISIBLE);
                            setRecyclerVisibility(false);
                        }

                        followupListAdapter = new FollowupListAdapter(todaysList);
                        mBinding.rvFollowup.setAdapter(followupListAdapter);
                        if (value ==0 && todaysList.size() ==0){
                            mBinding.tvNoData.setVisibility(View.VISIBLE);
                            setRecyclerVisibility(false);
                        }else if (value == 1 && pendingList.size() !=0){
                            mBinding.tvNoData.setVisibility(View.GONE);
                            followupListAdapter = new FollowupListAdapter(pendingList);
                            mBinding.rvFollowup.setAdapter(followupListAdapter);
                            setRecyclerVisibility(true);
                            mBinding.prioritySpinner.setText("Pending Calls (" + pendingList.size() + ")");
                            setData();
                        }else{
                            mBinding.tvNoData.setVisibility(View.GONE);
                            setRecyclerVisibility(true);
                        }
                        //setRecyclerVisibility(true);
                    } else {
                        setRecyclerVisibility(false);
                        isLoadMore = false;
                    }
                } else {
                    setRecyclerVisibility(false);
                    isLoadMore = false;
                }
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }


    private void getLoadMoreFollowupListData() {
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            dashboardViewModel.getFollowupList(snakBarView, "Pending", pageNo).observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel != null) {
                    if (responseModel.size() != 0) {
                        followList.addAll(responseModel);
                        isLoadMore = true;
                        followupListAdapter.addItems(responseModel);
                        followupListAdapter.notifyDataSetChanged();
                        if (value ==1) {
                            mBinding.prioritySpinner.setText("Pending Calls (" + pendingList.size() + ")");
                        }else if (value ==0){
                            mBinding.prioritySpinner.setText("Today's Priority (" + todaysList.size() + ")");
                        }
                    } else {
                        isLoadMore = false;
                    }
                } else {
                    isLoadMore = false;
                }
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }

    void filter(String text) {
        ArrayList<LeadStatusListResponseModel> temp = new ArrayList();
        for (LeadStatusListResponseModel d : followList) {
//            if (d.getName().toLowerCase().contains(text.toLowerCase()) || d.getMobile().toString().toLowerCase().contains(text.toLowerCase())) {
//                temp.add(d);
//            }
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        if (temp != null && temp.size() != 0) {
//            mBinding.tvNoData.setVisibility(View.GONE);
//            mBinding.rvFollowup.setVisibility(View.VISIBLE);
            setRecyclerVisibility(true);
            followupListAdapter.updateList(temp);
        } else {
            setRecyclerVisibility(false);
//            mBinding.rvFollowup.setVisibility(View.GONE);
//            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showBottomSheetDialog(int todays_count, int pending_count) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetStyle);
        bottomSheetDialog.setContentView(R.layout.bottom_dailog_box);

        TextView todays = bottomSheetDialog.findViewById(R.id.todays_count);

        TextView pending = bottomSheetDialog.findViewById(R.id.pending_count);

        todays.setText("Today's Priority (" +todays_count +")");

        pending.setText("Pending Calls (" +pending_count +")");

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
                mBinding.prioritySpinner.setText("Today's Priority (" + todaysList.size() + ")");
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
                mBinding.prioritySpinner.setText("Pending Calls (" + pendingList.size() + ")");
                setData();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }


}
