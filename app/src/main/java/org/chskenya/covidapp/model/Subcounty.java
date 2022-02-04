package org.chskenya.covidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Subcounty implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String sname;
    @SerializedName("county_code")
    @Expose
    private Integer county_code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return sname;
    }

    public void setName(String sname) {
        this.sname = sname;
    }

    public Integer getCounty_code() {
        return county_code;
    }

    public void setCounty_code(Integer county_code) {
        this.county_code = county_code;
    }

    @Override
    public String toString() {
        return sname;
    }
}
