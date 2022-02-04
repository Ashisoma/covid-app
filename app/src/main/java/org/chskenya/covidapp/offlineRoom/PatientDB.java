package org.chskenya.covidapp.offlineRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.chskenya.covidapp.model.Patient;

@Database(entities = {Patient.class}, version = 1, exportSchema = false)
public abstract class PatientDB extends RoomDatabase {

    private static final String DB_NAME = "countries_db";

    public abstract PatientDAO getPatientListDAO();
    private static PatientDB patientDB;

    public static synchronized PatientDB getInstance(Context context) {
        if (patientDB == null) {
            patientDB = buildDatabaseInstance(context);
        }
        return patientDB;
    }

    private static PatientDB buildDatabaseInstance (Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), PatientDB.class, DB_NAME )
                .allowMainThreadQueries().build();
    }

    public void  cleanUp() {
        patientDB = null;
    }
}
