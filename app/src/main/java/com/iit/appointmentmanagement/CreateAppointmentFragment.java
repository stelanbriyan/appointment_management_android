package com.iit.appointmentmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Stelan Briyan
 */
public class CreateAppointmentFragment extends Fragment {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private Date appointmentDate;
    private EditText titleTxt, timeTxt, detailTxt;

    private DBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_appointment,
                container, false);

        Intent intent = getActivity().getIntent();
        String appointmentDate = intent.getStringExtra("appointmentDate");
        try {
            this.appointmentDate = shortDateFormat.parse(appointmentDate);
        } catch (ParseException e) {
            Log.e(CreateAppointmentFragment.class.toString(), e.getMessage(), e);
        }

        this.titleTxt = rootView.findViewById(R.id.appointmentTitleTxt);
        this.timeTxt = rootView.findViewById(R.id.appointmentTimeTxt);
        this.detailTxt = rootView.findViewById(R.id.appointmentDetailTxt);

        this.dbHandler = new DBHandler(rootView.getContext(), null, null, 1);

        Button saveBtn = rootView.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });

        return rootView;
    }

    public void saveAppointment() {
        String title = this.titleTxt.getText().toString();
        Appointment appointmentByTitleAndDate = this.dbHandler.getAppointmentByTitleAndDate(title, this.appointmentDate);

        if (appointmentByTitleAndDate == null) {
            boolean valid = true;

            if (TextUtils.isEmpty(this.timeTxt.getText().toString())) {
                this.timeTxt.setError("Please set a time for the appointment.");
                valid = false;
            }
            if (TextUtils.isEmpty(this.titleTxt.getText().toString())) {
                this.titleTxt.setError("Title field is empty.");
                valid = false;
            }

            if (valid) {
                Appointment appointment = new Appointment();
                try {
                    appointment.setTime(shortTimeFormat.parse(this.timeTxt.getText().toString()));
                } catch (ParseException e) {
                    Log.e(CreateAppointmentFragment.class.toString(), e.getMessage(), e);
                }

                appointment.setDetail(this.detailTxt.getText().toString());
                appointment.setTitle(this.titleTxt.getText().toString());
                appointment.setAppointmentDate(this.appointmentDate);
                appointment.setCreatedDate(new Date());

                dbHandler.createAppointment(appointment);

//                Intent intent = new Intent(getActivity(), CreateAppointmentActivity.class);
//                getActivity().startActivity(intent);
                showDialog("Appointment '" + title + "' on " + shortDateFormat.format(this.appointmentDate) + " was created successfully.");
            }
        } else {
            showDialog("Appointment " + title + " already exists, please choose a different event title");
        }
    }

    /**
     * This function creates a dialog box which takes
     *
     * @param message String
     */
    public void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
