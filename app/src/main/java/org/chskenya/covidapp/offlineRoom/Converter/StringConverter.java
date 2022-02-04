package org.chskenya.covidapp.offlineRoom.Converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class StringConverter {
    @TypeConverter
    public String fromList(String[] stringList) {
        if (stringList == null) {
            return (null);
        }
        Type type = new TypeToken<String[]>() {
        }.getType();
        return new Gson().toJson(stringList, type);
    }

    @TypeConverter
    public String[] toList(String string) {
        if (string == null) {
            return (null);
        }
        Type type = new TypeToken<String[]>() {
        }.getType();
        return new Gson().fromJson(string, type);
    }
}
