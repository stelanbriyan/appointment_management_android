package com.iit.appointmentmanagement;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class HomeFragment extends Fragment {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String appointmentDate;

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
                intent.putExtra("appointmentDate" , appointmentDate );
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }
}
