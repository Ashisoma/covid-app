package org.chskenya.covidapp.offlineRoom.Converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.chskenya.covidapp.model.Facility;

import java.lang.reflect.Type;

public class FacilityConverter {
    @TypeConverter
    public String fromList(Facility[] facilities) {
        if (facilities == null) {
            return (null);
        }
        Type type = new TypeToken<Facility[]>() {}.getType();
        return new Gson().toJson(facilities, type);
    }

    @TypeConverter
    public Facility[] toList(String facility) {
        if (facility == null) {
            return (null);
        }
        Type type = new TypeToken<Facility[]>(){}.getType();
        return new Gson().fromJson(facility, type);
    }
}
