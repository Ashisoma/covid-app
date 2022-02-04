package org.chskenya.covidapp.model;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LabTestType {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("specimen_types")
    @Expose
    private String[] specimenTypes;
    @SerializedName("results")
    @Expose
    private String[] results;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSpecimenTypes() {
        return specimenTypes;
    }

    public void setSpecimenTypes(String[] specimenTypes) {
        this.specimenTypes = specimenTypes;
    }

    public String[] getResults() {
        return results;
    }

    public void setResults(String[] results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return name;
    }
}
