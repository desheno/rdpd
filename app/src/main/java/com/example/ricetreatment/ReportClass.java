package com.example.ricetreatment;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ReportClass {
    private String diseaseName;
    private String fieldType;
    private String locationArea;
    private String treatmentPlan;
    private String comments;

    public ReportClass() {
    }

    public ReportClass(String diseaseName, String fieldType, String locationArea, String treatmentPlan, String comments) {
        this.diseaseName = diseaseName;
        this.fieldType = fieldType;
        this.locationArea = locationArea;
        this.treatmentPlan = treatmentPlan;
        this.comments = comments;
    }

    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public void setLocationArea(String locationArea) { this.locationArea = locationArea; }

    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }

    public void setComments(String comments) { this.comments = comments; }

    public String getDiseaseName() { return diseaseName; }

    public String getFieldType() {
        return fieldType;
    }

    public String getLocationArea() {
        return locationArea;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public String getComments() {
        return comments;
    }
}
