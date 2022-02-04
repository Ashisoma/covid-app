package org.chskenya.covidapp.offlineRoom.Converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.chskenya.covidapp.model.LabTestType;

import java.lang.reflect.Type;

public class LabTestTypeConverter {
    @TypeConverter
    public String fromList(LabTestType[] labTestTypes) {
        if (labTestTypes == null) {
            return (null);
        }
        Type type = new TypeToken<LabTestType[]>() {
        }.getType();
        return new Gson().toJson(labTestTypes, type);
    }

    @TypeConverter
    public LabTestType[] toList(String labTestType) {
        if (labTestType == null) {
            return (null);
        }
        Type type = new TypeToken<LabTestType[]>() {
        }.getType();
        return new Gson().fromJson(labTestType, type);
    }
}
