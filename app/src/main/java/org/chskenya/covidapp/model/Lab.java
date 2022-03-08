package org.chskenya.covidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Lab implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("patient_id")
    @Expose
    private int patient_id;
    @SerializedName("specimen_collected")
    @Expose
    private String specimen_collected;
    @SerializedName("reason_not_collected")
    @Expose
    private String reason_not_collected;
    @SerializedName("specimen_type")
    @Expose
    private String specimen_type;
    @SerializedName("specimen_type_other")
    @Expose
    private String specimen_type_other;
    @SerializedName("date_collected")
    @Expose
    private String date_collected;
    @SerializedName("date_sent_to_lab")
    @Expose
    private String date_sent_to_lab;
    @SerializedName("date_received_in_lab")
    @Expose
    private String date_received_in_lab;
    @SerializedName("confirming_lab")
    @Expose
    private String confirming_lab;
    @SerializedName("assay_used")
    @Expose
    private String assay_used;
    @SerializedName("lab_result")
    @Expose
    private String lab_result;
    @SerializedName("sequencing_done")
    @Expose
    private String sequencing_done;
    @SerializedName("lab_confirmation_date")
    @Expose
    private String lab_confirmation_date;
    @SerializedName("investigator")
    @Expose
    private Integer investigator;

    @SerializedName("test_type")
    @Expose
    private String test_type;

    public String getTest_type() {
        return test_type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getSpecimen_collected() {
        return specimen_collected;
    }

    public void setSpecimen_collected(String specimen_collected) {
        this.specimen_collected = specimen_collected;
    }

    public String getReason_not_collected() {
        return reason_not_collected;
    }

    public void setReason_not_collected(String reason_not_collected) {
        this.reason_not_collected = reason_not_collected;
    }

    public String getSpecimen_type() {
        return specimen_type;
    }

    public void setSpecimen_type(String specimen_type) {
        this.specimen_type = specimen_type;
    }

    public String getSpecimen_type_other() {
        return specimen_type_other;
    }

    public void setSpecimen_type_other(String specimen_type_other) {
        this.specimen_type_other = specimen_type_other;
    }

    public String getDate_collected() {
        return date_collected;
    }

    public void setDate_collected(String date_collected) {
        this.date_collected = date_collected;
    }

    public String getDate_sent_to_lab() {
        return date_sent_to_lab;
    }

    public void setDate_sent_to_lab(String date_sent_to_lab) {
        this.date_sent_to_lab = date_sent_to_lab;
    }

    public String getDate_received_in_lab() {
        return date_received_in_lab;
    }

    public void setDate_received_in_lab(String date_received_in_lab) {
        this.date_received_in_lab = date_received_in_lab;
    }

    public String getConfirming_lab() {
        return confirming_lab;
    }

    public void setConfirming_lab(String confirming_lab) {
        this.confirming_lab = confirming_lab;
    }

    public String getAssay_used() {
        return assay_used;
    }

    public void setAssay_used(String assay_used) {
        this.assay_used = assay_used;
    }

    public String getLab_result() {
        return lab_result;
    }

    public void setLab_result(String lab_result) {
        this.lab_result = lab_result;
    }

    public String getSequencing_done() {
        return sequencing_done;
    }

    public void setSequencing_done(String sequencing_done) {
        this.sequencing_done = sequencing_done;
    }

    public String getLab_confirmation_date() {
        return lab_confirmation_date;
    }

    public void setLab_confirmation_date(String lab_confirmation_date) {
        this.lab_confirmation_date = lab_confirmation_date;
    }

    public Integer getInvestigator() {
        return investigator;
    }

    public void setInvestigator(Integer investigator) {
        this.investigator = investigator;
    }
}
