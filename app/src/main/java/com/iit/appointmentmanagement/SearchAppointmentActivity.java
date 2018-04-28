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

import java.util.List;

/**
 * @author Stelan Briyan
 */
public class SearchAppointmentActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    private List<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);

        this.dbHandler = new DBHandler(this, null, null, 1);

        this.appointments = this.dbHandler.findAppointments();

        final EditText searchText = findViewById(R.id.searchTextValue);

        final ListView listView = findViewById(R.id.searchResultList);
        listView.setAdapter(new CustomListAdapter(appointments, getLayoutInflater()));

        Button searchButton = findViewById(R.id.searchABtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointments = dbHandler.findAppointments(searchText.getText().toString());
                listView.setAdapter(new CustomListAdapter(appointments, getLayoutInflater()));
            }
        });
    }
}
