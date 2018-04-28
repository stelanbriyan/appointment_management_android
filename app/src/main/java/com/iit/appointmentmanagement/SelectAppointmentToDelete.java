package com.iit.appointmentmanagement;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SelectAppointmentToDelete extends AppCompatActivity {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private Date appointmentDate;

    private DBHandler dbHandler;

    private List<Appointment> appointments;
    String countryList[] = {"India", "China", "australia", "Portugle", "America", "NewZealand"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_appointment_to_delete);

        Intent intent = getIntent();
        String appointmentDate = intent.getStringExtra("appointmentDate");
        try {
            this.appointmentDate = shortDateFormat.parse(appointmentDate);
        } catch (ParseException e) {
            Log.e(CreateAppointmentFragment.class.toString(), e.getMessage(), e);
        }

        this.dbHandler = new DBHandler(this, null, null, 1);

        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Couldn't find any matches", Toast.LENGTH_SHORT).show();
            }
        });


//        this.appointments = dbHandler.findAppointmentsByDate(this.appointmentDate);
//
//        ListView listView = findViewById(R.id.searchResultList);
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_select_appointment_to_delete, R.id.deleteTextValue, countryList);
//        listView.setAdapter(arrayAdapter);
    }
}
