package org.chskenya.covidapp.util;

import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.model.User;

public enum SessionManager {
    INSTANCE;
    private User user;
    private Patient patient;
    private PatientContact patientContact;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public PatientContact getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(PatientContact patientContact) {
        this.patientContact = patientContact;
    }
}
