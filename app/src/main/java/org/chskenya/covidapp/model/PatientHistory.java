package org.chskenya.covidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PatientHistory implements Serializable {

    @SerializedName("id")
    @Expose
    Integer id;

    @SerializedName("date_taken")
    @Expose
    String date_taken;

    @SerializedName("places_travelled")
    @Expose
    String places_travelled;

    @SerializedName("contact_with_infected")
    @Expose
    String contact_with_infected;

    @SerializedName("vaccinated")
    @Expose
    String vaccinated;

    @SerializedName("first_dose")
    @Expose
    String first_dose;

    @SerializedName("travelled")
    @Expose
    String travelled;

    @SerializedName("first_dose_date")
    @Expose
    String first_dose_date;

    @SerializedName("contact_setting")
    @Expose
    String contact_setting;

    @SerializedName("second_dose")
    @Expose
    String second_dose;

    @SerializedName("second_dose_date")
    @Expose
    String second_dose_date;

    public String getContact_setting() {
        return contact_setting;
    }

    public String getTravelled() {
        return travelled;
    }

    public Integer getId() {
        return id;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public String getPlaces_traveled() {
        return places_travelled;
    }

    public String getContact_with_infected() {
        return contact_with_infected;
    }

    public String getVaccinated() {
        return vaccinated;
    }

    public String getFirst_dose() {
        return first_dose;
    }

    public String getFirst_dose_date() {
        return first_dose_date;
    }

    public String getSecond_dose() {
        return second_dose;
    }

    public String getSecond_dose_date() {
        return second_dose_date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public void setPlaces_traveled(String places_traveled) {
        this.places_travelled = places_traveled;
    }

    public void setContact_with_infected(String contact_with_infected) {
        this.contact_with_infected = contact_with_infected;
    }

    public void setVaccinated(String vaccinated) {
        this.vaccinated = vaccinated;
    }

    public void setFirst_dose(String first_dose) {
        this.first_dose = first_dose;
    }

    public void setFirst_dose_date(String first_dose_date) {
        this.first_dose_date = first_dose_date;
    }

    public void setSecond_dose(String second_dose) {
        this.second_dose = second_dose;
    }

    public void setSecond_dose_date(String second_dose_date) {
        this.second_dose_date = second_dose_date;
    }
}
