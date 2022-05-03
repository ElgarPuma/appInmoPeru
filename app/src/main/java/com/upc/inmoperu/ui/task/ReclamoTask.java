package com.upc.inmoperu.ui.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ReclamoTask extends AsyncTask<String,Void,String> {

    private Context httpContext;
    public String linkRequestAPI = "";


    public ReclamoTask(Context ctx, String linkAPI){
        this.httpContext = ctx;
        this.linkRequestAPI = linkAPI;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
    }


    @Override
    protected String  doInBackground(String... params) {
        String result = null;
        String wsURL = linkRequestAPI;
        URL url = null;
        try {
            url = new URL(wsURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            JSONObject parameterPost = new JSONObject();
            parameterPost.put("userid", params[0]);
            parameterPost.put("title", params[1]);
            parameterPost.put("asunto", params[2]);
            parameterPost.put("description", params[3]);
            parameterPost.put("img1", params[4]);
            parameterPost.put("img2", params[5]);
            parameterPost.put("img3", params[6]);
            parameterPost.put("state", 1);

            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(parameterPost.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String linea = "";
                while ((linea = in.readLine()) != null) {
                    sb.append(linea);
                    break;
                }
                in.close();
                result = sb.toString();
                //System.out.println(result);
            } else {
                result = new String("Error: " + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
}
