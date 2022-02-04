package org.chskenya.covidapp.offlineRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.chskenya.covidapp.model.InitialData;

@Database(entities = {InitialData.class}, version = 1, exportSchema = false)
public abstract class InitialDataDB  extends RoomDatabase {
    private static final String DB_NAME = "initial_data_db";

    public abstract InitialDataDAO getInitialDataDAO();
    private static InitialDataDB initialDataDB;

    public static synchronized InitialDataDB getInstance(Context context) {
        if (initialDataDB == null) {
            initialDataDB = buildDatabaseInstance(context);
        }
        return initialDataDB;
    }

    private static InitialDataDB buildDatabaseInstance (Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), InitialDataDB.class, DB_NAME )
                .allowMainThreadQueries().build();
    }

    public void  cleanUp() {
        initialDataDB = null;
    }
}
