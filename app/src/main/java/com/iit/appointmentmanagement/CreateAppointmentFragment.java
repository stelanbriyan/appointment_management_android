package com.iit.appointmentmanagement;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iit.appointmentmanagement.database_sqlite.DBHandler;
import com.iit.appointmentmanagement.entity.Appointment;
import com.iit.appointmentmanagement.entity.Thesaurus;
import com.iit.appointmentmanagement.thesaurus.ThesaurusService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Stelan Briyan
 */
public class CreateAppointmentFragment extends Fragment {
    private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private Date appointmentDate;
    private EditText titleTxt, timeTxt, detailTxt, thesaurusTxt;
    private ListView thesaurusList;
    private DBHandler dbHandler;

    private String[] thesaurusArray;

    private ThesaurusService thesaurusService;

    private String url;

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

        this.thesaurusTxt = rootView.findViewById(R.id.thesaurusTxt);
        Button thesaurusBtn = rootView.findViewById(R.id.thesaurusBtn);

        this.thesaurusService = new ThesaurusService();
        thesaurusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openThesaurus(thesaurusTxt.getText().toString());
                } catch (IOException e) {

                }
            }
        });

        return rootView;
    }

    /**
     * Open external popup window and show thesaurus on it.
     *
     * @param word
     * @throws IOException
     */
    public void openThesaurus(String word) throws IOException {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.thesaurus_popup);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(getActivity().getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT; // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        thesaurusList = dialog.findViewById(R.id.thesaurusList);

        if (word.isEmpty()) {
            Toast.makeText(getActivity(), "Type here something!", Toast.LENGTH_SHORT).show();
        } else {
            if (isNetworkAvailable()) {
                this.url = this.thesaurusService.getUrl();
                this.url = this.url.replace("&{word}", word);
                new ThesaurusCaller().execute();
                dialog.show();
            } else {
                Toast.makeText(getActivity(), "Internet is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check internet connection is available or not.
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    /**
     * This class is running as a separate thread. this is for calling to web service.
     * AsyncTask
     */
    private class ThesaurusCaller extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                downloadData(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_thersaurus_list_item, R.id.thesaurusTextItem, thesaurusArray);
            thesaurusList.setAdapter(arrayAdapter);
        }
    }

    /**
     * Create a connection with web service. and store data in local array.
     *
     * @param url
     * @throws IOException
     */
    public void downloadData(String url) throws IOException {
        URL myurl = new URL(url);
        URLConnection con = myurl.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();


        /**
         * Jackson data bind.
         */
        ObjectMapper mapper = new ObjectMapper();

        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(response.toString());
        JsonNode jsonNode = mapper.readTree(parser);

        JsonNode node = jsonNode.get("response");

        List<String> words = new ArrayList<>();
        for (JsonNode jNode : node) {
            JsonNode listNode = jNode.get("list");
            Thesaurus thesaurus = mapper.readValue(listNode.toString(), Thesaurus.class);

            String[] synonyms = thesaurus.getSynonyms().split("[|]");
            for (String syn : synonyms) {
                words.add(syn.concat(" ").concat(thesaurus.getCategory()));
            }
        }

        thesaurusArray = new String[words.size()];

        for (int i = 0; i < thesaurusArray.length; i++) {
            thesaurusArray[i] = words.get(i);
        }
    }
}
