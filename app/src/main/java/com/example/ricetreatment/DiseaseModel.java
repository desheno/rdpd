package com.example.ricetreatment;

public class DiseaseModel {
    private String diseaseName, diseaseDescription, whereOccurs, howIdentify, treatmentPlan;
    private String diseaseImage;

    public DiseaseModel() {
    }

    public DiseaseModel(String diseaseName, String diseaseDescription, String whereOccurs, String howIdentify, String treatmentPlan, String diseaseImage) {
        this.diseaseName = diseaseName;
        this.diseaseDescription = diseaseDescription;
        this.whereOccurs = whereOccurs;
        this.howIdentify = howIdentify;
        this.treatmentPlan = treatmentPlan;
        this.diseaseImage = diseaseImage;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public void setDiseaseDescription(String diseaseDescription) { this.diseaseDescription = diseaseDescription; }

    public String getWhereOccurs() {
        return whereOccurs;
    }

    public void setWhereOccurs(String whereOccurs) {
        this.whereOccurs = whereOccurs;
    }

    public String getHowIdentify() {
        return howIdentify;
    }

    public void setHowIdentify(String howIdentify) {
        this.howIdentify = howIdentify;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getDiseaseImage() {
        return diseaseImage;
    }

    public void setDiseaseImage(String diseaseImage) {
        this.diseaseImage = diseaseImage;
    }
}
