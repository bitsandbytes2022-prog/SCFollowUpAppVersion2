package com.varsity.dgmdashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DGMDistrictResponse {
    @SerializedName("districtId")
    @Expose
    private Integer districtId;
    @SerializedName("districtName")
    @Expose
    private String districtName;
    @SerializedName("unassignedCount")
    @Expose
    private Integer unassignedCount;

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getUnassignedCount() {
        return unassignedCount;
    }

    public void setUnassignedCount(Integer unassignedCount) {
        this.unassignedCount = unassignedCount;
    }
}
