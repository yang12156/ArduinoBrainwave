package zolup.weather2;


import android.os.AsyncTask;

import java.net.MalformedURLException;

public class OpenWeatherTask extends AsyncTask<String, Void, Integer> {
    @Override
    protected Integer doInBackground(String... params) {
        OpenWeather client = new OpenWeather();
        String lat = params[0];
        String lon = params[1];
        int weatherId = 0;
        try {
            weatherId = client.getWeatherId(lat,lon);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return weatherId;
    }
}
