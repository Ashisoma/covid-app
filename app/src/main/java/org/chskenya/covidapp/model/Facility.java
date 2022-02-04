package org.chskenya.covidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Facility implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("mflCode")
    @Expose
    private String mflCode;
    @SerializedName("name")
    @Expose
    private String fname;
    @SerializedName("county")
    @Expose
    private Integer county;
    @SerializedName("subCounty")
    @Expose
    private Integer subCounty;
    @SerializedName("project")
    @Expose
    private Integer project;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMflCode() {
        return mflCode;
    }

    public void setMflCode(String mflCode) {
        this.mflCode = mflCode;
    }

    public String getName() {
        return fname;
    }

    public void setName(String fname) {
        this.fname = fname;
    }

    public Integer getCounty() {
        return county;
    }

    public void setCounty(Integer county) {
        this.county = county;
    }

    public Integer getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(Integer subCounty) {
        this.subCounty = subCounty;
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return fname;
    }
}
