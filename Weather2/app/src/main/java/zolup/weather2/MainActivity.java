package zolup.weather2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    String latitude, longitude;
    EditText latEditText, lonEditText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latEditText = (EditText) findViewById(R.id.latEditText);
        lonEditText = (EditText) findViewById(R.id.lonEditText);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void onClickStartBtn (View v) {
        latitude = latEditText.getText().toString();
        longitude = lonEditText.getText().toString();
        OpenWeatherTask openWeatherTask = new OpenWeatherTask();
        try {
            int weatherId = openWeatherTask.execute(latitude, longitude).get();
            textView.setText(String.valueOf(weatherId));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
