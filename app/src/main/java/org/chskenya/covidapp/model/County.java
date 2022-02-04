package org.chskenya.covidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class County implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String cname;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("capital")
    @Expose
    private String capital;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("subcounties")
    @Expose
    private Subcounty[] subcounties;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return cname;
    }

    public void setName(String cname) {
        this.cname = cname;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Subcounty[] getSubcounties() {
        return subcounties;
    }

    public void setSubcounties(Subcounty[] subcounties) {
        this.subcounties = subcounties;
    }

    @Override
    public String toString() {
        return cname;
    }
}
