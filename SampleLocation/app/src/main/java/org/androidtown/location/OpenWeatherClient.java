package org.androidtown.location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenWeatherClient {
    final static String key = "3b6e9fe19495f162f7d11d79b3f8294a";

    public int getWeatherId(String lat, String lon) throws MalformedURLException {
        int weatherId = 0;
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather" +
                    "?appid=" + key + "&lat=" + lat + "&lon=" + lon);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject json = new JSONObject(getStringFromInputStream(in));
            weatherId = json.getJSONArray("weather").getJSONObject(0).getInt("id");
        }
        catch (MalformedURLException e) {
            System.err.println("Malformed URL");
            e.printStackTrace();
        }
        catch (JSONException e) {
            System.err.println("JSON parsing error");
            e.printStackTrace();
        }
        catch(IOException e) {
            System.err.println("URL Connection failed");
            e.printStackTrace();
        }

        return weatherId;
    }

    private static String getStringFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }

}
