package com.manage.hospital.hmapp.ui.patient;

import android.os.AsyncTask;
import android.util.Log;

import com.manage.hospital.hmapp.Extras.Interface.RestCallbackHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;


public class MocRestClient extends AsyncTask<Void, Void, Void> {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private String id, endPoint, methodName, body, responseBody;
    private int responseCode;
    private RestCallbackHandler handler;

    public MocRestClient(String idIn, String urlIn, String methodNameIn, String bodyIn, RestCallbackHandler handlerIn) {
        id = idIn;
        endPoint = urlIn;
        methodName = methodNameIn;
        body = bodyIn;
        handler = handlerIn;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(endPoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");

            if(!methodName.equals(GET) && !methodName.equals(POST) && !methodName.equals(PUT) && !methodName.equals(DELETE))
                throw new UnsupportedOperationException();
            urlConnection.setRequestMethod(methodName);
            if(methodName.equals(POST) || methodName.equals(PUT)) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(body);
                writer.close();
            } else {
                urlConnection.setDoOutput(false);
            }

            responseCode = urlConnection.getResponseCode();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder buffer = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine);
                buffer.append("\n");
            }

            responseBody = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        handler.handleResponse(this);
    }

    public String getId() { return id; }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
