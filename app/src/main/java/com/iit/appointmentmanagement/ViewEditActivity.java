package com.iit.appointmentmanagement;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.iit.appointmentmanagement.adapter.CustomListAdapter;
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
        listView.setAdapter(new CustomListAdapter(appointments, getLayoutInflater()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment appointment = openUpdateBox(appointments.get(position));

                if (appointment.getTitle() != null && !appointment.getTitle().isEmpty()) {
                    TextView icon = view.findViewById(R.id.letterItem);
                    icon.setText(String.valueOf(appointment.getTitle().toCharArray()[0]).toUpperCase());
                }

                String detail = "";
                if (appointment.getTime() != null) {
                    detail = detail.concat("").concat(shortTimeFormat.format(appointment.getTime()).concat("  - "));
                }

                TextView titleItem = view.findViewById(R.id.titleItem);
                titleItem.setText(appointment.getTitle());

                TextView detailItem = view.findViewById(R.id.detailItem);
                detailItem.setText(detail.concat("Detail: ").concat(appointment.getDetail()));
            }
        });
    }

    public Appointment openUpdateBox(final Appointment appointment) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_popup);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT; // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        final EditText titleText = dialog.findViewById(R.id.appointmentTitleUpdateTxt);
        titleText.setText(appointment.getTitle());

        final EditText detailText = dialog.findViewById(R.id.appointmentDetailUpdateTxt);
        detailText.setText(appointment.getDetail());

        final EditText timeText = dialog.findViewById(R.id.appointmentTimeUpdateTxt);
        if (appointment.getTime() != null) {
            timeText.setText(shortTimeFormat.format(appointment.getTime()));
        }

        Button dialogButton = dialog.findViewById(R.id.updateBtn);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (TextUtils.isEmpty(timeText.getText().toString())) {
                    timeText.setError("Please set a time for the appointment.");
                    valid = false;
                }

                if (TextUtils.isEmpty(titleText.getText().toString())) {
                    titleText.setError("Title field is empty.");
                    valid = false;
                }

                if (valid) {
                    try {
                        appointment.setTime(shortTimeFormat.parse(timeText.getText().toString()));
                    } catch (ParseException e) {
                        Log.e(CreateAppointmentFragment.class.toString(), e.getMessage(), e);
                    }

                    appointment.setDetail(detailText.getText().toString());
                    appointment.setTitle(titleText.getText().toString());
                    appointment.setCreatedDate(new Date());

                    dbHandler.updateAppointment(appointment);

                    showDialog("Appointment '" + titleText.getText().toString() + "' was updated successfully.");
                }

                dialog.dismiss();
            }
        });

        dialog.show();
        return appointment;
    }

    /**
     * This function creates a dialog box which takes
     *
     * @param message String
     */
    public void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
