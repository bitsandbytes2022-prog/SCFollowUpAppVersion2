package com.varsity.dgmdashboard.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.varsity.dgmdashboard.DGMDashboardApplication;
import com.varsity.dgmdashboard.R;
import com.varsity.dgmdashboard.adapter.DgmDistrictAdapter;
import com.varsity.dgmdashboard.adapter.DistrictListAdapter;
import com.varsity.dgmdashboard.adapter.LeadAllocationAdapter;
import com.varsity.dgmdashboard.adapter.LeadAllocationManualAdapter;
import com.varsity.dgmdashboard.databinding.FragmentLeadAllotmentBinding;
import com.varsity.dgmdashboard.model.AssignLeadManualRequest;
import com.varsity.dgmdashboard.model.AssignLeadRequest;
import com.varsity.dgmdashboard.model.GetProDetailsLeadResponse;
import com.varsity.dgmdashboard.utils.SnackBar;
import com.varsity.dgmdashboard.viewmodel.DGMDashboardViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class LeadAllotmentFragment extends Fragment implements LeadAllocationManualAdapter.LeadAllocationListener {

    private FragmentLeadAllotmentBinding mBinding;
    private View snakBarView;
    private DGMDashboardViewModel dashboardViewModel;
    private LeadAllocationManualAdapter leadAllocationAdapter;
    private LeadAllocationAdapter leadAutoAllocationAdapter;
    private DgmDistrictAdapter districtListAdapter;
    private AssignLeadManualRequest request;
    private AssignLeadRequest requestAuto;
    private ArrayList<AssignLeadManualRequest.AssignTo> assignToArrayList;
    private int totalLeadCount;
    private boolean isLeadManual = false;

    private String proCount;

    private boolean  actionButton;


    public static LeadAllotmentFragment getNewInstance(Context context) {
        LeadAllotmentFragment fragment = new LeadAllotmentFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_allotment, container, false);
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
        request = new AssignLeadManualRequest();
        requestAuto = new AssignLeadRequest();
        assignToArrayList = new ArrayList<>();
        snakBarView = getActivity().findViewById(android.R.id.content);
        dashboardViewModel = new DGMDashboardViewModel(getContext());
        getAreaList();
        mBinding.leadStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isLeadManual = isChecked;
            if (isChecked) {
                mBinding.rvLeadManual.setVisibility(View.VISIBLE);
                mBinding.rvLeadAuto.setVisibility(View.GONE);
            } else {
                mBinding.rvLeadManual.setVisibility(View.GONE);
                mBinding.rvLeadAuto.setVisibility(View.VISIBLE);
            }
        });


        mBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLeadManual) {
                    if (totalLeadCount != 0 && request != null) {
                        ArrayList<AssignLeadManualRequest.AssignTo> updateArrayList= new ArrayList<>();
                        for (int i=0;i<assignToArrayList.size();i++){
                            if (assignToArrayList.get(i).getLeadCount()!=0){
                                updateArrayList.add(assignToArrayList.get(i));
                            }
                        }
                        request.setAssignTo(updateArrayList);

                        if (updateArrayList.size()!=0){
                            submitLeadManual();
                        }else{
                            SnackBar.showError(getContext(), snakBarView, "No Assigned Leads found");
                        }

                    } else {
                        SnackBar.showError(getContext(), snakBarView, "No Leads found");
                    }
                } else {
                    if (totalLeadCount != 0 && requestAuto != null) {
                        submitLeadAuto();
                    } else {
                        SnackBar.showError(getContext(), snakBarView, "No Leads found");
                    }
                }

            }
        });

    }


    private void getAreaList() {
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            dashboardViewModel.getDgmDistrictData(snakBarView).observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel != null) {
                    if (responseModel.size() != 0) {
                        districtListAdapter = new DgmDistrictAdapter(getContext(), responseModel);
                        mBinding.spArea.setAdapter(districtListAdapter);
                        mBinding.spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mBinding.tvAllotmentCount.setText(responseModel.get(position).getUnassignedCount().toString());
                                totalLeadCount = responseModel.get(position).getUnassignedCount();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        setLeadData();
                    } else {
                        mBinding.llArea.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.llArea.setVisibility(View.GONE);
                }
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }


    private void setLeadData() {
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            dashboardViewModel.getDGMProDetailsData(snakBarView).observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel != null) {
                    proCount  = String.valueOf(responseModel.size());
                    mBinding.tvProCount.setText(proCount);
                    if (responseModel.size() != 0) {
                        if (mBinding.leadStatus.isChecked()){
                            mBinding.rvLeadManual.setVisibility(View.VISIBLE);
                            mBinding.rvLeadAuto.setVisibility(View.GONE);
                        } else {
                            mBinding.rvLeadManual.setVisibility(View.GONE);
                            mBinding.rvLeadAuto.setVisibility(View.VISIBLE);
                        }
                        //mBinding.rvLeadManual.setVisibility(View.GONE);
                        mBinding.tvNoData.setVisibility(View.GONE);
                        countTotalLeads(responseModel);
                        countTotalLeadsAuto(responseModel);
                        Log.d("TotalLeads",String.valueOf(totalLeadCount));
                        leadAllocationAdapter = new LeadAllocationManualAdapter(responseModel,totalLeadCount, this);
                        leadAutoAllocationAdapter = new LeadAllocationAdapter(responseModel);
                        mBinding.rvLeadManual.setAdapter(leadAllocationAdapter);
                        mBinding.rvLeadAuto.setAdapter(leadAutoAllocationAdapter);
                        mBinding.btnSubmit.setEnabled(true);
                    } else {
                        mBinding.rvLeadManual.setVisibility(View.GONE);
                        mBinding.rvLeadAuto.setVisibility(View.GONE);
                        mBinding.tvNoData.setVisibility(View.VISIBLE);
                        mBinding.btnSubmit.setEnabled(false);
                    }
                }
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }

    private void countTotalLeads(ArrayList<GetProDetailsLeadResponse> list) {
        int totalLeads = 0;
        assignToArrayList = new ArrayList<>();
        for (GetProDetailsLeadResponse data : list) {
            AssignLeadManualRequest.AssignTo assignTo = new AssignLeadManualRequest.AssignTo();
            assignTo.setEmpID(data.getAssignedTo());
            assignTo.setLeadCount(0);
            assignToArrayList.add(assignTo);

            //int newTotal = data.getCommunicatedLeads() + data.getCompletedLeads() + data.getPendingLeads();
            int newTotal =data.getPendingLeads();
            totalLeads = totalLeads + newTotal;
            Log.d("getCommunicatedLeads",data.getCommunicatedLeads().toString());
            Log.d("getCompletedLeads",data.getCompletedLeads().toString());
            Log.d("getPendingLeads",data.getPendingLeads().toString());
           // totalLeadCount = totalLeads;
        }
        Log.d("TLL", String.valueOf(totalLeadCount));
        Log.d("TLLL", String.valueOf(totalLeads));

        //totalLeadCount = totalLeads;
       // mBinding.tvAllotmentCount.setText("" + totalLeads);
    }

    private void countTotalLeadsAuto(ArrayList<GetProDetailsLeadResponse> list) {
        int totalLeadsAuto = 0;
        ArrayList<AssignLeadRequest.AssignTo> assignToArrayList = new ArrayList<>();
        ArrayList<AssignLeadRequest.AssignTo> assignToArrayList1 = new ArrayList<>();
        for (GetProDetailsLeadResponse data : list) {
            AssignLeadRequest.AssignTo assignTo = new AssignLeadRequest.AssignTo();
            assignTo.setEmpID(data.getAssignedTo());
            assignToArrayList.add(assignTo);

            //int newTotal = data.getCommunicatedLeads() + data.getCompletedLeads() + data.getPendingLeads();
            int newTotal = data.getPendingLeads();
            totalLeadsAuto = totalLeadsAuto + newTotal;
           // totalLeadCount = totalLeadsAuto;
        }
        if (assignToArrayList.size()>totalLeadCount){
            for (int i=0;i<totalLeadCount;i++){
                assignToArrayList1.add(assignToArrayList.get(i));
            }
            requestAuto.setAssignTo(assignToArrayList1);
        }else {
            requestAuto.setAssignTo(assignToArrayList);
        }
        requestAuto.setTotalLead(totalLeadCount);
        //totalLeadCount = totalLeadsAuto;
       // mBinding.tvAllotmentCount.setText("" + totalLeadsAuto);
    }

    private void submitLeadManual() {

        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            Log.d("SubmitManual","yes");

            dashboardViewModel.submitLeadManual(snakBarView, request).observe(getViewLifecycleOwner(), responseModel -> {
                AlertDialog.Builder builderSaved = new AlertDialog.Builder(getContext(), R.style.MaterialAlertDialogStyle);
                builderSaved.setTitle("Status");
                builderSaved.setMessage("Lead Submitted Successfully");
                builderSaved.setCancelable(false);
                builderSaved.setPositiveButton("OK", (dialogInterface, i) -> {
                   onResume();
                    dialogInterface.dismiss();
                });
                builderSaved.show();
                builderSaved.create();
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }

    private void submitLeadAuto() {
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {

            dashboardViewModel.submitLead(snakBarView, requestAuto).observe(getViewLifecycleOwner(), responseModel -> {
                AlertDialog.Builder builderSaved = new AlertDialog.Builder(getContext(), R.style.MaterialAlertDialogStyle);
                builderSaved.setTitle("Status");
                builderSaved.setMessage("Lead Submitted Successfully");
                builderSaved.setCancelable(false);
                builderSaved.setPositiveButton("OK", (dialogInterface, i) -> {
                    onResume();
                    dialogInterface.dismiss();
                });
                builderSaved.show();
                builderSaved.create();
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }

    @Override
    public void updateLeadCount(GetProDetailsLeadResponse data, int leadCount, int type, int position) {
        if (request != null) {
            AssignLeadManualRequest.AssignTo assignTo = new AssignLeadManualRequest.AssignTo();
            assignTo.setEmpID(data.getId());
            assignTo.setLeadCount(leadCount);
            assignToArrayList.set(position, assignTo);
        }
    }
}
