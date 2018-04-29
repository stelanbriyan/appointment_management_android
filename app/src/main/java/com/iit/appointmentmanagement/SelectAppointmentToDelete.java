package com.iit.appointmentmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SelectAppointmentToDelete extends AppCompatActivity {
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
        setContentView(R.layout.activity_select_appointment_to_delete);

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

        final EditText deleteTextValue = findViewById(R.id.deleteTextValue);

        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer id = null;
                for (int i = 0; i < appointments.size(); i++) {
                    if (String.valueOf(i + 1).equals(deleteTextValue.getText().toString())) {
                        Appointment appointment = appointments.get(i);
                        id = appointment.getId();
                    }
                }

                if (id != null) {
                    showConfirmDialog(id);
                } else {
                    Toast.makeText(getBaseContext(), "Couldn't find any matches", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showConfirmDialog(final Integer id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dbHandler.deleteAppointmentById(id);
                        Toast.makeText(getBaseContext(), "Selected appointment deleted successfully", Toast.LENGTH_SHORT).show();
                        loadItems();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /**
     * Load appointments to the custom list.
     */
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
