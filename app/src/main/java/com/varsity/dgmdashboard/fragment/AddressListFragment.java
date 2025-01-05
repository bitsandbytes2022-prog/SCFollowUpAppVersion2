package com.varsity.dgmdashboard.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.varsity.dgmdashboard.DGMDashboardApplication;
import com.varsity.dgmdashboard.R;
import com.varsity.dgmdashboard.adapter.AddressListAdapter;
import com.varsity.dgmdashboard.databinding.FragmentAddressListBinding;
import com.varsity.dgmdashboard.listener.PhoneCallListener;
import com.varsity.dgmdashboard.model.CallStatusListResponseModel;
import com.varsity.dgmdashboard.model.GetProDetailsLeadResponse;
import com.varsity.dgmdashboard.utils.SnackBar;
import com.varsity.dgmdashboard.viewmodel.DashboardViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddressListFragment extends Fragment {

    private FragmentAddressListBinding mBinding;
    private View snakBarView;
    private DashboardViewModel dashboardViewModel;
    private AddressListAdapter addressListAdapter;

    private AddressListAdapter searchListAdapter;
    private AddressListAdapter todayListAdapter;
    private String mobileNo = "";
    private ArrayList<CallStatusListResponseModel> addressList = new ArrayList<>();

    private ArrayList<CallStatusListResponseModel> pendingCallList ;
    private ArrayList<CallStatusListResponseModel> todayCallList ;

    private String currentDateAndTime;

    public static AddressListFragment getNewInstance(Context context) {
        AddressListFragment fragment = new AddressListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_list, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        pendingCallList = new ArrayList<>();
        todayCallList = new ArrayList<>();
        snakBarView = getActivity().findViewById(android.R.id.content);
        dashboardViewModel = new DashboardViewModel(getContext());

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        currentDateAndTime = currentDate.format(new Date());
        Log.d("Currendate",currentDateAndTime.toString());

        getAddressListData();



        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkText();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void checkText() {
        Log.d("textt",mBinding.edtSearch.getText().toString());
        if (mBinding.edtSearch.getText().toString().equalsIgnoreCase("") || mBinding.edtSearch.getText().toString().equalsIgnoreCase(null)){
            mBinding.lytSearch.setVisibility(View.GONE);
            if (todayCallList.size() == 0){
                mBinding.lytToday.setVisibility(View.GONE);
            }else {
                mBinding.lytToday.setVisibility(View.VISIBLE);
            }
            if (pendingCallList.size() == 0){
                mBinding.lytPending.setVisibility(View.GONE);
            }else {
                mBinding.lytPending.setVisibility(View.VISIBLE);
            }
        }else{
            mBinding.lytSearch.setVisibility(View.VISIBLE);
            mBinding.lytToday.setVisibility(View.GONE);
            mBinding.lytPending.setVisibility(View.GONE);
        }
    }

    private void getAddressListData() {
        todayCallList.clear();
        pendingCallList.clear();
        addressList.clear();
        if (DGMDashboardApplication.getInstance().isNetworkAvailable()) {
            dashboardViewModel.getCallStatusList(snakBarView).observe(getViewLifecycleOwner(), responseModel -> {
                if (responseModel != null) {
                    if (responseModel.size() != 0) {
                        mBinding.edtSearch.setVisibility(View.VISIBLE);
                        mBinding.lytToday.setVisibility(View.VISIBLE);
                        mBinding.lytPending.setVisibility(View.VISIBLE);
                        mBinding.noDataFound.setVisibility(View.GONE);
                        addressList.addAll(responseModel);
                        //addressList = responseModel;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                           todayCallList.addAll(addressList.stream().filter(e -> e.getRemainderDate()!=null && e.getRemainderDate().equalsIgnoreCase(currentDateAndTime)).collect(Collectors.toList()));
//                            pendingCallList.addAll(addressList.stream().filter(e -> e.getRemainderDate()==null || !Objects.equals(e.getRemainderDate(), currentDateAndTime)).collect(Collectors.toList()));
//                        }else{
                            for(int i=0; i< addressList.size();i++){
                                if (addressList.get(i).getRemainderDate()!=null &&addressList.get(i).getRemainderDate().equalsIgnoreCase(currentDateAndTime)){
                                    todayCallList.add(addressList.get(i));
                                }else if (addressList.get(i).getRemainderDate() == null || !Objects.equals(addressList.get(i).getRemainderDate(), currentDateAndTime)){
                                    pendingCallList.add(addressList.get(i));
                                }
                            }

                            if (todayCallList.size() == 0){
                                mBinding.lytToday.setVisibility(View.GONE);
                            }

                        //}
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            pendingCallList = addressList.stream().filter();
//                        }
                            //if (responseModel.get(1).getRemainderDate().equalsIgnoreCase(currentDateAndTime)){
                                //todayCallList.add(responseModel.get(1));
                        Log.d("todayCallList",responseModel.toString());
                        mBinding.tvNoData.setVisibility(View.GONE);
                        addressListAdapter = new AddressListAdapter(pendingCallList);
                        todayListAdapter = new AddressListAdapter(todayCallList);
                        searchListAdapter = new AddressListAdapter(addressList);
                        mBinding.rvSearch.setAdapter(searchListAdapter);
                        mBinding.rvPending.setAdapter(addressListAdapter);
                        mBinding.rvToday.setAdapter(todayListAdapter);

                        mBinding.todayCallsTile.setText("Today's Priority (" +todayCallList.size()+")");
                        mBinding.pendingCallsTile.setText("Pending Calls (" +pendingCallList.size()+")");

                    }
                }
            });
        } else {
            SnackBar.showInternetError(getContext(), snakBarView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode) {
            case 9:
                permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionGranted) {
            call(mobileNo);
        } else {
            Toast.makeText(getContext(), "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }


    private void call(String mobileNumber) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + mobileNumber));
            getActivity().startActivity(callIntent);
        } else {
            Toast.makeText(getContext(), "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    void filter(String text) {
        ArrayList<CallStatusListResponseModel> temp = new ArrayList();
        if (addressList != null)
            for (CallStatusListResponseModel d : addressList) {
                if (d.getStudentName().toLowerCase().contains(text.toLowerCase()) ||  d.getMobileNo().toString().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                    Log.d("sizee", String.valueOf(temp.size()));
                    Log.d("name",d.getStudentName());
                    mBinding.rvSearch.setVisibility(View.VISIBLE);
                    mBinding.tvNoDataSearch.setVisibility(View.GONE);
                }else if (temp == null && temp.size() == 0){
                    mBinding.rvSearch.setVisibility(View.GONE);
                    mBinding.tvNoDataSearch.setVisibility(View.VISIBLE);
                }
            }
        if (temp != null && temp.size() != 0) {
            mBinding.tvNoDataSearch.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.GONE);
            mBinding.rvPending.setVisibility(View.VISIBLE);
            searchListAdapter.updateList(temp);
        } else {
            mBinding.rvPending.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }
    }

}
