package com.iit.appointmentmanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewEditActivity extends AppCompatActivity {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private DBHandler dbHandler;

    private Date appointmentDate;
    private List<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit);

        Intent intent = getIntent();
        String appointmentDate = intent.getStringExtra("appointmentDate");
        try {
            this.appointmentDate = shortDateFormat.parse(appointmentDate);
        } catch (ParseException e) {
            Log.e(CreateAppointmentFragment.class.toString(), e.getMessage(), e);
        }

        this.dbHandler = new DBHandler(this, null, null, 1);

        this.appointments = this.dbHandler.findAppointmentsByDate(this.appointmentDate);

        ListView listView = findViewById(R.id.appointment_list);
        listView.setAdapter(new CustomListAdapter());
    }

    class CustomListAdapter extends BaseAdapter {

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
            convertView = getLayoutInflater().inflate(R.layout.custom_list_item, null);

            Appointment appointment = appointments.get(position);

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
}
