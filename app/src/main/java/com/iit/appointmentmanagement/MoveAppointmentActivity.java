package com.iit.appointmentmanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MoveAppointmentActivity extends AppCompatActivity {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private Date appointmentDate;

    private DBHandler dbHandler;
    private ListView listView;

    private List<Appointment> appointments;
    String appointmentArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_appointment);

        Intent intent = getIntent();
        String appointmentDate = intent.getStringExtra("appointmentDate");
        try {
            this.appointmentDate = shortDateFormat.parse(appointmentDate);
        } catch (ParseException e) {
            Log.e(CreateAppointmentFragment.class.toString(), e.getMessage(), e);
        }

        this.dbHandler = new DBHandler(this, null, null, 1);


        this.listView = findViewById(R.id.searchResultList);
        this.loadItems();

        final EditText moveTextValue = findViewById(R.id.moveTextValue);

        Button moveBtn = findViewById(R.id.moveBtn);
        moveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void loadItems() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);
        numberFormat.setGroupingUsed(false);

        this.appointments = dbHandler.findAppointmentsByDate(this.appointmentDate);
        appointmentArray = new String[this.appointments.size()];

        for (int i = 0; i < this.appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            String detail = numberFormat.format((i + 1)) + ". ";

            if (appointment.getTime() != null) {
                detail = detail.concat(shortTimeFormat.format(appointment.getTime()));
            }
            appointmentArray[i] = detail.concat(" ").concat(appointment.getTitle());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_delete_list_item, R.id.letterItemLine, appointmentArray);
        listView.setAdapter(arrayAdapter);
    }
}
