package org.chskenya.covidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Radiology implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("date_requested")
    @Expose
    private String date_requested;

    @SerializedName("date_done")
    @Expose
    private String date_done;

    @SerializedName("results")
    @Expose
    private String results;

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("test_type")
    @Expose
    private String test_type;

    public String getTest_type() {
        return test_type;
    }

    public void setTest_type(String test_type) {
        this.test_type = test_type;
    }

    public Integer getId() {
        return id;
    }

    public String getDate_requested() {
        return date_requested;
    }

    public String getDate_done() {
        return date_done;
    }

    public String getResults() {
        return results;
    }

    public String getComments() {
        return comments;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate_requested(String date_requested) {
        this.date_requested = date_requested;
    }

    public void setDate_done(String date_done) {
        this.date_done = date_done;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
