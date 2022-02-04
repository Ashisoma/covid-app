package org.chskenya.covidapp.offlineRoom.Converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.chskenya.covidapp.model.PatientContact;

import java.lang.reflect.Type;

public class PatientContactConverter {
    @TypeConverter
    public String fromList(PatientContact[] patientContacts) {
        if (patientContacts == null) {
            return (null);
        }
        Type type = new TypeToken<PatientContact[]>() {
        }.getType();
        return new Gson().toJson(patientContacts, type);
    }

    @TypeConverter
    public PatientContact[] toList(String patientContact) {
        if (patientContact == null) {
            return (null);
        }
        Type type = new TypeToken<PatientContact[]>() {
        }.getType();
        return new Gson().fromJson(patientContact, type);
    }
}
