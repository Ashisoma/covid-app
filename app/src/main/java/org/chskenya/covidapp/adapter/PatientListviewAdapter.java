package org.chskenya.covidapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.activity.PatientHomePageActivity;
import org.chskenya.covidapp.model.Patient;
import org.chskenya.covidapp.offlineRoom.PatientDB;

import java.util.ArrayList;
import java.util.List;

public class PatientListviewAdapter extends RecyclerView.Adapter<PatientListviewAdapter.ViewHolder>{
    private static final String TAG = "PatientListviewAdapter";
    private List<Patient> patientList = new ArrayList<>();
    private Context context;

    public PatientListviewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PatientListviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_patient_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientListviewAdapter.ViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.tvName.setText(patient.getFirstName() + " " + patient.getSecondName() + " " + patient.getSurname());
        holder.tvPhone.setText(patient.getPhone());
        holder.btnCheckIn.setOnClickListener(v -> {
            patient.setActive(1);
            PatientDB patientDB = PatientDB.getInstance(context);
            patientDB.getPatientListDAO().updatePatient(patient);

            context.startActivity(new Intent(context, PatientHomePageActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvPhone;
        private final Button btnCheckIn;

        private ViewHolder(View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnCheckIn = itemView.findViewById(R.id.btnCheckIn);
        }
    }
}
