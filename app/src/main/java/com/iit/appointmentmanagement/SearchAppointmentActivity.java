package com.iit.appointmentmanagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.iit.appointmentmanagement.adapter.CustomListAdapter;
import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stelan Briyan
 */
public class SearchAppointmentActivity extends AppCompatActivity {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private DBHandler dbHandler;
    private ListView listView;

    private List<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);

        this.dbHandler = new DBHandler(this, null, null, 1);

        this.appointments = this.dbHandler.findAppointments();

        final EditText searchText = findViewById(R.id.searchTextValue);

        listView = findViewById(R.id.searchResultList);
        listView.setAdapter(new CustomListAdapter(appointments, getLayoutInflater()));

        Button searchButton = findViewById(R.id.searchABtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Find all appointments.
                 */
                appointments = dbHandler.findAppointments();
                filter(searchText.getText().toString());

            }
        });
    }

    /**
     * String text filter.
     *
     * @param searchText String
     */
    public void filter(String searchText) {
        List<Appointment> filterAppointments = new ArrayList<>();

        if (this.appointments != null && searchText != null) {
            for (Appointment appointment : this.appointments) {


                /**
                 * Check the matching
                 */
                if (appointment.getTitle() != null && appointment.getTitle().contains(searchText)) {
                    filterAppointments.add(appointment);
                } else if (appointment.getDetail() != null && appointment.getDetail().contains(searchText)) {
                    filterAppointments.add(appointment);
                } else if (appointment.getAppointmentDate() != null) {
                    String format = shortDateFormat.format(appointment.getAppointmentDate());
                    if (format.contains(searchText)) {
                        filterAppointments.add(appointment);
                    }
                }

            }
        }

        this.appointments = filterAppointments;

        /**
         * Update the list view.
         */
        listView.setAdapter(new CustomListAdapter(appointments, getLayoutInflater()));
    }
}
