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

/**
 * @author Stelan Briyan
 */
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

    /**
     * Return appointments from the db by searching title and appointment date.
     * @param title
     * @param appointmentDate
     * @return
     */
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

    /**
     * Convert cursor object to the appointment object.
     * @param cursor
     * @return
     */
    private Appointment writeCursorToAppointment(Cursor cursor) {
        Appointment appointment = new Appointment();
        appointment.setId(cursor.getInt(cursor.getColumnIndex(this.id)));
        appointment.setTitle(cursor.getString(cursor.getColumnIndex(this.title)));
        appointment.setDetail(cursor.getString(cursor.getColumnIndex(this.detail)));

        String date = cursor.getString(cursor.getColumnIndex(this.date));
        if (date != null && !date.isEmpty()) {
            try {
                appointment.setAppointmentDate(shortDateFormat.parse(date));
            } catch (ParseException e) {
                Log.e(DBHandler.class.toString(), e.getMessage(), e);
            }
        }

        String time = cursor.getString(cursor.getColumnIndex(this.time));
        if (time != null && !time.isEmpty()) {
            try {
                appointment.setTime(timeFormat.parse(time));
            } catch (ParseException e) {
                Log.e(DBHandler.class.toString(), e.getMessage(), e);
            }
        }

        String createdDate = cursor.getString(cursor.getColumnIndex(this.createdDate));
        if (date != null && !date.isEmpty()) {
            try {
                appointment.setCreatedDate(shortDateFormat.parse(createdDate));
            } catch (ParseException e) {
                Log.e(DBHandler.class.toString(), e.getMessage(), e);
            }
        }
        return appointment;
    }

    /**
     * Save new appointment to the db.
     * @param appointment
     * @return
     */
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

    /**
     * Find all appointments.
     * @return
     */
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

    /**
     * Find all appointments for appointment date.
     * @param appointmentDate
     * @return
     */
    public List<Appointment> findAppointmentsByDate(Date appointmentDate) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + tableName + " WHERE "
                + this.date + "= '" + shortDateFormat.format(appointmentDate) + "' ORDER BY " + time + " DESC";

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

    /**
     * update all appointment details.
     * @param appointment
     */
    public void updateAppointment(Appointment appointment) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        if (appointment.getAppointmentDate() != null) {
            contentValues.put(date, this.shortDateFormat.format(appointment.getAppointmentDate()));
        }
        if (appointment.getTime() != null) {
            contentValues.put(time, this.timeFormat.format(appointment.getTime()));
        }
        contentValues.put(title, appointment.getTitle());
        contentValues.put(detail, appointment.getDetail());

        if (appointment.getCreatedDate() != null) {
            contentValues.put(createdDate, this.dateFormat.format(appointment.getCreatedDate()));
        }

        db.update(tableName, contentValues, id + "=" + appointment.getId(), null);
        db.close();
    }

    public List<Appointment> findAppointments(String searchText) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + tableName + " WHERE "
                + this.date + " LIKE '%" + searchText + "%' OR "
                + this.title + " LIKE '%" + searchText + "%' OR "
                + this.detail + " LIKE '%" + searchText + "%' OR "
                + this.time + " LIKE '%" + searchText + "%' ORDER BY " + time + " DESC";

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

    public void deleteAppointmentById(Integer id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE " + this.id + "=\'" + id + "\'");
        db.close();
    }

    public void deleteAppointmentByDate(String appointmentDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE " + this.date + "=\'" + appointmentDate + "\'");
        db.close();
    }
}
