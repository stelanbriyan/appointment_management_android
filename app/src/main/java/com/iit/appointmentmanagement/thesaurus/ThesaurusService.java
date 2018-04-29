package com.iit.appointmentmanagement.thesaurus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iit.appointmentmanagement.entity.Thesaurus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class ThesaurusService {
    private final String api_key = "X2EEweCCkZnvu1NECRt6";
    private String url = "http://thesaurus.altervista.org/thesaurus/v1?word=&{word}&language=en_US&key=&{api_key}&output=json";

    public ThesaurusService() {
        url = url.replace("&{api_key}", api_key);
    }

    public String getUrl() {
        return url;
    }
}
