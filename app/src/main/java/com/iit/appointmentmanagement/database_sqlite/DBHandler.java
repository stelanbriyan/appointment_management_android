package com.iit.appointmentmanagement.database_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iit.appointmentmanagement.CreateAppointmentFragment;
import com.iit.appointmentmanagement.entity.Appointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "appointment_management.db";

    private String tableName = "APPOINTMENTS";
    private String id = "id", date = "date", time = "time", title = "title", detail = "detail", createdDate = "createdDate";

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + tableName + " (" +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                date + " DATE," +
                time + " DATETIME, " +
                title + " TEXT," +
                detail + " TEXT," +
                createdDate + " DATETIME)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public Appointment getAppointmentByTitleAndDate(String title, Date appointmentDate) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = " SELECT * FROM " + tableName + " WHERE "
                + this.date + "= '" + shortDateFormat.format(appointmentDate) + "'" + " AND " + this.title
                + "= '" + title + "';";

        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex("title")) != null) {
                return writeCursorToAppointment(cursor);
            }
            cursor.moveToNext();
        }
        return null;
    }

    private Appointment writeCursorToAppointment(Cursor cursor) {
        Appointment appointment = new Appointment();
        appointment.setId(cursor.getInt(cursor.getColumnIndex(this.id)));
        appointment.setTitle(cursor.getString(cursor.getColumnIndex(this.title)));
        appointment.setDetail(cursor.getString(cursor.getColumnIndex(this.detail)));

        String date = cursor.getString(cursor.getColumnIndex(this.date));
        if(date != null && !date.isEmpty()){
            try {
                appointment.setAppointmentDate(shortDateFormat.parse(date));
            } catch (ParseException e) {
                Log.e(DBHandler.class.toString(), e.getMessage(), e);
            }
        }

        String createdDate = cursor.getString(cursor.getColumnIndex(this.createdDate));
        if(date != null && !date.isEmpty()){
            try {
                appointment.setCreatedDate(shortDateFormat.parse(createdDate));
            } catch (ParseException e) {
                Log.e(DBHandler.class.toString(), e.getMessage(), e);
            }
        }
        return appointment;
    }

    public boolean createAppointment(Appointment appointment) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        if (appointment.getCreatedDate() != null) {
            contentValues.put(date, this.shortDateFormat.format(appointment.getCreatedDate()));
        }
        if (appointment.getTime() != null) {
            contentValues.put(time, this.timeFormat.format(appointment.getTime()));
        }
        contentValues.put(title, appointment.getTitle());
        contentValues.put(detail, appointment.getDetail());

        if (appointment.getCreatedDate() != null) {
            contentValues.put(createdDate, this.dateFormat.format(appointment.getCreatedDate()));
        }

        db.insert(tableName, null, contentValues);
        db.close();
        return true;
    }

    public List<Appointment> findAppointments() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + tableName;

        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();

        List<Appointment> appointments = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex("title")) != null) {
                appointments.add(writeCursorToAppointment(cursor));
            }
            cursor.moveToNext();
        }
        return appointments;
    }
}
