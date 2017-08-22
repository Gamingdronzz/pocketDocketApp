package com.example.hp.pocket_docket.httpRequestProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by hp on 21-04-2017.
 */

public class HTTPRequestProcessor {

    private String jsonResponseString;
    private StringBuilder sb;

    // This method will process POST request and  return a response String
    public String pOSTRequestProcessor(String jsonString, String requestURL) {
        sb = new StringBuilder();
        try {
            // Sending data to API
            URL url = new URL(requestURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setReadTimeout(15000); // Timeout on waiting to read data
            httpURLConnection.setConnectTimeout(15000); //Timeout in making the initial connection
            OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
            out.write(jsonString); // Transmit data by writing to the stream returned by getOutputStream()
            out.flush();
            out.close();
            // Read the response
            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String responseData = br.readLine();
            while (responseData != null) {
                sb.append(responseData);
                responseData = br.readLine();
            }
             httpURLConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonResponseString = sb.toString();
        return jsonResponseString;
    }

    // This method will process http GET request and return json response string
    public String gETRequestProcessor(String requestURL) {
        sb = new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Length", "0");
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String responseData = br.readLine();
            while (responseData != null) {
                sb.append(responseData);
                responseData = br.readLine();
            }
            br.close();
            urlConnection.disconnect();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonResponseString = sb.toString();
        return jsonResponseString;
    }

}
