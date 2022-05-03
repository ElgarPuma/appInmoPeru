package com.upc.inmoperu.ui.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InicioTask extends AsyncTask<String,Void,String> {

    private Context httpContext;
    public String linkRequestAPI = "";

    public InicioTask(Context ctx, String linkAPI){
        this.httpContext = ctx;
        this.linkRequestAPI = linkAPI;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
    }


    @Override
    protected String  doInBackground(String... params) {
        String result=null;
        String inputLine;
        String wsURL = linkRequestAPI;
        URL url = null;

        try {
            URL urls = new URL(wsURL);
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            conn.setReadTimeout(150000); //milliseconds
            conn.setConnectTimeout(15000); // milliseconds
            conn.setRequestMethod("GET");

            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");

                }
                result = sb.toString();
            } else {

                return "error";
            }


        } catch (Exception e) {
            // System.out.println("exception in jsonparser class ........");
            e.printStackTrace();
            return "error";
        }

        return result;
    }

}
