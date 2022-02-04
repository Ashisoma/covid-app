package org.chskenya.covidapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.chskenya.covidapp.offlineRoom.Converter.PatientContactConverter;

import java.io.Serializable;

@Entity(tableName  = "patients") public class Patient implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("secondName")
    @Expose
    private String secondName;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("facility")
    @Expose
    private String facility;
    @SerializedName("nationalID")
    @Expose
    private String nationalID;
    @SerializedName("guardianID")
    @Expose
    private String guardianID;
    @SerializedName("guardianName")
    @Expose
    private String guardianName;
    @SerializedName("citizenship")
    @Expose
    private String citizenship;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("maritalStatus")
    @Expose
    private String maritalStatus;
    @SerializedName("educationLevel")
    @Expose
    private String educationLevel;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("alive")
    @Expose
    private String alive;
    @SerializedName("caseLocation")
    @Expose
    private String caseLocation;
    @SerializedName("investigatingFacility")
    @Expose
    private String investigatingFacility;
    @SerializedName("county")
    @Expose
    private int county;
    @SerializedName("subCounty")
    @Expose
    private int subCounty;
    @SerializedName("nokName")
    @Expose
    private String nokName;
    @SerializedName("nokPhone")
    @Expose
    private String nokPhone;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @TypeConverters(PatientContactConverter.class)
    @SerializedName("contacts")
    @Expose
    private PatientContact[] patientContacts = null;

    private int active = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getGuardianID() {
        return guardianID;
    }

    public void setGuardianID(String guardianID) {
        this.guardianID = guardianID;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAlive() {
        return alive;
    }

    public void setAlive(String alive) {
        this.alive = alive;
    }

    public String getCaseLocation() {
        return caseLocation;
    }

    public void setCaseLocation(String caseLocation) {
        this.caseLocation = caseLocation;
    }

    public String getInvestigatingFacility() {
        return investigatingFacility;
    }

    public void setInvestigatingFacility(String investigatingFacility) {
        this.investigatingFacility = investigatingFacility;
    }

    public int getCounty() {
        return county;
    }

    public void setCounty(int county) {
        this.county = county;
    }

    public int getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(int subCounty) {
        this.subCounty = subCounty;
    }

    public String getNokName() {
        return nokName;
    }

    public void setNokName(String nokName) {
        this.nokName = nokName;
    }

    public String getNokPhone() {
        return nokPhone;
    }

    public void setNokPhone(String nokPhone) {
        this.nokPhone = nokPhone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public PatientContact[] getPatientContacts() {
        return patientContacts;
    }

    public void setPatientContacts(PatientContact[] patientContacts) {
        this.patientContacts = patientContacts;
    }
}
