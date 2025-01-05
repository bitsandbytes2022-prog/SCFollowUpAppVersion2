package com.varsity.dgmdashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveLeadData {
        @SerializedName("assignedTo")
        @Expose
        private Integer assignedTo;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("feedBack")
        @Expose
        private String feedBack;
        @SerializedName("followUpType")
        @Expose
        private String followUpType;
        @SerializedName("followupStatus")
        @Expose
        private Boolean followupStatus;
        @SerializedName("leadId")
        @Expose
        private Integer leadId;
        @SerializedName("leadStatusId")
        @Expose
        private Integer leadStatusId;

    public Integer getAppNo() {
        return appNo;
    }

    public void setAppNo(Integer appNo) {
        this.appNo = appNo;
    }

    @SerializedName("appNo")
    @Expose
    private Integer appNo;

        public Integer getAssignedTo() {
            return assignedTo;
        }

        public void setAssignedTo(Integer assignedTo) {
            this.assignedTo = assignedTo;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getFeedBack() {
            return feedBack;
        }

        public void setFeedBack(String feedBack) {
            this.feedBack = feedBack;
        }

        public String getFollowUpType() {
            return followUpType;
        }

        public void setFollowUpType(String followUpType) {
            this.followUpType = followUpType;
        }

        public Boolean getFollowupStatus() {
            return followupStatus;
        }

        public void setFollowupStatus(Boolean followupStatus) {
            this.followupStatus = followupStatus;
        }

        public Integer getLeadId() {
            return leadId;
        }

        public void setLeadId(Integer leadId) {
            this.leadId = leadId;
        }

        public Integer getLeadStatusId() {
            return leadStatusId;
        }

        public void setLeadStatusId(Integer leadStatusId) {
            this.leadStatusId = leadStatusId;
        }

    }

