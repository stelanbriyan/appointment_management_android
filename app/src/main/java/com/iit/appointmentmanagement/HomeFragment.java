package com.iit.appointmentmanagement;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.iit.appointmentmanagement.database_sqlite.DBHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Stelan Briyan
 */
public class HomeFragment extends Fragment {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String appointmentDate;

    private DBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home,
                container, false);

        this.appointmentDate = shortDateFormat.format(new Date());

        CalendarView calendarView = rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                appointmentDate = shortDateFormat.format(new GregorianCalendar(year, month, dayOfMonth).getTime());
            }
        });

        Button createAppointmentBtn = rootView.findViewById(R.id.createAppointmentBtn);
        createAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAppointmentActivity.class);
                intent.putExtra("appointmentDate", appointmentDate);
                getActivity().startActivity(intent);
            }
        });

        Button deleteBtn = rootView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeletePopup();
            }
        });

        Button viewEditBtn = rootView.findViewById(R.id.viewEditBtn);
        viewEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewEditActivity.class);
                intent.putExtra("appointmentDate", appointmentDate);
                getActivity().startActivity(intent);
            }
        });

        Button searchBtn = rootView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchAppointmentActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Button moveAppointmentBtn = rootView.findViewById(R.id.moveAppointmentBtn);
        moveAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoveAppointmentActivity.class);
                intent.putExtra("appointmentDate", appointmentDate);
                getActivity().startActivity(intent);
            }
        });

        this.dbHandler = new DBHandler(getActivity(), null, null, 1);
        return rootView;
    }

    public void openDeletePopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_popup);
        dialog.setTitle("Title...");

        Button select_delete_btn = dialog.findViewById(R.id.select_delete_btn);
        select_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectAppointmentToDelete.class);
                intent.putExtra("appointmentDate", appointmentDate);
                getActivity().startActivity(intent);
                dialog.dismiss();
            }
        });

        Button delete_all_btn = dialog.findViewById(R.id.delete_all_btn);
        delete_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteAppointmentByDate(appointmentDate);
                Toast.makeText(getActivity(), "All appointments deleted for selected appointment date.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
