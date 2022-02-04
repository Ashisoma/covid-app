package org.chskenya.covidapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.activity.ContactLinelistActivity;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.model.PatientContact;
import org.chskenya.covidapp.offlineRoom.PatientDB;
import org.chskenya.covidapp.retrofit.AuthRetrofitApiClient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "AddContactBottomSheet";
    private Patient patient;
    private Context context;

    private View view;
    private Button btnCancel, submitButton;
    private EditText etSurname, etFName, etSecName, etPhone;

    public static AddContactBottomSheetFragment newInstance(Patient patient, Context context) {
        AddContactBottomSheetFragment fragment = new AddContactBottomSheetFragment();
        fragment.setCancelable(false);
        fragment.patient = patient;
        fragment.context = context;

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_contact_bottom_sheet, container, false);

        initViews();

        btnCancel.setOnClickListener(view2 -> dismiss());

        submitButton.setOnClickListener(this::submit);

        return view;
    }

    private void initViews() {
        etSurname = view.findViewById(R.id.etSurname);
        etFName = view.findViewById(R.id.etFName);
        etSecName = view.findViewById(R.id.etSecName);
        etPhone = view.findViewById(R.id.etPhone);
        btnCancel = view.findViewById(R.id.btnCancel);
        submitButton = view.findViewById(R.id.btnSubmit);
    }

    private void submit(View view) {
        String surname = etSurname.getText().toString();
        String firstname = etFName.getText().toString();
        String secondname = etSecName.getText().toString();
        String phoneNumber = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(surname)) {
            etSurname.setError("Enter Surname");
        }
        if (TextUtils.isEmpty(firstname)) {
            etFName.setError("Enter First Name");
        }
        if (TextUtils.isEmpty(secondname)) {
            etSecName.setError("Enter Second Name");
        } else if (TextUtils.isEmpty(phoneNumber)) {
            etPhone.setError("Enter contact phone number");
        } else {
            AuthRetrofitApiClient.getInstance(context)
                    .getAuthorizedApi()
                    .savePatientContact(firstname, secondname, surname, phoneNumber, patient.getId())
                    .enqueue(new Callback<List<PatientContact>>() {
                        @Override
                        public void onResponse(@NotNull Call<List<PatientContact>> call, @NotNull Response<List<PatientContact>> response) {
                            dismiss();
                            if (response.code() == 200) {
                                ((Activity) context).runOnUiThread(() -> {

                                    Log.d(TAG, "onResponse: here");
                                    PatientContact[] patientContactList = response.body().toArray(new PatientContact[0]);
                                    patient.setPatientContacts(patientContactList);
                                    PatientDB patientDB = PatientDB.getInstance(context);
                                    patientDB.getPatientListDAO().updatePatient(patient);

                                    Log.d(TAG, "onResponse: patient contacts" + Arrays.toString(patient.getPatientContacts()));

                                    ((ContactLinelistActivity) getContext()).populateRecyclerView(Arrays.asList(patientContactList));
                                });
                            } else if (response.code() == 401) {
                                ((Activity) context).runOnUiThread(() -> new SweetAlertDialog(context,
                                        SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getString(R.string.error_title))
                                        .setContentText(getResources().getString(R.string.session_expiry_error))
                                        .setConfirmButton(getString(R.string.log_in),
                                                sweetAlertDialog -> ((ContactLinelistActivity) getContext()).logout())
                                        .show()
                                );
                                Log.d(TAG, "onResponse: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<PatientContact>> call, @NotNull Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                            dismiss();

                            ((Activity) context).runOnUiThread(() -> new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.add_contact_bottom_sheet, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }


}
