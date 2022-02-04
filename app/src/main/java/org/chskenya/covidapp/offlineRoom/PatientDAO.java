package org.chskenya.covidapp.offlineRoom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.chskenya.covidapp.model.Patient;

import java.util.List;

@Dao
public interface PatientDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insertPatients (Patient patient);

    @Query("SELECT * FROM patients")
    List<Patient> getAllPatients();

    @Query("SELECT * FROM patients where id LIKE :id")
    Patient getPatient(int id);

    @Query("SELECT * FROM patients where phone LIKE :searchString OR nationalID LIKE :searchString OR guardianID LIKE :searchString")
    List<Patient> searchPatients(String searchString);

    @Update(onConflict = OnConflictStrategy.REPLACE) void updatePatient (Patient patient);

    @Query("DELETE FROM patients")
    void deletePatients();
}
