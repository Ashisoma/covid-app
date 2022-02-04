package org.chskenya.covidapp.offlineRoom.Converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.chskenya.covidapp.model.County;

import java.lang.reflect.Type;

public class CountyConverter {
    @TypeConverter
    public String fromList(County[] counties) {
        if (counties == null) {
            return (null);
        }
        Type type = new TypeToken<County[]>() {
        }.getType();
        return new Gson().toJson(counties, type);
    }

    @TypeConverter
    public County[] toList(String county) {
        if (county == null) {
            return (null);
        }
        Type type = new TypeToken<County[]>() {
        }.getType();
        return new Gson().fromJson(county, type);
    }
}
