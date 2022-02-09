package org.chskenya.covidapp.retrofit;

import androidx.annotation.Nullable;

import org.chskenya.covidapp.model.InitialData;
import org.chskenya.covidapp.model.Lab;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.model.PatientHistory;
import org.chskenya.covidapp.model.Radiology;
import org.chskenya.covidapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClient {
    @GET("getInitialData")
    Call<InitialData> getInitialData();

    @POST("app_login")
    @FormUrlEncoded
    Call<User> login(@Field("phone") String phone, @Field("password") String password);

    @POST("app_register")
    @FormUrlEncoded
    Call<User[]> registerUser(@Field("phone") String phone,
                              @Field("password") String password);

    @GET("verify_token")
    Call<User> verifyToken();

    @POST("register_patient")
    @FormUrlEncoded
    Call<Patient> registerPatient(@Field("firstName") String firstName, @Field("secondName") String secondName,
                                  @Field("surname") String surname, @Field("facility")@Nullable String facility,
                                  @Field("nationalID") String nationalID, @Field("guardianID") String guardianID,
                                  @Field("guardianName") String guardianName, @Field("phone") String phone,
                                  @Field("citizenship") String citizenship, @Field("gender") String gender,
                                  @Field("occupation") String occupation, @Field("maritalStatus") String maritalStatus,
                                  @Field("educationLevel") String educationLevel, @Field("dob") String dob,
                                  @Field("alive") String alive, @Field("caseLocation") String caseLocation,
                                  @Field("investigatingFacility") String investigatingFacility, @Field("county") int county,
                                  @Field("subCounty") int subCounty, @Field("nokName") String nokName,
                                  @Field("nokPhone") String nokPhone, @Field("department") String department);

    @POST("search_patient")
    @FormUrlEncoded
    Call<List<Patient>> searchPatient(@Field("searchParam") String searchParam);

    @POST("save_triage_data")
    @FormUrlEncoded
    Call<Patient> savePatientTriage(@Field("temperature") double temperature, @Field("weight") double weight,
                                    @Field("height") double height, @Field("spo2") double spo2,
                                    @Field("zscore") double zscore, @Field("cough")@Nullable String cough,
                                    @Field("difficulty_in_breathing")@Nullable String difficulty_in_breathing,
                                    @Field("weight_loss")@Nullable String weight_loss, @Field("patient_id") int patient_id,
                                    @Field("filled_by") int filled_by);

    @POST("save_screening_data")
    @FormUrlEncoded
    Call<Patient> savePatientScreeningData(@Field("patient_id") int patient_id, @Field("screened_by") int screened_by,
                                           @Field("fever_history") String fever_history, @Field("general_weakness") String general_weakness,
                                           @Field("cough") String cough, @Field("sore_throat") String sore_throat,
                                           @Field("runny_nose") String runny_nose, @Field("weight_loss") String weight_loss,
                                           @Field("night_sweats") String night_sweats, @Field("loss_of_taste") String loss_of_taste,
                                           @Field("loss_of_smell") String loss_of_smell, @Field("breathing_difficulty") String breathing_difficulty,
                                           @Field("diarrhoea") String diarrhoea, @Field("headache") String headache,
                                           @Field("irritability") String irritability, @Field("nausea") String nausea,
                                           @Field("shortness_of_breath") String shortness_of_breath, @Field("pain") String pain);

    @GET("get_patients")
    Call<List<Patient>> getPatients(@Query("mflCode") String mflCode);

    @GET("get_patient_contacts")
    Call<List<PatientContact>> getPatientContacts(@Query("patientId") int patientId);

    @POST("save_patient_contact")
    @FormUrlEncoded
    Call<List<PatientContact>> savePatientContact(@Field("firstName") String firstName, @Field("middleName") String middleName,
                                                  @Field("surname") String surname, @Field("phoneNumber") String phoneNumber,
                                                  @Field("patient_id") int patient_id);

    @POST("save_contact_tracing_data")
    @FormUrlEncoded
    Call<PatientContact> savePatientContactTracingData(@Field("contact_id") int contact_id, @Field("county") int county,
                                                       @Field("subcounty") int subcounty, @Field("contactTraced") String contactTraced,
                                                       @Field("tracingDate") String tracingDate, @Field("reported_by") int reported_by,
                                                       @Field("contactTested") String contactTested, @Field("testingDate") String testingDate,
                                                       @Field("testOutcome") String testOutcome);

    @POST("save_lab_request")
    @FormUrlEncoded
    Call<Patient> saveLabRequest(@Field("patient_id") int patient_id, @Field("investigator") int investigator,
                                 @Field("specimen_collected") String specimen_collected,
                                 @Field("reason_not_collected") String reason_not_collected,
                                 @Field("test_type") String test_type, @Field("specimen_type") String specimen_type,
                                 @Field("date_collected") String date_collected, @Field("date_sent_to_lab") String date_sent_to_lab);

    @POST("save_radiology_request")
    @FormUrlEncoded
    Call<Patient> saveRadiologyRequest(@Field("patient_id") int patient_id, @Field("date_requested") String date_requested,
                                       @Field("test_type") String test_type,
                                       @Field("date_done") String date_done, @Field("results") String results,
                                       @Field("comments") String comments, @Field("submitted_by") int submitted_by);

    @GET("get_patient_history")
    Call<PatientHistory> getPatientHistoryRequest(@Query("patientId") int patientId);

    @GET("get_radiology_request")
    Call<Radiology> getRadiologyData(@Query("patientId") int patientId);

    @GET("get_lab_results")
    Call<Lab> getLabResultsRequest(@Query("patientId") int patientId);


}