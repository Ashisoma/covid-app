package org.chskenya.covidapp.offlineRoom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.chskenya.covidapp.model.InitialData;

@Dao
public interface InitialDataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insertData (InitialData initialData);

    @Query("SELECT * FROM initialData")
    InitialData getInitialData();

    @Update(onConflict = OnConflictStrategy.REPLACE) void updateInitialData (InitialData initialData);

    @Delete
    void deleteInitialData(InitialData initialData);
}
