package org.chskenya.covidapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.chskenya.covidapp.offlineRoom.Converter.CountyConverter;
import org.chskenya.covidapp.offlineRoom.Converter.FacilityConverter;
import org.chskenya.covidapp.offlineRoom.Converter.LabTestTypeConverter;
import org.chskenya.covidapp.offlineRoom.Converter.StringConverter;

import java.io.Serializable;

@Entity(tableName  = "initialData") public class InitialData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private Integer id;
    @TypeConverters(FacilityConverter.class)
    @SerializedName("facilities")
    @Expose
    private Facility[] facilities;
    @TypeConverters(CountyConverter.class)
    @SerializedName("counties")
    @Expose
    private County[] counties;
    @TypeConverters(StringConverter.class)
    @SerializedName("nationalities")
    @Expose
    private String[] nationalities;
    @TypeConverters(LabTestTypeConverter.class)
    @SerializedName("lab_test_types")
    @Expose
    private LabTestType[] labTestTypes;
    @TypeConverters(StringConverter.class)
    @SerializedName("radiology_test_types")
    @Expose
    private String[] radiologyTestTypes;
    @TypeConverters(StringConverter.class)
    @SerializedName("xray_results")
    @Expose
    private String[] xrayResults;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Facility[] getFacilities() {
        return facilities;
    }

    public void setFacilities(Facility[] facilities) {
        this.facilities = facilities;
    }

    public County[] getCounties() {
        return counties;
    }

    public void setCounties(County[] counties) {
        this.counties = counties;
    }

    public String[] getNationalities() {
        return nationalities;
    }

    public void setNationalities(String[] nationalities) {
        this.nationalities = nationalities;
    }

    public LabTestType[] getLabTestTypes() {
        return labTestTypes;
    }

    public void setLabTestTypes(LabTestType[] labTestTypes) {
        this.labTestTypes = labTestTypes;
    }

    public String[] getRadiologyTestTypes() {
        return radiologyTestTypes;
    }

    public void setRadiologyTestTypes(String[] radiologyTestTypes) {
        this.radiologyTestTypes = radiologyTestTypes;
    }

    public String[] getXrayResults() {
        return xrayResults;
    }

    public void setXrayResults(String[] xrayResults) {
        this.xrayResults = xrayResults;
    }
}
