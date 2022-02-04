package org.chskenya.covidapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chskenya.covidapp.R;
import org.chskenya.covidapp.activity.ContactTracingActivity;
import org.chskenya.covidapp.model.PatientContact;

import java.util.ArrayList;
import java.util.List;

public class PatientContactListViewAdapter extends RecyclerView.Adapter<PatientContactListViewAdapter.ViewHolder>{
    private List<PatientContact> patientContactList = new ArrayList<>();
    private final Context context;

    public PatientContactListViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PatientContactListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_patient_contacts, parent, false);
        return new PatientContactListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientContactListViewAdapter.ViewHolder holder, int position) {
        PatientContact contact = patientContactList.get(position);
        holder.tvName.setText(contact.getFirstName() + " " + contact.getMiddleName() + " " + contact.getSurname());
        holder.tvPhone.setText(contact.getPhoneNumber());
        holder.contactinfo.setOnClickListener(v -> {
            ((Activity) context).runOnUiThread(() -> {
                Intent intent = new Intent(context, ContactTracingActivity.class);
                intent.putExtra("contact", contact);
                context.startActivity(intent);
            });
        });
    }

    @Override
    public int getItemCount() {
        return patientContactList.size();
    }

    public void setPatientContactList(List<PatientContact> patientContactList) {
        this.patientContactList = patientContactList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvPhone;
        private final RelativeLayout contactinfo;

        private ViewHolder(View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            contactinfo = itemView.findViewById(R.id.contactinfo);
        }
    }
}
