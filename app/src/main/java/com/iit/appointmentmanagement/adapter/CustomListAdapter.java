package com.iit.appointmentmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iit.appointmentmanagement.R;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private List<Appointment> appointments;
    private DateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");
    private LayoutInflater layoutInflater;


    public CustomListAdapter(List<Appointment> appointments, LayoutInflater layoutInflater) {
        this.appointments = appointments;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.custom_list_item, null);

        Appointment appointment = appointments.get(position);


        if (appointment.getTitle() != null && !appointment.getTitle().isEmpty()) {
            TextView icon = convertView.findViewById(R.id.letterItem);
            icon.setText(String.valueOf(appointment.getTitle().toCharArray()[0]).toUpperCase());
        }

        String detail = "";

//            if (appointment.getAppointmentDate() != null) {
//                detail = detail.concat("").concat(shortDateFormat.format(appointment.getAppointmentDate()));
//            }

        if (appointment.getTime() != null) {
            detail = detail.concat("").concat(shortTimeFormat.format(appointment.getTime()).concat("  - "));
        }

        TextView titleItem = convertView.findViewById(R.id.titleItem);
        titleItem.setText(appointment.getTitle());

        TextView detailItem = convertView.findViewById(R.id.detailItem);
        detailItem.setText(detail.concat("Detail: ").concat(appointment.getDetail()));

        return convertView;
    }
}
